package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class PerceptionDistortionTask extends ScheduledTask {

    public PerceptionDistortionTask() {
        super(40);
    }

    @Override
    public void execute(TribulationInstance instance) {
        var player = instance.getPlayer();
        if (player == null || !player.isAlive()) return;

        float intensity = instance.getIntensity();
        double warding = getAttributeValue(player, PerkAttributes.WARDING, 0.0);

        // Scale duration with intensity, but warding reduces it
        int baseDuration = (int)(60 + intensity * 20);
        int duration = Math.max(20, (int)(baseDuration * (1.0 - warding * 0.01)));

        // Scale amplifier with intensity
        int amplifier = Math.min(2, Math.round(intensity * 0.5f));

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
