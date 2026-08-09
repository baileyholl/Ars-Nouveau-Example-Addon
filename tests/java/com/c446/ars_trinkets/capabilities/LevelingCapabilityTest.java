package com.c446.ars_trinkets.capabilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LevelingCapabilityTest {
    @Test
    void damageMultiplierAcceptsIntegerConfigValues() {
        assertEquals(2.0, LevelingCapability.damageMultiplierValue(Integer.valueOf(2)), 0.0001);
    }

    @Test
    void damageMultiplierAcceptsDecimalConfigValues() {
        assertEquals(2.5, LevelingCapability.damageMultiplierValue(Double.valueOf(2.5)), 0.0001);
    }
}
