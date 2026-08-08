package com.c446.ars_trinkets.client;

final class CrownHudLayout {
    private static final int ARMOR_BAR_X = 91;
    private static final int ARMOR_BAR_Y = 59;
    static final int ICON_SIZE = 8;
    private static final int ICON_GAP = 2;

    static CrownHudLayout aboveArmorBar(int screenWidth, int screenHeight, int lives, int xOffset, int yOffset) {
        int x = screenWidth / 2 - ARMOR_BAR_X + xOffset;
        int y = screenHeight - ARMOR_BAR_Y - ICON_SIZE - ICON_GAP + yOffset;
        return new CrownHudLayout(x, y);
    }

    private final int x;
    private final int y;

    private CrownHudLayout(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int x() {
        return x;
    }

    int y() {
        return y;
    }
}
