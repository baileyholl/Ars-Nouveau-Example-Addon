package com.example.examplemod;

import com.example.examplemod.glyphs.TestEffect;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs(){
        register(TestEffect.INSTANCE);
    }

    public static void register(AbstractSpellPart spellPart){
        ArsNouveauAPI.getInstance().registerSpell(spellPart.tag,spellPart);
        registeredSpells.add(spellPart);
    }
}
