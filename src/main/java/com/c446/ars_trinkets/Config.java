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
        public static ModConfigSpec.BooleanValue ENABLE_BONUS_GLYPH_SLOTS;
        public static ModConfigSpec.BooleanValue ENFORCE_BONUS_GLYPH_SLOTS_ON_CAST;
        public static ModConfigSpec.IntValue BONUS_GLYPH_SLOTS_FALLBACK;
        public static ModConfigSpec.IntValue BONUS_GLYPH_SLOTS_CAP;

        // Karma system config
        public static ModConfigSpec.BooleanValue ENABLE_KARMA_SCALING;
        public static ModConfigSpec.DoubleValue KARMA_SCALE;
        public static ModConfigSpec.DoubleValue KARMA_PLAYER_KILL_WEIGHT;
        public static ModConfigSpec.DoubleValue KARMA_PASSIVE_MOB_KILL_WEIGHT;
        public static ModConfigSpec.DoubleValue KARMA_DEATH_WEIGHT;
        public static ModConfigSpec.DoubleValue KARMA_PLAYTIME_DAMPENING;
        public static ModConfigSpec.DoubleValue KARMA_VILLAGER_TRADE_DAMPENING;
        public static ModConfigSpec.DoubleValue KARMA_ANIMALS_BRED_DAMPENING;
        public static ModConfigSpec.BooleanValue KARMA_SCALE_WITH_PLAYER_LEVEL;

        public static ModConfigSpec.BooleanValue TRIBULATION_SYSTEM_ENABLED;
        public static ModConfigSpec.DoubleValue TRIBULATION_INTENSITY_BASE;
        public static ModConfigSpec.DoubleValue TRIBULATION_INTENSITY_PER_LEVEL;
        public static ModConfigSpec.IntValue TRIBULATION_DURATION_BASE_SECONDS;
        public static ModConfigSpec.DoubleValue TRIBULATION_DURATION_SECONDS_PER_INTENSITY;
        public static ModConfigSpec.IntValue TRIBULATION_DURATION_MAX_SECONDS;
        public static ModConfigSpec.DoubleValue TRIBULATION_BOSS_ENHANCEMENT_CHANCE;
        public static ModConfigSpec.IntValue TRIBULATION_ADDITIONAL_LAYER_LEVELS_PER_STEP;

        public static ModConfigSpec.IntValue LIGHTNING_RAIN_INTERVAL;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_RADIUS_BASE;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_RADIUS_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_RADIUS_PER_SPELL_DAMAGE;
        public static ModConfigSpec.IntValue LIGHTNING_RAIN_STRIKE_CHANCE_BASE;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_STRIKE_CHANCE_REDUCTION_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_BASE;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_PER_SPELL_DAMAGE;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_SIZE_BASE;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_SIZE_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_MULTIPLIER_THRESHOLD;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_MULTIPLIER_DIVISOR;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_MULTIPLIER_MIN;
        public static ModConfigSpec.DoubleValue LIGHTNING_RAIN_DAMAGE_MULTIPLIER_MAX;

        public static ModConfigSpec.IntValue STAR_RAIN_INTERVAL;
        public static ModConfigSpec.DoubleValue STAR_RAIN_RADIUS_BASE;
        public static ModConfigSpec.DoubleValue STAR_RAIN_RADIUS_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue STAR_RAIN_RADIUS_PER_SPELL_DAMAGE;
        public static ModConfigSpec.DoubleValue STAR_RAIN_STRIKE_COUNT_BASE;
        public static ModConfigSpec.DoubleValue STAR_RAIN_STRIKE_COUNT_PER_INTENSITY;
        public static ModConfigSpec.IntValue STAR_RAIN_STRIKE_COUNT_RANDOM_BOUND;
        public static ModConfigSpec.IntValue STAR_RAIN_STRIKE_COUNT_MAX;

        public static ModConfigSpec.IntValue AILMENTS_INTERVAL;
        public static ModConfigSpec.IntValue AILMENTS_DURATION_BASE;
        public static ModConfigSpec.DoubleValue AILMENTS_DURATION_PER_INTENSITY;
        public static ModConfigSpec.IntValue AILMENTS_DURATION_MIN;
        public static ModConfigSpec.DoubleValue AILMENTS_WARDING_DURATION_REDUCTION;
        public static ModConfigSpec.DoubleValue AILMENTS_AMPLIFIER_BASE;
        public static ModConfigSpec.DoubleValue AILMENTS_AMPLIFIER_PER_INTENSITY;
        public static ModConfigSpec.IntValue AILMENTS_AMPLIFIER_MAX;
        public static ModConfigSpec.DoubleValue AILMENTS_HEALTH_SCALING_THRESHOLD;
        public static ModConfigSpec.DoubleValue AILMENTS_HEALTH_SCALING_DIVISOR;
        public static ModConfigSpec.IntValue AILMENTS_HEALTH_RANDOM_BONUS;

        public static ModConfigSpec.IntValue PERCEPTION_DISTORTION_INTERVAL;
        public static ModConfigSpec.IntValue PERCEPTION_DISTORTION_DURATION_BASE;
        public static ModConfigSpec.DoubleValue PERCEPTION_DISTORTION_DURATION_PER_INTENSITY;
        public static ModConfigSpec.IntValue PERCEPTION_DISTORTION_DURATION_MIN;
        public static ModConfigSpec.DoubleValue PERCEPTION_DISTORTION_WARDING_DURATION_REDUCTION;
        public static ModConfigSpec.DoubleValue PERCEPTION_DISTORTION_AMPLIFIER_BASE;
        public static ModConfigSpec.DoubleValue PERCEPTION_DISTORTION_AMPLIFIER_PER_INTENSITY;
        public static ModConfigSpec.IntValue PERCEPTION_DISTORTION_AMPLIFIER_MAX;

        public static ModConfigSpec.IntValue BOSS_ENHANCEMENT_INTERVAL;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_RADIUS_BASE;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_RADIUS_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_RADIUS_PER_SPELL_DAMAGE;
        public static ModConfigSpec.IntValue BOSS_ENHANCEMENT_EFFECT_DURATION;
        public static ModConfigSpec.IntValue BOSS_ENHANCEMENT_STRENGTH_MAX;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_STRENGTH_BASE;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_STRENGTH_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_STRENGTH_PER_PLAYER_DAMAGE;
        public static ModConfigSpec.IntValue BOSS_ENHANCEMENT_RESISTANCE_MAX;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_RESISTANCE_BASE;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_RESISTANCE_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_SPEED_BASE;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_SPEED_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_SPEED_PER_SPELL_DAMAGE;
        public static ModConfigSpec.IntValue BOSS_ENHANCEMENT_HEALTH_MAX;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_HEALTH_BASE;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_HEALTH_PER_INTENSITY;
        public static ModConfigSpec.IntValue BOSS_ENHANCEMENT_DAMAGE_MAX;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_DAMAGE_BASE;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_DAMAGE_PER_INTENSITY;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_DAMAGE_PER_PLAYER_DAMAGE;
        public static ModConfigSpec.DoubleValue BOSS_ENHANCEMENT_DAMAGE_PER_SPELL_DAMAGE;

        public static ModConfigSpec.ConfigValue<List<? extends Double>> LOTUS_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> RING_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> MONOCLE_VALUES;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> SOUL_QUANTITY_FOR_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> MANA_BONUS_PER_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> DAMAGE_BONUS_PER_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> MANA_REGEN_BONUS_PER_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> SOUL_QUANTITY_FOR_ESSENCE_LEVEL;
        public static ModConfigSpec.ConfigValue<List<? extends Float>> WORLD_DIFFICULTY_PER_LEVEL;

        public static ModConfigSpec.ConfigValue<List<List<? extends String>>> LEVEL_BONUSES_RAW;

        public Common(ModConfigSpec.Builder builder) {
            CURIOS_FILE_PATH = builder.define("ars_trinkets_curios_config", "ars_trinkets/curios_config.json");
            builder.comment("^^THE FILE ABOVE HAS TO BE SPECIFIED WITH THE PATH FORMATTING OF YOUR OS!\nPLACE IT WITHIN THE NEOFORGE CONFIG DIRECTORY!\nTHIS PATH IS RELATIVE.\nFOR A PATH IN \"~/Documents/my-instance/config/ars_trinkets/custom_eternity_rune.json\", PLEASE PUT \"ars_trinkets/custom_eternity_rune.json\"\nAN EXAMPLE CONFIG CAN BE FOUND HERE!!! (https://github.com/clcment446/ars_trinkets");

            AURA_BASE_DURATION = builder.defineInRange("aura_base_duration", 20 * 5, 1, 10000000);
            AURA_BASE_RADIUS = builder.defineInRange("aura_base_radius", 10, 1, 10000000);
            AURA_BASE_DELAY = builder.defineInRange("aura_base_delay", 10, 1, 10000000);
            AURA_BASE_ACCELERATE = builder.defineInRange("aura_base_accelerate", 5, 1, 10000000);

            MAX_LEVEL_ALLOWED = builder.defineInRange("max_level_allowed", 9, 0, 9);

            builder.push("bonus_glyph_slots");
            ENABLE_BONUS_GLYPH_SLOTS = builder
                    .comment("Enables addon-controlled bonus glyph slot resolution.")
                    .define("enabled", true);
            ENFORCE_BONUS_GLYPH_SLOTS_ON_CAST = builder
                    .comment("Reject spells that exceed the computed slot cap during server-side cast.")
                    .define("enforce_on_cast", true);
            BONUS_GLYPH_SLOTS_FALLBACK = builder
                    .comment("Used when no player/caster bonus could be resolved.")
                    .defineInRange("fallback_bonus", 0, 0, 1024);
            BONUS_GLYPH_SLOTS_CAP = builder
                    .comment("Hard cap applied to computed bonus slots to avoid extreme values.")
                    .defineInRange("bonus_cap", 256, 0, 4096);
            builder.pop();

            builder.push("karma_system");
            ENABLE_KARMA_SCALING = builder
                    .comment("Enable tribulation difficulty scaling based on player actions (karma system).")
                    .define("enabled", true);
            KARMA_SCALE = builder
                    .comment("Global scale multiplier for all karma-related penalties. Adjust to fine-tune overall difficulty progression.")
                    .defineInRange("karma_scale", 1.0, 0, 10.0);
            KARMA_PLAYER_KILL_WEIGHT = builder
                    .comment("Weight multiplier for player kills in karma calculation.")
                    .defineInRange("player_kill_weight", 5.0, 0.0, 100.0);
            KARMA_PASSIVE_MOB_KILL_WEIGHT = builder
                    .comment("Weight multiplier for passive mob kills in karma calculation.")
                    .defineInRange("passive_mob_kill_weight", 0.5, 0.0, 100.0);
            KARMA_DEATH_WEIGHT = builder
                    .comment("Weight multiplier for player deaths in karma calculation.")
                    .defineInRange("death_weight", 2.0, 0.0, 100.0);
            KARMA_PLAYTIME_DAMPENING = builder
                    .comment("Dampening factor for playtime (hours). Higher values reduce karma scaling for experienced players.")
                    .defineInRange("playtime_dampening", 0.001, 0.0, 0.1);
            KARMA_VILLAGER_TRADE_DAMPENING = builder
                    .comment("Dampening factor for villager trades. More trades = lower tribulation difficulty.")
                    .defineInRange("villager_trade_dampening", 0.05, 0.0, 1.0);
            KARMA_ANIMALS_BRED_DAMPENING = builder
                    .comment("Dampening factor for animals bred. More breeding = lower tribulation difficulty (highest importance).")
                    .defineInRange("animals_bred_dampening", 0.1, 0.0, 1.0);
            KARMA_SCALE_WITH_PLAYER_LEVEL = builder
                    .comment("Scale tribulation difficulty with player's in-game level via LevelingCapability.")
                    .define("scale_with_level", true);
            builder.pop();

            builder.push("tribulations");
            TRIBULATION_SYSTEM_ENABLED = builder
                    .comment("Master toggle for the tribulation system. When disabled, tribulations are not started or processed.")
                    .define("enabled", true);
            TRIBULATION_INTENSITY_BASE = builder.defineInRange("intensity_base", 1.5, 0.0, 1000.0);
            TRIBULATION_INTENSITY_PER_LEVEL = builder.defineInRange("intensity_per_level", 1.25, 0.0, 1000.0);
            TRIBULATION_DURATION_BASE_SECONDS = builder.defineInRange("duration_base_seconds", 45, 1, 100000);
            TRIBULATION_DURATION_SECONDS_PER_INTENSITY = builder.defineInRange("duration_seconds_per_intensity", 10.0, 0.0, 100000.0);
            TRIBULATION_DURATION_MAX_SECONDS = builder.defineInRange("duration_max_seconds", 180, 1, 100000);
            TRIBULATION_BOSS_ENHANCEMENT_CHANCE = builder.defineInRange("boss_enhancement_chance", 0.8, 0.0, 1.0);
            TRIBULATION_ADDITIONAL_LAYER_LEVELS_PER_STEP = builder.defineInRange("additional_layer_levels_per_step", 2, 1, 1000);

            builder.push("lightning_rain");
            LIGHTNING_RAIN_INTERVAL = builder.defineInRange("interval_ticks", 8, 1, 100000);
            LIGHTNING_RAIN_RADIUS_BASE = builder.defineInRange("radius_base", 40.0, 0.0, 100000.0);
            LIGHTNING_RAIN_RADIUS_PER_INTENSITY = builder.defineInRange("radius_per_intensity", 12.0, 0.0, 100000.0);
            LIGHTNING_RAIN_RADIUS_PER_SPELL_DAMAGE = builder.defineInRange("radius_per_spell_damage", 3.0, 0.0, 100000.0);
            LIGHTNING_RAIN_STRIKE_CHANCE_BASE = builder.defineInRange("strike_chance_base", 10, 1, 100000);
            LIGHTNING_RAIN_STRIKE_CHANCE_REDUCTION_PER_INTENSITY = builder.defineInRange("strike_chance_reduction_per_intensity", 1.5, 0.0, 100000.0);
            LIGHTNING_RAIN_DAMAGE_BASE = builder.defineInRange("damage_base", 15.0, 0.0, 100000.0);
            LIGHTNING_RAIN_DAMAGE_PER_SPELL_DAMAGE = builder.defineInRange("damage_per_spell_damage", 1.0, 0.0, 100000.0);
            LIGHTNING_RAIN_DAMAGE_SIZE_BASE = builder.defineInRange("damage_size_base", 3.0, 0.0, 100000.0);
            LIGHTNING_RAIN_DAMAGE_SIZE_PER_INTENSITY = builder.defineInRange("damage_size_per_intensity", 0.5, 0.0, 100000.0);
            LIGHTNING_RAIN_DAMAGE_MULTIPLIER_THRESHOLD = builder.defineInRange("damage_multiplier_generic_threshold", 1.5, 0.0, 100000.0);
            LIGHTNING_RAIN_DAMAGE_MULTIPLIER_DIVISOR = builder.defineInRange("damage_multiplier_health_ratio_divisor", 2.5, 0.0001, 100000.0);
            LIGHTNING_RAIN_DAMAGE_MULTIPLIER_MIN = builder.defineInRange("damage_multiplier_min", 1.0, 0.0, 100000.0);
            LIGHTNING_RAIN_DAMAGE_MULTIPLIER_MAX = builder.defineInRange("damage_multiplier_max", 2.0, 0.0, 100000.0);
            builder.pop();

            builder.push("star_rain");
            STAR_RAIN_INTERVAL = builder.defineInRange("interval_ticks", 5, 1, 100000);
            STAR_RAIN_RADIUS_BASE = builder.defineInRange("radius_base", 56.0, 0.0, 100000.0);
            STAR_RAIN_RADIUS_PER_INTENSITY = builder.defineInRange("radius_per_intensity", 16.0, 0.0, 100000.0);
            STAR_RAIN_RADIUS_PER_SPELL_DAMAGE = builder.defineInRange("radius_per_spell_damage", 4.0, 0.0, 100000.0);
            STAR_RAIN_STRIKE_COUNT_BASE = builder.defineInRange("strike_count_base", 2.0, 0.0, 100000.0);
            STAR_RAIN_STRIKE_COUNT_PER_INTENSITY = builder.defineInRange("strike_count_per_intensity", 0.9, 0.0, 100000.0);
            STAR_RAIN_STRIKE_COUNT_RANDOM_BOUND = builder.defineInRange("strike_count_random_bound", 4, 1, 100000);
            STAR_RAIN_STRIKE_COUNT_MAX = builder.defineInRange("strike_count_max", 14, 1, 100000);
            builder.pop();

            builder.push("ailments");
            AILMENTS_INTERVAL = builder.defineInRange("interval_ticks", 30, 1, 100000);
            AILMENTS_DURATION_BASE = builder.defineInRange("duration_base_ticks", 160, 1, 100000);
            AILMENTS_DURATION_PER_INTENSITY = builder.defineInRange("duration_per_intensity", 45.0, 0.0, 100000.0);
            AILMENTS_DURATION_MIN = builder.defineInRange("duration_min_ticks", 30, 1, 100000);
            AILMENTS_WARDING_DURATION_REDUCTION = builder.defineInRange("warding_duration_reduction", 0.01, 0.0, 1.0);
            AILMENTS_AMPLIFIER_BASE = builder.defineInRange("amplifier_base", 1.0, 0.0, 100000.0);
            AILMENTS_AMPLIFIER_PER_INTENSITY = builder.defineInRange("amplifier_per_intensity", 0.9, 0.0, 100000.0);
            AILMENTS_AMPLIFIER_MAX = builder.defineInRange("amplifier_max", 5, 0, 255);
            AILMENTS_HEALTH_SCALING_THRESHOLD = builder.defineInRange("health_scaling_threshold", 20.0, 0.0, 100000.0);
            AILMENTS_HEALTH_SCALING_DIVISOR = builder.defineInRange("health_scaling_divisor", 10.0, 0.0001, 100000.0);
            AILMENTS_HEALTH_RANDOM_BONUS = builder.defineInRange("health_random_bonus", 1, 0, 100000);
            builder.pop();

            builder.push("perception_distortion");
            PERCEPTION_DISTORTION_INTERVAL = builder.defineInRange("interval_ticks", 20, 1, 100000);
            PERCEPTION_DISTORTION_DURATION_BASE = builder.defineInRange("duration_base_ticks", 100, 1, 100000);
            PERCEPTION_DISTORTION_DURATION_PER_INTENSITY = builder.defineInRange("duration_per_intensity", 35.0, 0.0, 100000.0);
            PERCEPTION_DISTORTION_DURATION_MIN = builder.defineInRange("duration_min_ticks", 20, 1, 100000);
            PERCEPTION_DISTORTION_WARDING_DURATION_REDUCTION = builder.defineInRange("warding_duration_reduction", 0.01, 0.0, 1.0);
            PERCEPTION_DISTORTION_AMPLIFIER_BASE = builder.defineInRange("amplifier_base", 1.0, 0.0, 100000.0);
            PERCEPTION_DISTORTION_AMPLIFIER_PER_INTENSITY = builder.defineInRange("amplifier_per_intensity", 0.7, 0.0, 100000.0);
            PERCEPTION_DISTORTION_AMPLIFIER_MAX = builder.defineInRange("amplifier_max", 4, 0, 255);
            builder.pop();

            builder.push("boss_enhancement");
            BOSS_ENHANCEMENT_INTERVAL = builder.defineInRange("interval_ticks", 80, 1, 100000);
            BOSS_ENHANCEMENT_RADIUS_BASE = builder.defineInRange("radius_base", 80.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_RADIUS_PER_INTENSITY = builder.defineInRange("radius_per_intensity", 24.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_RADIUS_PER_SPELL_DAMAGE = builder.defineInRange("radius_per_spell_damage", 6.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_EFFECT_DURATION = builder.defineInRange("effect_duration_ticks", 260, 1, 100000);
            BOSS_ENHANCEMENT_STRENGTH_MAX = builder.defineInRange("strength_max", 6, 0, 255);
            BOSS_ENHANCEMENT_STRENGTH_BASE = builder.defineInRange("strength_base", 2.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_STRENGTH_PER_INTENSITY = builder.defineInRange("strength_per_intensity", 1.1, 0.0, 100000.0);
            BOSS_ENHANCEMENT_STRENGTH_PER_PLAYER_DAMAGE = builder.defineInRange("strength_per_player_damage", 0.15, 0.0, 100000.0);
            BOSS_ENHANCEMENT_RESISTANCE_MAX = builder.defineInRange("resistance_max", 5, 0, 255);
            BOSS_ENHANCEMENT_RESISTANCE_BASE = builder.defineInRange("resistance_base", 1.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_RESISTANCE_PER_INTENSITY = builder.defineInRange("resistance_per_intensity", 0.9, 0.0, 100000.0);
            BOSS_ENHANCEMENT_SPEED_BASE = builder.defineInRange("speed_base", 1.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_SPEED_PER_INTENSITY = builder.defineInRange("speed_per_intensity", 0.25, 0.0, 100000.0);
            BOSS_ENHANCEMENT_SPEED_PER_SPELL_DAMAGE = builder.defineInRange("speed_per_spell_damage", 0.25, 0.0, 100000.0);
            BOSS_ENHANCEMENT_HEALTH_MAX = builder.defineInRange("health_max", 6, 0, 255);
            BOSS_ENHANCEMENT_HEALTH_BASE = builder.defineInRange("health_base", 1.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_HEALTH_PER_INTENSITY = builder.defineInRange("health_per_intensity", 0.7, 0.0, 100000.0);
            BOSS_ENHANCEMENT_DAMAGE_MAX = builder.defineInRange("damage_max", 6, 0, 255);
            BOSS_ENHANCEMENT_DAMAGE_BASE = builder.defineInRange("damage_base", 1.0, 0.0, 100000.0);
            BOSS_ENHANCEMENT_DAMAGE_PER_INTENSITY = builder.defineInRange("damage_per_intensity", 0.6, 0.0, 100000.0);
            BOSS_ENHANCEMENT_DAMAGE_PER_PLAYER_DAMAGE = builder.defineInRange("damage_per_player_damage", 0.2, 0.0, 100000.0);
            BOSS_ENHANCEMENT_DAMAGE_PER_SPELL_DAMAGE = builder.defineInRange("damage_per_spell_damage", 0.15, 0.0, 100000.0);
            builder.pop();
            builder.pop();

            LOTUS_VALUES = builder.defineList("lotus_curios_amplifications", List.of(1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d), element -> true);
            RING_VALUES = builder.defineList("ring_curios_amplifications", List.of(1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d), element -> true);
            MONOCLE_VALUES = builder.defineList("monocle_curios_amplifications", List.of(1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d), element -> true);

            SOUL_QUANTITY_FOR_LEVEL = builder.defineList("soul_quantity", List.of(500, 1000, 2500, 5000, 10000, 25000, 50000, 100000, 250000), e -> true);
            SOUL_QUANTITY_FOR_ESSENCE_LEVEL = builder.defineList("soul_quantity", List.of(25, 50, 125, 250, 500, 1500, 3000, 6000, 15000), e -> true);

            DAMAGE_BONUS_PER_LEVEL = builder.defineList("damage_bonus", List.of(1.1d, 1.15d, 1.2d, 1.3d, 1.5d, 2d, 4d, 8d, 15d), e -> true);
            MANA_BONUS_PER_LEVEL = builder.defineList("mana_bonus", List.of(25 * 3, 50 * 3, 125 * 3, 250 * 3, 500 * 3, 1500 * 3, 3000 * 3, 6000 * 3, 15000 * 3), e -> true);
            MANA_REGEN_BONUS_PER_LEVEL = builder.defineList("regen_bonus", List.of(25 * 3, 50 * 3, 125 * 3, 250 * 3, 500 * 3, 1500 * 3, 3000 * 3, 6000 * 3, 15000 * 3), e -> true);

            WORLD_DIFFICULTY_PER_LEVEL = builder.defineList("world_difficulty_per_level",
                    List.of(
                            1f,
                            2.5f,
                            5.0f,
                            7.5f,
                            10.0f,
                            12.5f,
                            15.0f,
                            20.0f,
                            25.0f,
                            30.0f
                    ), e-> true);
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
