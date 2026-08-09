package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectDevourSoul extends AbstractEffect implements IDamageEffect {
    public static final EffectDevourSoul INSTANCE = new EffectDevourSoul(ArsTrinkets.prefix("glyph_devour_soul"), "Devour the soul of the target.");

    public EffectDevourSoul(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    protected int getDefaultManaCost() {
        return 3_000;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAmplify.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 9);
        super.addDefaultAugmentLimits(defaults);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity target)) return;
        if (!(world instanceof ServerLevel serverLevel)) return;
        if (!(shooter instanceof ServerPlayer serverPlayer)) return;
        if (target == serverPlayer) return;

        LevelingCapability casterSoul = serverPlayer.getData(CapabilityRegistry.LEVEL_CAP);
        long soulsBeforeCast = Math.max(0L, casterSoul.souls);
        long consumedSouls = DevourSoulMath.consumedSouls(soulsBeforeCast,
                Config.Common.DEVOUR_SOUL_CONSUMPTION_PERCENT.get() / 100.0
                        * Math.max(1.0, spellStats.getAmpMultiplier()));
        if (consumedSouls == 0L) return;

        casterSoul.souls = soulsBeforeCast - consumedSouls;
        double damage = 1.0 + DevourSoulMath.bonusDamage(consumedSouls,
                Config.Common.DEVOUR_SOUL_DAMAGE_PER_SQRT_SOUL.get());
        attemptDamage(serverLevel, serverPlayer, spellStats, spellContext, resolver, target,
                DamageUtil.source(serverLevel, DamageTypes.MAGIC, serverPlayer), (float) damage);

        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                target.getX(), target.getEyeY(), target.getZ(), 32, 0.25, 0.25, 0.25, 0.02);
        serverPlayer.displayClientMessage(Component.translatable("text.ars_trinkets.souls.devour"), true);
    }

    public static double targetDamageMultiplier(double damageMultiplier) {
        return DevourSoulMath.targetDamageMultiplier(damageMultiplier);
    }

    public static double targetDamageMultiplier(double damageMultiplier, double coefficient) {
        return DevourSoulMath.targetDamageMultiplier(damageMultiplier, coefficient);
    }
}
