package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EffectSunFlare extends AbstractEffect implements IDamageEffect {
    public static final EffectSunFlare INSTANCE = new EffectSunFlare(ArsTrinkets.prefix("glyph_sun_flare"), "Sun Flare");

    public EffectSunFlare(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel level) {
            Vec3 pos = rayTraceResult.getLocation();
            double range = 1 + 1.7 * spellStats.getAoeMultiplier();
            int bonus = 1;
            DamageSource source = DamageUtil.source(level, DamageTypes.IN_FIRE, shooter);

            double damage = this.DAMAGE.get() + (6 * (1 + bonus)) + (this.AMP_VALUE.get() * spellStats.getAmpMultiplier());
            //attemptDamage(level, shooter, spellStats, spellContext, resolver, living, source, (float) damage);

            level.sendParticles(ParticleTypes.GLOW, pos.x, pos.y, pos.z, 10, 0, 0, 0, 1);
            level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 50, 0, 0, 0, 1);
//            level.sendParticles(new DustParticle.Provider((SpriteSet) ParticleTypes.DUST).createParticle().setColor(1,1,1), 0,0,0,1,0,0, 0,1);

            for (Entity e : world.getEntities(shooter, new AABB(
                    pos.add(range, range, range), pos.subtract(range, range, range)))) {
                if (!(e instanceof LivingEntity l))
                    continue;

                level.sendParticles(ParticleTypes.GLOW, l.getX(), l.getY(), l.getZ(), 20, 0, 0, 0, 1);
                level.sendParticles(ParticleTypes.FLAME, l.getX(), l.getY(), l.getZ(), 100, 0, 0, 0, 1);

                bonus = 0;

                if (l.isOnFire()) {
                    bonus += 1;
                }
                if (l.hasEffect(ModPotions.FREEZING_EFFECT)) {
                    bonus += Objects.requireNonNull(l.getEffect(ModPotions.FREEZING_EFFECT)).getAmplifier();
                }
                if (l.getType().getCategory().equals(MobCategory.MONSTER)) {
                    bonus += 10;
                }
                damage = (this.DAMAGE.get() + ((this.AMP_VALUE.get()) / 1.5 * (spellStats.getAmpMultiplier()))) * Math.clamp(1+bonus,1,2f);
                attemptDamage(level, shooter, spellStats, spellContext, resolver, l, source, (float) damage);
            }
        }
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 13);
        addAmpConfig(builder, 7);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE);
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
        return this.setOf(SpellSchools.ELEMENTAL_FIRE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 4);
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(), 4);
    }
}
