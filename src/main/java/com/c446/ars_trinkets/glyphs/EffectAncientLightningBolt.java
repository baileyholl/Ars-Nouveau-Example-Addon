package com.c446.ars_trinkets.glyphs;

import com.c446.ars_trinkets.Util;
import com.c446.ars_trinkets.entities.red_lightning.RedLightning;
import com.c446.ars_trinkets.registry.EntityRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBreak;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectAncientLightningBolt extends AbstractEffect implements IDamageEffect {
    public ModConfigSpec.DoubleValue AOE;

    public void addDamageConfig(ModConfigSpec.Builder builder, double defaultValue) {
        this.AOE = builder.defineInRange("aoe", defaultValue, 0.0F, Integer.MAX_VALUE);
    }

    public EffectAncientLightningBolt(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addAmpConfig(builder, 10D);
        addDamageConfig(builder, 40D);
    }



    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if (world instanceof ServerLevel serverLevel) {
            Vec3 pos = rayTraceResult.getLocation();

            RedLightning rlb = EntityRegistry.RED_LIGHTNING.get().create(serverLevel);
            assert rlb != null;
            rlb.setDamage(0);
            rlb.setPos(pos);
            rlb.setVisualOnly(true);
            serverLevel.addFreshEntity(rlb);

            double size = spellStats.getAoeMultiplier() * this.AOE.getAsDouble() + 3;
            double dmg = spellStats.getAmpMultiplier() * this.AMP_VALUE.getAsDouble() + this.DAMAGE.getAsDouble();

            serverLevel.getEntities(rlb, AABB.ofSize(pos, size * 2, size * 2, size * 2), (target) -> (target instanceof LivingEntity livingTarget && (rlb.getOwner() == null || rlb.getOwner().canAttack(livingTarget)))).forEach(target -> {
                double distance = target.distanceToSqr(pos);
                if (distance < size * size && Util.hasLineOfSight(serverLevel, pos.add(0, 2, 0), target.getBoundingBox().getCenter())) {
                    float finalDamage = (float) (dmg * (1 - distance / (size * size)));

                    attemptDamage(serverLevel, shooter, spellStats, spellContext, resolver, (LivingEntity) target, DamageUtil.source(serverLevel, DamageTypesRegistry.GENERIC_SPELL_DAMAGE, shooter, rlb), finalDamage);

                    if (target instanceof Creeper creeper) {
                        creeper.thunderHit(serverLevel, rlb);
                    }
                }
            });


        }
    }

    @Override
    protected int getDefaultManaCost() {
        return 1500;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE);
    }

}
