package com.c446.ars_trinkets.capabilities;

final class MobDamageHandlingMath {
    private MobDamageHandlingMath() {
    }

    static double scaledDamage(double damage, double capabilityMultiplier, double cores) {
        return scaledDamage(damage, capabilityMultiplier, cores, 0.5);
    }

    static double scaledDamage(double damage, double capabilityMultiplier, double cores, double coefficient) {
        return damage * capabilityMultiplier * Math.max(1.0, cores) * coefficient;
    }
}
