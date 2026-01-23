package com.c446.ars_trinkets.events;


import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.entities.red_lightning.RedLightningRenderer;
import com.c446.ars_trinkets.registry.EntityRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ArsTrinkets.MODID, value = Dist.CLIENT)
public class Client {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void registerModels(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityRegistry.RED_LIGHTNING.get(), RedLightningRenderer::new);
    }
}


