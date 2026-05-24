package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class AilmentsTask extends ScheduledTask {
    private final RandomSource random;
    private static final List<Holder<MobEffect>> DEBUFFS = List.of(
            MobEffects.POISON,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.WITHER,
            MobEffects.HUNGER
    );

    public AilmentsTask() {
        super(Config.Common.AILMENTS_INTERVAL.get());
        this.random = RandomSource.create();
    }

    @Override
    public void execute(TribulationInstance instance) {
        var player = instance.getPlayer();
        if (player == null || !player.isAlive()) return;

        float intensity = instance.getIntensity();
        double warding = player.getAttributeValue(PerkAttributes.WARDING);
        double maxHealth = player.getMaxHealth();

        // Scale duration and intensity with tribulation intensity, reduced by warding
        int baseDuration = (int)(Config.Common.AILMENTS_DURATION_BASE.get()
                + intensity * Config.Common.AILMENTS_DURATION_PER_INTENSITY.get());
        int duration = Math.max(Config.Common.AILMENTS_DURATION_MIN.get(),
                (int)(baseDuration * (1.0 - warding * Config.Common.AILMENTS_WARDING_DURATION_REDUCTION.get())));

        // Scale amplifier with intensity and health (higher health = worse debuffs)
        int baseAmplifier = Math.round((float)(Config.Common.AILMENTS_AMPLIFIER_BASE.get()
                + intensity * Config.Common.AILMENTS_AMPLIFIER_PER_INTENSITY.get()));
        double healthScalingThreshold = Config.Common.AILMENTS_HEALTH_SCALING_THRESHOLD.get();
        int healthScaling = Math.toIntExact(maxHealth > healthScalingThreshold
                ? Math.round((maxHealth - healthScalingThreshold) / Config.Common.AILMENTS_HEALTH_SCALING_DIVISOR.get())
                : 0);
        int amplifier = Math.min(Config.Common.AILMENTS_AMPLIFIER_MAX.get(),
                baseAmplifier + random.nextInt(Math.max(1, healthScaling + Config.Common.AILMENTS_HEALTH_RANDOM_BONUS.get())));

        var debuff = DEBUFFS.get(random.nextInt(DEBUFFS.size()));
        ArsTrinkets.LOGGER.debug("[AilmentsTask] Executing: intensity={}, duration={}, amplifier={}, debuff={}", intensity, duration, amplifier, debuff.value().getDescriptionId());
        player.addEffect(new MobEffectInstance(debuff, duration, amplifier, false, false));
    }
}
