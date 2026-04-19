package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Util;
import com.c446.ars_trinkets.entities.red_lightning.RedLightning;
import com.c446.ars_trinkets.registry.EntityRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectAncientLightningBolt extends AbstractEffect implements IDamageEffect {
    public static final EffectAncientLightningBolt INSTANCE = new EffectAncientLightningBolt(ArsTrinkets.prefix("glyph_red_lightning"), "Red Lightning");
    public ModConfigSpec.DoubleValue AOE;

    public EffectAncientLightningBolt(ResourceLocation tag, String description) {
        super(tag, description);
    }

    void addAoeConfig(ModConfigSpec.Builder builder, float defaultValue) {
        this.AOE = builder.defineInRange("aoe", defaultValue, 0.0F, Integer.MAX_VALUE);
    }


    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        addAmpConfig(builder, 15D);
        addDamageConfig(builder, 40D);
        addAoeConfig(builder, 4);
        super.buildConfig(builder);

    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.ELEMENTAL_AIR);
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

            serverLevel.getEntities(rlb, AABB.ofSize(pos, size * 2, size * 2, size * 2), (target) -> (target instanceof LivingEntity livingTarget && !shooter.isAlliedTo(livingTarget))).forEach(target -> {
                double distance = target.distanceToSqr(pos);
                if (distance < size * size && Util.hasLineOfSight(serverLevel, pos.add(0, 2, 0), target.getBoundingBox().getCenter())) {
                    float damageMult = getDamageMult(shooter, (LivingEntity) target);

                    float finalDamage = (float) (dmg * (1 - distance / (size * size)));
                    finalDamage *= damageMult;

                    //ArsTrinkets.LOGGER.debug("RLB found target {}\nmult: {}", target.getStringUUID(), damageMult);
                    var type = damageMult > 1.5f ? DamageTypes.GENERIC : DamageTypes.LIGHTNING_BOLT;

                    attemptDamage(serverLevel, shooter, spellStats, spellContext, resolver,
                            target, DamageUtil.source(serverLevel, type, shooter), finalDamage);

                    if (target instanceof Creeper creeper) {
                        creeper.thunderHit(serverLevel, rlb);
                    }
                    if (!spellContext.getRemainingSpell().isEmpty()) {
                        SpellResolver childResolver = resolver.getNewResolver(spellContext.clone().makeChildContext());
                        childResolver.onResolveEffect(world, new net.minecraft.world.phys.EntityHitResult(target));
                    }
                }
            });
            //spellContext.setCanceled(true);
        }
    }

    private static float getDamageMult(@NotNull LivingEntity shooter, LivingEntity target) {
        float targetHealth = target.getMaxHealth();
        float casterHealth = shooter instanceof LivingEntity l ? l.getHealth() : 0;

        float healthDifferenceRatio = (targetHealth - casterHealth) / casterHealth; // ratio of health difference relative to caster health

        float damageMult = 1.0f + (healthDifferenceRatio / 2.5f); // +0.01 per 2.5% health difference

        damageMult = Math.min(damageMult, 2.0f);
        damageMult = Math.max(damageMult, 1.0f); // bounded in [1, 2]
        return damageMult;
    }

    @Override
    protected int getDefaultManaCost() {
        return 1500;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE);
    }

    @Override
    protected void buildAugmentLimitsConfig(ModConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        super.buildAugmentLimitsConfig(builder, defaults);
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 6);
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(), 4);
    }


}
