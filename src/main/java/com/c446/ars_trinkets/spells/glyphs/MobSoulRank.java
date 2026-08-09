package com.c446.ars_trinkets.spells.glyphs;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class MobSoulRank {
    private MobSoulRank() {
    }

    public static MutableComponent format(int level, int cores) {
        return levelDecorator(level).append(Component.literal(" ")).append(coreDecorator(cores));
    }

    public static MutableComponent levelDecorator(int level) {
        return switch (Math.clamp(level, 1, 9)) {
            case 1 -> Component.translatable("text.ars_trinkets.mob_level.dormant");
            case 2 -> Component.translatable("text.ars_trinkets.mob_level.awakened");
            case 3, 4 -> Component.translatable("text.ars_trinkets.mob_level.fallen");
            case 5, 6 -> Component.translatable("text.ars_trinkets.mob_level.corrupted");
            case 7 -> Component.translatable("text.ars_trinkets.mob_level.great");
            case 8 -> Component.translatable("text.ars_trinkets.mob_level.cursed");
            case 9 -> Component.translatable("text.ars_trinkets.mob_level.unholy");
            default -> Component.translatable("text.ars_trinkets.mob_level.dormant");
        };
    }

    public static MutableComponent coreDecorator(int cores) {
        return switch (Math.clamp(cores, 1, 9)) {
            case 1 -> Component.translatable("text.ars_trinkets.mob_core.beast");
            case 2 -> Component.translatable("text.ars_trinkets.mob_core.monster");
            case 3, 4 -> Component.translatable("text.ars_trinkets.mob_core.demon");
            case 5, 6 -> Component.translatable("text.ars_trinkets.mob_core.devil");
            case 7 -> Component.translatable("text.ars_trinkets.mob_core.tyrant");
            case 8 -> Component.translatable("text.ars_trinkets.mob_core.terror");
            case 9 -> Component.translatable("text.ars_trinkets.mob_core.titan");
            default -> Component.translatable("text.ars_trinkets.mob_core.beast");
        };
    }
}
