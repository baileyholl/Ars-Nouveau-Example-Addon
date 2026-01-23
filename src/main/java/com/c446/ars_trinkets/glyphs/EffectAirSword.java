package com.c446.ars_trinkets.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EffectAirSword extends AbstractEffect implements IDamageEffect {
    public static EffectAirSword INSTANCE = new EffectAirSword(ArsTrinkets.prefix("glyph_sword"), "conjures a blade of wind that pierces the enemy, negating invulnerability");

    public EffectAirSword(String tag, String description) {
        super(tag, description);
    }

    public EffectAirSword(ResourceLocation tag, String description) {
        super(tag, description);
    }


    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        rayTraceResult.getEntity();
        if (rayTraceResult.getEntity() instanceof LivingEntity living && world instanceof ServerLevel level) {
            Vec3 livingEyes = living.getEyePosition();
            double x = livingEyes.x;
            double y = livingEyes.y;
            double z = livingEyes.z;
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, 0, 0, 1);

            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, 0, 0, 1);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, -0.5, -0.5, 0, 1);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.5, -0.5, 0, 1);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.5, 0.5, 0, 1);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, -0.5, 0.5, 0, 1);

            level.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0, 0, 0.5, 1);

            float bonus = ((living.hasEffect(ModPotions.HEX_EFFECT)) ? (Objects.requireNonNull(living.getEffect(ModPotions.HEX_EFFECT)).getAmplifier() * 2) : (1));
            float damage = (float) ((spellStats.getAmpMultiplier() * bonus) + this.DAMAGE.get());

            attemptDamage(world, shooter, spellStats, spellContext, resolver, living, DamageUtil.source(world, DamageTypesRegistry.GENERIC_SPELL_DAMAGE, shooter), damage);

        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 17);
        addAmpConfig(builder, 10);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public int getDefaultManaCost() {
        return 1500;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.ELEMENTAL_AIR);
    }
    @Override
    public void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(),4);
    }
}
