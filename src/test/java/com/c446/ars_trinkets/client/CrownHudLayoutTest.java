package com.c446.ars_trinkets.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrownHudLayoutTest {
    @Test
    void alignsTheLivesRowAboveTheArmorBar() {
        CrownHudLayout layout = CrownHudLayout.aboveArmorBar(400, 240, 9, 0, 0);

        assertEquals(109, layout.x());
        assertEquals(171, layout.y());
    }

    @Test
    void appliesConfiguredPositionOffsets() {
        CrownHudLayout layout = CrownHudLayout.aboveArmorBar(400, 240, 3, 7, -4);

        assertEquals(116, layout.x());
        assertEquals(167, layout.y());
    }
}
