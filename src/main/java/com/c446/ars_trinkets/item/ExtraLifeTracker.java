package com.c446.ars_trinkets.item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Tracks the crown's lives independently from the death and respawn events. */
final class ExtraLifeTracker {
    private final int livesPerCycle;
    private final Map<UUID, Integer> remainingLives = new HashMap<>();
    private final Map<UUID, Boolean> awaitingRespawn = new HashMap<>();

    ExtraLifeTracker(int livesPerCycle) {
        if (livesPerCycle < 1) throw new IllegalArgumentException("livesPerCycle must be positive");
        this.livesPerCycle = livesPerCycle;
    }

    void equip(UUID playerId) {
        remainingLives.putIfAbsent(playerId, livesPerCycle);
    }

    boolean tryPreventDeath(UUID playerId) {
        int remaining = remaining(playerId);
        if (remaining == 0) {
            awaitingRespawn.put(playerId, true);
            return false;
        }

        remainingLives.put(playerId, remaining - 1);
        return true;
    }

    void respawn(UUID playerId) {
        if (awaitingRespawn.remove(playerId) != null) {
            remainingLives.put(playerId, livesPerCycle);
        }
    }

    void unequip(UUID playerId) {
        remainingLives.remove(playerId);
        awaitingRespawn.remove(playerId);
    }

    int remaining(UUID playerId) {
        return remainingLives.getOrDefault(playerId, livesPerCycle);
    }
}
