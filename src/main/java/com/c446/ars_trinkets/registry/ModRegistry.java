package com.c446.ars_trinkets.registry;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import static com.c446.ars_trinkets.ArsTrinkets.MODID;
import static com.c446.ars_trinkets.ArsTrinkets.prefix;
import static net.minecraft.core.registries.Registries.SOUND_EVENT;

public class ModRegistry {


    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(SOUND_EVENT, MODID);

    //this is an example of how to register a sound. You also need to add the sound to the sound.json file, referencing your ogg files, and a texture for the button under textures/sounds.
    //this example will use one of the existing sounds randomly
    public static DeferredHolder<SoundEvent, SoundEvent> EXAMPLE_FAMILY = SOUNDS.register("example_sound", () -> makeSound("example_sound"));
    public static SpellSound EXAMPLE_SPELL_SOUND = new SpellSound(ModRegistry.EXAMPLE_FAMILY, Component.literal("Example"), prefix("example_random_sound"));

    static SoundEvent makeSound(@NotNull String name) {
        return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}
