package com.c446.ars_trinkets.tribulations;

public final class DamageResistanceMath {
    private DamageResistanceMath() {
    }

    /**
     * Resistance amplifiers are zero-based, and Resistance V negates all damage.
     * Keep enhanced mobs dangerous without making spells completely ineffective.
     */
    public static int amplifierForConfiguredLevel(int configuredLevel) {
        return Math.clamp(configuredLevel, 1, 4) - 1;
    }
}
