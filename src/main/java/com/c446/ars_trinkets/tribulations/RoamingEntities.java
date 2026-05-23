package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
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

        // Scale enhancement based on karma (1.0 = no change, 2.0 = 2x enhancement)
        float enhancementFactor = karmaMultiplier - 1.0f; // 0.0 to X.X range

        // Apply strength buff
        int strengthLevel = Math.min(3, Math.round(enhancementFactor));
        if (strengthLevel > 0) {
            mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, strengthLevel - 1, false, false));
        }

        // Apply resistance
        int resistanceLevel = Math.round(enhancementFactor * 0.5f);
        if (resistanceLevel > 0) {
            mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, resistanceLevel - 1, false, false));
        }

        // Apply speed boost
        int speedLevel = Math.round(enhancementFactor * 0.3f);
        if (speedLevel > 0) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speedLevel - 1, false, false));
        }

        // Boost health
        var healthAttr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            double healthBoost = enhancementFactor * 10.0;
            healthAttr.addPermanentModifier(new AttributeModifier(ArsTrinkets.prefix("karma_boost"), healthBoost, AttributeModifier.Operation.ADD_VALUE));
            mob.setHealth(Math.min(mob.getMaxHealth(), mob.getHealth() + (float) healthBoost));
        }

        // Boost damage
        var damageAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            double damageBoost = enhancementFactor * 3.0;
            damageAttr.addPermanentModifier(new AttributeModifier(ArsTrinkets.prefix("karma_damage"), damageBoost, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
