package com.example.an_addon.datagen;

import com.example.an_addon.ExampleANAddon;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ExampleANAddon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Setup {

    //use runData configuration to generate stuff
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(new ArsProviders.ImbuementProvider(gen));
        gen.addProvider(new ArsProviders.GlyphProvider(gen));
        gen.addProvider(new ArsProviders.EnchantingAppProvider(gen));

        gen.addProvider(new ArsProviders.PatchouliProvider(gen));
    }

}
