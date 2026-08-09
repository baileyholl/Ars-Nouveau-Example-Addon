package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber
public class RoamingEntities {

    @SubscribeEvent
    public static void handleSpawn(FinalizeSpawnEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (mob.level().isClientSide) return;

        // Find the nearest player to determine karma scaling
        var nearestPlayer = mob.level().getNearestPlayer(mob.getX(), mob.getY(), mob.getZ(), 128.0, false);
        if (nearestPlayer == null) return;

        float karmaMultiplier = KarmaCalculator.calculateKarmaMultiplier(nearestPlayer);

        // Don't enhance if multiplier is below 1.1 (minimal negative karma)
        if (karmaMultiplier < 1.1f) return;

        // Capability scaling owns mob combat damage. Roaming difficulty contributes HP only by default.
        if (Config.Common.ROAMING_HEALTH_SCALING_ENABLED.get()) {
            double healthMultiplier = Math.min(
                    karmaMultiplier,
                    Config.Common.ROAMING_HEALTH_SCALING_MAX.get());
            var healthAttr = mob.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null && healthMultiplier > 1.0) {
                healthAttr.addPermanentModifier(new AttributeModifier(
                        ArsTrinkets.prefix("roaming_difficulty_health"),
                        healthMultiplier - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                mob.setHealth(mob.getMaxHealth());
            }
        }

        if (Config.Common.ROAMING_ATTACK_SCALING_ENABLED.get()) {
            int strengthLevel = Math.min(3, Math.round(karmaMultiplier - 1.0f));
            if (strengthLevel > 0) {
                mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1,
                        strengthLevel - 1, false, false));
            }
        }

        if (Config.Common.ROAMING_RESISTANCE_ENABLED.get()) {
            int resistanceLevel = Math.round((karmaMultiplier - 1.0f) * 0.5f);
            if (resistanceLevel > 0) {
                mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1,
                        DamageResistanceMath.amplifierForConfiguredLevel(resistanceLevel), false, false));
            }
        }
    }
}
