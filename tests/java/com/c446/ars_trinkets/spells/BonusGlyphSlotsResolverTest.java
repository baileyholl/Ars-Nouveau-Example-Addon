package com.c446.ars_trinkets.spells;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusGlyphSlotsResolverTest {
    @Test
    void infiniteSpellsAddPlayerBonusSlotsToTheNativeBonus() {
        assertEquals(25, BonusGlyphSlotsResolver.infiniteModeBonus(20, 5, 0));
    }

    @Test
    void infiniteSpellsDoNotDoubleCountTheNativeCasterBonus() {
        assertEquals(25, BonusGlyphSlotsResolver.infiniteModeBonus(20, 5, 20));
        assertEquals(29, BonusGlyphSlotsResolver.infiniteModeBonus(20, 5, 24));
    }
}
