package com.c446.ars_trinkets.tribulations.tasks;

import org.junit.jupiter.api.Test;

import com.c446.ars_trinkets.tribulations.DamageResistanceMath;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BossEnhancementTaskTest {
    @Test
    void convertsResistanceLevelToSafeMinecraftAmplifier() {
        assertEquals(0, DamageResistanceMath.amplifierForConfiguredLevel(1));
        assertEquals(3, DamageResistanceMath.amplifierForConfiguredLevel(4));
        assertEquals(3, DamageResistanceMath.amplifierForConfiguredLevel(5));
    }
}
