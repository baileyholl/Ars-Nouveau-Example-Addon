package com.c446.ars_trinkets.spells.glyphs;

final class DevourSoulMath {
    private DevourSoulMath() {
    }

    static long consumedSouls(long totalSouls, double fraction) {
        if (totalSouls <= 0L || !Double.isFinite(fraction)) return 0L;
        double boundedFraction = Math.clamp(fraction, 0.0, 1.0);
        return Math.min(totalSouls, (long) Math.floor(totalSouls * boundedFraction));
    }

    static double bonusDamage(long consumedSouls, double damagePerSqrtSoul) {
        if (consumedSouls <= 0L || !Double.isFinite(damagePerSqrtSoul)) return 0.0;
        return Math.max(0.0, damagePerSqrtSoul) * Math.sqrt(consumedSouls);
    }

    static double targetDamageMultiplier(double damageMultiplier) {
        return targetDamageMultiplier(damageMultiplier, 1.0);
    }

    static double targetDamageMultiplier(double damageMultiplier, double coefficient) {
        if (damageMultiplier <= 1.0 || !Double.isFinite(damageMultiplier)
                || !Double.isFinite(coefficient)) return 1.0;
        return Math.pow(damageMultiplier, -Math.clamp(coefficient, 0.0, 1.0));
    }
}
