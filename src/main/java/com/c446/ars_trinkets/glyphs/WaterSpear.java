package com.c446.ars_trinkets.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class WaterSpear extends AbstractEffect implements IDamageEffect {
    public WaterSpear(String tag, String description) {
        super(tag, description);
    }

    public static final WaterSpear INSTANCE = new WaterSpear(new ResourceLocation(ArsTrinkets.MODID, "glyph_water_spear"), "Water Spear");

    public WaterSpear(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public int getDefaultManaCost() {
        return 4500;
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nonnull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity living && world instanceof ServerLevel level) {
            Vec3 vecTar = entity.position();
            double bonus = 1;
            float AMP_DAMAGE = 15f;
            double damage = 30;
            if (living.hasEffect(ModPotions.FREEZING_EFFECT)) {
                int s = Objects.requireNonNull(living.getEffect(ModPotions.FREEZING_EFFECT)).getAmplifier();
                s /= 2;
                bonus += s;
            }
            if (living.hasEffect(ModPotions.SHOCKED_EFFECT)) {
                int s = Objects.requireNonNull(living.getEffect(ModPotions.SHOCKED_EFFECT)).getAmplifier();
                s /= 2.5;
                bonus += s;
            }
            if (living.isOnFire()) {
                bonus *= 1.2;
            }
            bonus /= 1.5;
            damage = (this.DAMAGE.get() + ((this.AMP_VALUE.get()) / 1.5 * (spellStats.getAmpMultiplier()))) * (1 + bonus) / 1.5;

            level.sendParticles(ParticleTypes.DRIPPING_DRIPSTONE_WATER, vecTar.x, vecTar.y, vecTar.z, 20, 0, 0, 0, 1);
            this.attemptDamage(world, shooter, spellStats, spellContext, resolver, entity, DamageUtil.source(world, DamageTypes.DROWN), (float) (damage));
            living.invulnerableTime = 3;
        }
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE);
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.ELEMENTAL_WATER);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 9.0);
        addAmpConfig(builder, 9.0);
    }

    @Override
    public void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 4);
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(), 4);
    }


}
