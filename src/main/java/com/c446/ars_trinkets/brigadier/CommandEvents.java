package com.c446.ars_trinkets.brigadier;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class CommandEvents {

    @SubscribeEvent
    static public void onRegisterCommands(RegisterCommandsEvent event) {
        ArsTrinketsLevelCommand.register(event.getDispatcher());
    }

}
