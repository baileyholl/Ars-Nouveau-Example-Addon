package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.glyphs.*;
import com.c446.ars_trinkets.glyphs.filters.IsNotSelf;
import com.c446.ars_trinkets.glyphs.filters.IsSelf;
import com.c446.ars_trinkets.glyphs.forms.AuraForm;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {

    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen

    public static void registerGlyphs(){
        register(EffectAirSword.INSTANCE);
        register(EffectSunFlare.INSTANCE);
        register(EffectWaterSpear.INSTANCE);
/*        register(RandomCancel.HALF);
        register(RandomCancel.QUARTER);
        register(RandomCancel.THREE_FOURTHS);*/
        register(EffectSonicBoom.INSTANCE);
        register(IsSelf.INSTANCE);
        register(IsNotSelf.INSTANCE);
        //register(Inversion.INSTANCE);
        register(AuraForm.INSTANCE);
        register(EffectInspectSoul.INSTANCE);
        register(EffectAdvancedGrowth.INSTANCE);
    }

    public static void registerSounds(){
        SpellSoundRegistry.registerSpellSound(ModRegistry.EXAMPLE_SPELL_SOUND);
    }
    public static void register(AbstractSpellPart spellPart){
        GlyphRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }
}
