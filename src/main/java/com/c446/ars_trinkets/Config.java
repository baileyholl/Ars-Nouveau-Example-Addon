package com.c446.ars_trinkets;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Config {

    public static class Common {
        public static ModConfigSpec.IntValue AURA_BASE_DURATION;
        public static ModConfigSpec.IntValue AURA_BASE_RADIUS;
        public static ModConfigSpec.IntValue AURA_BASE_DELAY;
        public static ModConfigSpec.IntValue AURA_BASE_ACCELERATE;

        public static ModConfigSpec.ConfigValue<List<? extends Float>> LOTUS_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Float>> RING_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Float>> MONOCLE_VALUES;

        public Common(ModConfigSpec.Builder builder) {
            AURA_BASE_DURATION = builder.defineInRange("aura_base_duration", 20 * 5, 1, 10000000);
            AURA_BASE_RADIUS = builder.defineInRange("aura_base_radius", 10, 1, 10000000);
            AURA_BASE_DELAY = builder.defineInRange("aura_base_delay", 10, 1, 10000000);
            AURA_BASE_ACCELERATE = builder.defineInRange("aura_base_accelerate", 5, 1, 10000000);

            LOTUS_VALUES = builder.defineList("lotus_curios_amplifications", List.of(1f, 2.5f, 5f, 10f, 25f, 50f, 100f, 200f), element -> {
                return true;
            });
            RING_VALUES = builder.defineList("ring_curios_amplifications", List.of(1f, 2.5f, 5f, 10f, 25f, 50f, 100f, 200f), element -> {
                return true;
            });
            MONOCLE_VALUES = builder.defineList("_curios_amplifications", List.of(1f, 2.5f, 5f, 10f, 25f, 50f, 100f, 200f), element -> {
                return true;
            });
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
