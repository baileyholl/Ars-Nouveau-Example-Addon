package com.c446.ars_trinkets;

import com.c446.ars_trinkets.brigadier.ArsTrinketsLevelCommand;
import com.c446.ars_trinkets.datagen.ComponentRegistry;
import com.c446.ars_trinkets.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArsTrinkets.MODID)
public class ArsTrinkets {
    public static final String MODID = "ars_trinkets";

    public static final Logger LOGGER = LogManager.getLogger();
    public static Set<UUID> OMNIPOTENT_PLAYER = new HashSet<>();

    public ArsTrinkets(IEventBus modEventBus, ModContainer modContainer) {
        ArsNouveauRegistry.registerGlyphs();
        AttributeRegistry.ATTRIBUTES.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        CapabilityRegistry.ATTACHMENT_TYPES.register(modEventBus);
        ComponentRegistry.COMPONENTS.register(modEventBus);
        CreativeTabRegistry.CREATIVE_MOD_TABS.register(modEventBus);
        EffectsRegistry.EFFECTS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        ModRegistry.SOUNDS.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);
//        modEventBus.addListener(this::onRegisterCommands);
//        NeoForge.EVENT_BUS.register(this);

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
