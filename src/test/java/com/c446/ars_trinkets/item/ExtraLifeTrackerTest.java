package com.c446.ars_trinkets.item;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExtraLifeTrackerTest {
    private static final UUID PLAYER = UUID.randomUUID();

    @Test
    void startsWithNineLivesWhenCrownIsEquipped() {
        ExtraLifeTracker tracker = new ExtraLifeTracker(9);

        tracker.equip(PLAYER);

        assertEquals(9, tracker.remaining(PLAYER));
    }

    @Test
    void consumesExactlyOneLifePerDeathEvent() {
        ExtraLifeTracker tracker = new ExtraLifeTracker(9);
        tracker.equip(PLAYER);

        assertTrue(tracker.tryPreventDeath(PLAYER));
        assertEquals(8, tracker.remaining(PLAYER));
    }

    @Test
    void resetsOnlyAfterADeathThatWasNotPrevented() {
        ExtraLifeTracker tracker = new ExtraLifeTracker(9);
        tracker.equip(PLAYER);
        for (int i = 0; i < 9; i++) {
            assertTrue(tracker.tryPreventDeath(PLAYER));
        }

        assertEquals(0, tracker.remaining(PLAYER));
        assertFalse(tracker.tryPreventDeath(PLAYER));
        assertEquals(0, tracker.remaining(PLAYER));

        tracker.respawn(PLAYER);

        assertEquals(9, tracker.remaining(PLAYER));
    }

    @Test
    void unequippingRemovesTheState() {
        ExtraLifeTracker tracker = new ExtraLifeTracker(9);
        tracker.equip(PLAYER);

        tracker.unequip(PLAYER);

        assertEquals(9, tracker.remaining(PLAYER));
    }
}
