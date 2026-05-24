package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class PerceptionDistortionTask extends ScheduledTask {

    public PerceptionDistortionTask() {
        super(Config.Common.PERCEPTION_DISTORTION_INTERVAL.get());
    }

    @Override
    public void execute(TribulationInstance instance) {
        var player = instance.getPlayer();
        if (player == null || !player.isAlive()) return;

        float intensity = instance.getIntensity();
        double warding = getAttributeValue(player, PerkAttributes.WARDING, 0.0);

        // Scale duration with intensity, but warding reduces it
        int baseDuration = (int)(Config.Common.PERCEPTION_DISTORTION_DURATION_BASE.get()
                + intensity * Config.Common.PERCEPTION_DISTORTION_DURATION_PER_INTENSITY.get());
        int duration = Math.max(Config.Common.PERCEPTION_DISTORTION_DURATION_MIN.get(),
                (int)(baseDuration * (1.0 - warding * Config.Common.PERCEPTION_DISTORTION_WARDING_DURATION_REDUCTION.get())));

        // Scale amplifier with intensity
        int amplifier = Math.min(Config.Common.PERCEPTION_DISTORTION_AMPLIFIER_MAX.get(),
                Math.round((float)(Config.Common.PERCEPTION_DISTORTION_AMPLIFIER_BASE.get()
                        + intensity * Config.Common.PERCEPTION_DISTORTION_AMPLIFIER_PER_INTENSITY.get())));

        ArsTrinkets.LOGGER.debug("[PerceptionDistortionTask] Executing: intensity={}, duration={}, amplifier={}, warding={}", intensity, duration, amplifier, warding);

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, amplifier, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, amplifier, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier, false, false));
    }

    private double getAttributeValue(net.minecraft.world.entity.player.Player player,
                                     Holder<Attribute> attr, double defaultVal) {
        var attribute = player.getAttribute(attr);
        return attribute != null ? attribute.getValue() : defaultVal;
    }
}
