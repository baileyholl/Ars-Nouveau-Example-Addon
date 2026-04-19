package com.c446.ars_trinkets.tribulations;

import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

@EventBusSubscriber
public class RoamingEntities {



    @SubscribeEvent
    public static void handleSpawn(FinalizeSpawnEvent event) {

    }
}
