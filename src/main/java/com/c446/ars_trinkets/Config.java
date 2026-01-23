package com.c446.ars_trinkets;


import mezz.jei.neoforge.config.ServerConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Config {

    public static class Common {
        public static ModConfigSpec.ConfigValue<String> CURIOS_FILE_PATH;
        public static ModConfigSpec.IntValue AURA_BASE_DURATION;
        public static ModConfigSpec.IntValue AURA_BASE_RADIUS;
        public static ModConfigSpec.IntValue AURA_BASE_DELAY;
        public static ModConfigSpec.IntValue AURA_BASE_ACCELERATE;
        public static ModConfigSpec.IntValue MAX_LEVEL_ALLOWED;

        public static ModConfigSpec.ConfigValue<List<? extends Double>> LOTUS_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> RING_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> MONOCLE_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> SOUL_QUANTITY_FOR_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> MANA_BONUS_PER_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> DAMAGE_BONUS_PER_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> MANA_REGEN_BONUS_PER_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> SOUL_QUANTITY_FOR_ESSENCE_LEVEL;

        public static ModConfigSpec.ConfigValue<List<List<? extends String>>> LEVEL_BONUSES_RAW;

        public Common(ModConfigSpec.Builder builder) {
            CURIOS_FILE_PATH = builder.define("ars_trinkets_curios_config", "ars_trinkets/curios_config.json");
            builder.comment("^^THE FILE ABOVE HAS TO BE SPECIFIED WITH THE PATH FORMATTING OF YOUR OS!\nPLACE IT WITHIN THE NEOFORGE CONFIG DIRECTORY!\nTHIS PATH IS RELATIVE.\nFOR A PATH IN \"~/Documents/my-instance/config/ars_trinkets/custom_eternity_rune.json\", PLEASE PUT \"ars_trinkets/custom_eternity_rune.json\"\nA DEFAULT CONFIG CAN BE FOUND HERE!!! (https://github.com/clcment446/ars_trinkets");

            AURA_BASE_DURATION = builder.defineInRange("aura_base_duration", 20 * 5, 1, 10000000);
            AURA_BASE_RADIUS = builder.defineInRange("aura_base_radius", 10, 1, 10000000);
            AURA_BASE_DELAY = builder.defineInRange("aura_base_delay", 10, 1, 10000000);
            AURA_BASE_ACCELERATE = builder.defineInRange("aura_base_accelerate", 5, 1, 10000000);

            MAX_LEVEL_ALLOWED = builder.defineInRange("max_level_allowed", 9, 0, 9);

            LOTUS_VALUES = builder.defineList("lotus_curios_amplifications", List.of(1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d), element -> true);
            RING_VALUES = builder.defineList("ring_curios_amplifications", List.of(1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d), element -> true);
            MONOCLE_VALUES = builder.defineList("monocle_curios_amplifications", List.of(1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d), element -> true);

            SOUL_QUANTITY_FOR_LEVEL = builder.defineList("soul_quantity", List.of(500, 1000, 2500, 5000, 10000, 25000, 50000, 100000, 250000), e -> true);
            SOUL_QUANTITY_FOR_ESSENCE_LEVEL = builder.defineList("soul_quantity", List.of(25, 50, 125, 250, 500, 1500, 3000, 6000, 15000), e -> true);

            DAMAGE_BONUS_PER_LEVEL = builder.defineList("damage_bonus", List.of(1.1d, 1.15d, 1.2d, 1.3d, 1.5d, 2d, 4d, 8d, 15d), e -> true);
            MANA_BONUS_PER_LEVEL = builder.defineList("mana_bonus", List.of(25 * 3, 50 * 3, 125 * 3, 250 * 3, 500 * 3, 1500 * 3, 3000 * 3, 6000 * 3, 15000 * 3), e -> true);
            MANA_REGEN_BONUS_PER_LEVEL = builder.defineList("regen_bonus", List.of(25 * 3, 50 * 3, 125 * 3, 250 * 3, 500 * 3, 1500 * 3, 3000 * 3, 6000 * 3, 15000 * 3), e -> true);

        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
    }

    public static final Pair<Common, ModConfigSpec> PAIR = new ModConfigSpec.Builder().configure(Common::new);

    public static final ModConfigSpec COMMON;

    public static final Common COMMON_SPEC;

    static {
        COMMON_SPEC = PAIR.getLeft();
        COMMON = PAIR.getRight();
    }
}
