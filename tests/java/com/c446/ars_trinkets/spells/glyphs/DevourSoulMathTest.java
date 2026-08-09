package com.c446.ars_trinkets.spells.glyphs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DevourSoulMathTest {
    @Test
    void consumesBoundedPercentageOfSouls() {
        assertEquals(100L, DevourSoulMath.consumedSouls(1_000L, 0.10));
        assertEquals(1_000L, DevourSoulMath.consumedSouls(1_000L, 2.0));
        assertEquals(0L, DevourSoulMath.consumedSouls(-10L, 0.10));
    }

    @Test
    void bonusDamageUsesDiminishingReturns() {
        assertEquals(20.0, DevourSoulMath.bonusDamage(100L, 2.0), 0.0001);
        assertEquals(200.0, DevourSoulMath.bonusDamage(10_000L, 2.0), 0.0001);
        assertEquals(0.0, DevourSoulMath.bonusDamage(0L, 2.0), 0.0001);
    }

    @Test
    void targetMultiplierNeverIncreasesDamage() {
        assertEquals(1.0, DevourSoulMath.targetDamageMultiplier(1.0), 0.0001);
        assertEquals(0.5, DevourSoulMath.targetDamageMultiplier(2.0), 0.0001);
        assertEquals(1.0, DevourSoulMath.targetDamageMultiplier(0.0), 0.0001);
    }

    @Test
    void equalLevelAttackerAndTargetCancelTheirLevelScaling() {
        double levelMultiplier = 15.0;

        assertEquals(1.0,
                levelMultiplier * DevourSoulMath.targetDamageMultiplier(levelMultiplier),
                0.0001);
    }

    @Test
    void targetNormalizationCoefficientControlsMobScaling() {
        assertEquals(1.0, DevourSoulMath.targetDamageMultiplier(16.0, 0.0), 0.0001);
        assertEquals(0.25, DevourSoulMath.targetDamageMultiplier(16.0, 0.5), 0.0001);
        assertEquals(1.0 / 16.0, DevourSoulMath.targetDamageMultiplier(16.0, 1.0), 0.0001);
    }
}
