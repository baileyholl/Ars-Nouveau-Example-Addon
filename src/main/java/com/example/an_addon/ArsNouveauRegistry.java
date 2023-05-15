package com.example.an_addon;

import com.example.an_addon.glyphs.TestEffect;
import com.example.an_addon.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {

    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen

    public static void registerGlyphs(){
        register(TestEffect.INSTANCE);
    }
    public static void registerSounds(){
        ModRegistry.EXAMPLE_SPELL_SOUND = ArsNouveauAPI.getInstance().registerSpellSound(new SpellSound(ModRegistry.EXAMPLE_FAMILY.get(), Component.literal("Example")));
    }
    public static void register(AbstractSpellPart spellPart){
        ArsNouveauAPI.getInstance().registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

}
