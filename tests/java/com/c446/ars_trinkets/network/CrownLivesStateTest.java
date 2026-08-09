package com.c446.ars_trinkets.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrownLivesStateTest {
    @Test
    void zeroLivesHidesTheCrownHud() {
        CrownLivesState.setRemainingLives(7);

        CrownLivesState.setRemainingLives(0);

        assertEquals(0, CrownLivesState.remainingLives());
    }
}
