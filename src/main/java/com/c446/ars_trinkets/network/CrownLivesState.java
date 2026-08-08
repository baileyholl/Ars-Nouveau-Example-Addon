package com.c446.ars_trinkets.network;

public final class CrownLivesState {
    private static volatile int remainingLives;

    private CrownLivesState() {
    }

    public static int remainingLives() {
        return remainingLives;
    }

    public static void setRemainingLives(int lives) {
        remainingLives = Math.max(0, Math.min(9, lives));
    }
}
