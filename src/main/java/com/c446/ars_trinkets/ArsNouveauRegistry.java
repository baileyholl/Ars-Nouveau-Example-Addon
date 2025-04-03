package com.c446.ars_trinkets;

import com.c446.ars_trinkets.glyphs.AirSwordEffect;
import com.c446.ars_trinkets.glyphs.TestEffect;
import com.c446.ars_trinkets.glyphs.filters.RandomCancel;
import com.c446.ars_trinkets.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {

    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen

    public static void registerGlyphs(){
        register(AirSwordEffect.instance);
        register(RandomCancel.HALF);
        register(RandomCancel.QUARTER);
        register(RandomCancel.THREE_FOURTHS);
    }
    public static void registerSounds(){
        SpellSoundRegistry.registerSpellSound(ModRegistry.EXAMPLE_SPELL_SOUND);
    }
    public static void register(AbstractSpellPart spellPart){
        GlyphRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }
}
