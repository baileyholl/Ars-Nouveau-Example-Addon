package com.c446.ars_trinkets.tribulations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageResistanceMathTest {
    @Test
    void capsResistanceBelowFullDamageImmunity() {
        assertEquals(0, DamageResistanceMath.amplifierForConfiguredLevel(1));
        assertEquals(3, DamageResistanceMath.amplifierForConfiguredLevel(4));
        assertEquals(3, DamageResistanceMath.amplifierForConfiguredLevel(12));
    }
}
