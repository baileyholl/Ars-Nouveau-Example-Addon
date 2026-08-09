package com.c446.ars_trinkets.network;

public final class MobSoulVisionState {
    private static volatile int viewerLevel;
    private static volatile boolean enabled;

    private MobSoulVisionState() {
    }

    public static boolean enabled() {
        return enabled;
    }

    public static int viewerLevel() {
        return viewerLevel;
    }

    public static void set(int level, boolean visible) {
        viewerLevel = Math.clamp(level, 0, 9);
        enabled = visible;
    }
}
