package com.c446.ars_trinkets.capabilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MobDamageHandlingTest {
    @Test
    void mobDamageUsesCapabilityAndHalfTheConfiguredMultiplier() {
        assertEquals(4.5, MobDamageHandlingMath.scaledDamage(1.0, 9.0, 1.0), 0.0001);
        assertEquals(22.5, MobDamageHandlingMath.scaledDamage(1.0, 9.0, 5.0), 0.0001);
    }
}
