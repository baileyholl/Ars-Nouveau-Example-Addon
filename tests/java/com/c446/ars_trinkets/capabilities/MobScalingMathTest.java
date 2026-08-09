package com.c446.ars_trinkets.capabilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobScalingMathTest {
    @Test
    void derivesLevelsFromTheCombinedBaseStatScore() {
        assertEquals(4, MobScalingMath.levelForBaseStats(20.0, 3.0, 0.0));
        assertEquals(9, MobScalingMath.levelForBaseStats(500.0, 30.0, 20.0));
    }

    @Test
    void healthUsesExponentialLevelGrowthAndCoreCount() {
        assertEquals(2_304.0, MobScalingMath.healthMultiplier(9, 9), 0.0001);
    }

    @Test
    void healthMultiplierCanBeCappedForNerfedMobScaling() {
        assertEquals(16.0, MobScalingMath.cappedHealthMultiplier(9, 9, 16.0), 0.0001);
        assertEquals(8.0, MobScalingMath.cappedHealthMultiplier(4, 1, 16.0), 0.0001);
    }

    @Test
    void scalingFormulasUseConfiguredBoundsAndBases() {
        assertEquals(3, MobScalingMath.levelForBaseStats(8.0, 0.0, 0.0, 1.0, 2.0, 1, 9));
        assertEquals(27.0, MobScalingMath.healthMultiplier(4, 1, 3.0, 1, 9, 1, 9), 0.0001);
        assertEquals(12.0, MobScalingMath.combatStatMultiplier(4, 3, 1, 9, 1, 9), 0.0001);
    }

    @Test
    void playerProgressionLimitsSpawnedMobLevelAndCores() {
        assertEquals(6, MobScalingMath.mobLevelCapForPlayer(5, 9, 1));
        assertEquals(9, MobScalingMath.mobLevelCapForPlayer(20, 9, 1));
        assertEquals(7, MobScalingMath.mobCoreCapForPlayer(4, 9, 1));
        assertEquals(9, MobScalingMath.mobCoreCapForPlayer(20, 9, 1));
    }

    @Test
    void playerProgressionOffsetsAreConfigurable() {
        assertEquals(8, MobScalingMath.mobLevelCapForPlayer(5, 9, 1, 3));
        assertEquals(6, MobScalingMath.mobCoreCapForPlayer(2, 9, 1, 4));
    }

    @Test
    void combatStatsUseLevelAndCoreCount() {
        assertEquals(81.0, MobScalingMath.combatStatMultiplier(9, 9), 0.0001);
    }

    @Test
    void armorScalingHonorsTheConfiguredCap() {
        assertEquals(40.0, MobScalingMath.scaledArmor(10.0, 9, 9, 40.0), 0.0001);
        assertEquals(20.0, MobScalingMath.scaledArmor(10.0, 2, 1, 40.0), 0.0001);
    }

    @Test
    void coreRollsStayInRangeAndFavorHigherCoresAtHigherLevels() {
        assertEquals(1, MobScalingMath.rollCores(1, 0.0));
        assertEquals(9, MobScalingMath.rollCores(1, 0.999999));
        assertTrue(MobScalingMath.coreNineProbability(9) > MobScalingMath.coreNineProbability(1));
    }
}
