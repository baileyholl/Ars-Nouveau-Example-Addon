package com.c446.ars_trinkets;

import com.c446.ars_trinkets.registry.EffectsRegistry;
import com.c446.ars_trinkets.registry.ItemRegistry;
import com.c446.ars_trinkets.registry.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArsTrinkets.MODID)
public class ArsTrinkets {
    public static final String MODID = "ars_trinkets";

    private static final Logger LOGGER = LogManager.getLogger();

    public ArsTrinkets(IEventBus modEventBus, ModContainer modContainer) {
        ArsNouveauRegistry.registerGlyphs();
        ModRegistry.SOUNDS.register(modEventBus);
        EffectsRegistry.EFFECTS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON);
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ArsNouveauRegistry.registerSounds();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static void setInterval(Runnable method, int tickInterval, int timeToLive) {
        NeoForge.EVENT_BUS.register(new SetInterval(method, tickInterval, timeToLive));
    }

    public static class SetInterval {
        int ticks = 0;
        Runnable method;
        int tickInterval = 0; //How many ticks have to pass before the method is called again
        int timeToLive = 0;

        public SetInterval(Runnable method, int tickInterval, int timeToLive) {
            //function, tick rate, time to live
            this.method = method;
            this.tickInterval = tickInterval;
            this.timeToLive = timeToLive;
        }

        @SubscribeEvent
        public void onTick(ServerTickEvent.Pre event) {
            //subtract 1 tickInterval from the time to live to account for the extra tick that runs when
            //unregistering the listener
            if (ticks >= (timeToLive - tickInterval)) {
                //System.out.println("Time to unregister this listener, i guess ;-;");
                NeoForge.EVENT_BUS.unregister(this);
            }

            if (ticks % tickInterval == 0) {
                //System.out.println("On tick event called :)");
                this.method.run();
            }
            ticks++;
        }
    }
}
