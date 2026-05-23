package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
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
        super(80);
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
        int baseDuration = (int)(100 + intensity * 30);
        int duration = Math.max(30, (int)(baseDuration * (1.0 - warding * 0.01)));

        // Scale amplifier with intensity and health (higher health = worse debuffs)
        int baseAmplifier = Math.round(intensity * 0.7f);
        int healthScaling = Math.toIntExact(maxHealth > 20 ? Math.round((maxHealth - 20) / 10f) : 0);
        int amplifier = Math.min(3, baseAmplifier + random.nextInt(Math.max(1, healthScaling)));

        var debuff = DEBUFFS.get(random.nextInt(DEBUFFS.size()));
        ArsTrinkets.LOGGER.debug("[AilmentsTask] Executing: intensity={}, duration={}, amplifier={}, debuff={}", intensity, duration, amplifier, debuff.value().getDescriptionId());
        player.addEffect(new MobEffectInstance(debuff, duration, amplifier, false, false));
    }
}
