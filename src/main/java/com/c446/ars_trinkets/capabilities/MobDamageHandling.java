package com.c446.ars_trinkets.capabilities;

import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.c446.ars_trinkets.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public final class MobDamageHandling {
    private MobDamageHandling() {
    }

    @SubscribeEvent
    public static void onMobDamage(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)
                || attacker instanceof Player
                || !attacker.hasData(CapabilityRegistry.LEVEL_CAP)) {
            return;
        }

        LevelingCapability capability = attacker.getData(CapabilityRegistry.LEVEL_CAP);
        event.setNewDamage((float) MobDamageHandlingMath.scaledDamage(
                event.getNewDamage(), capability.getDamageMult(), capability.cores,
                Config.Common.MOB_DAMAGE_SCALING_COEFFICIENT.get()));
    }
}
