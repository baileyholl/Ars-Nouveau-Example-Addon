package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class KarmaCalculator {

    public static float calculateKarmaMultiplier(Player p) {
        if (!Config.Common.ENABLE_KARMA_SCALING.get()) {
            return 1.0f;
        }

        if (!(p instanceof ServerPlayer player)) return 1;

        var stats = player.getStats();

        // Get kill counts
        int playerKills = stats.getValue(Stats.ENTITY_KILLED.get(EntityType.PLAYER));
        int passiveMobKills = getPassiveMobKillCount(stats);
        int deaths = stats.getValue(Stats.CUSTOM.get(Stats.DEATHS));

        // Get positive actions (dampening factors)
        int villagerTrades = stats.getValue(Stats.CUSTOM.get(Stats.TRADED_WITH_VILLAGER));
        int animalsBred = stats.getValue(Stats.CUSTOM.get(Stats.ANIMALS_BRED));

        // Get playtime in hours
        int playtimeTicks = stats.getValue(Stats.CUSTOM.get(Stats.TOTAL_WORLD_TIME));
        double playtimeHours = playtimeTicks / 72000.0; // 72000 ticks = 1 hour

        // Calculate karma score (negative actions increase difficulty)
        double playerKillScore = playerKills * Config.Common.KARMA_PLAYER_KILL_WEIGHT.get();
        double passiveMobKillScore = passiveMobKills * Config.Common.KARMA_PASSIVE_MOB_KILL_WEIGHT.get();
        double deathScore = deaths * Config.Common.KARMA_DEATH_WEIGHT.get();

        double karmaScore = playerKillScore + passiveMobKillScore + deathScore;

        // Apply dampening factors (positive actions reduce difficulty)
        double playtimeDampening = playtimeHours * Config.Common.KARMA_PLAYTIME_DAMPENING.get();
        double villagerDampening = villagerTrades * Config.Common.KARMA_VILLAGER_TRADE_DAMPENING.get();
        double animalsDampening = animalsBred * Config.Common.KARMA_ANIMALS_BRED_DAMPENING.get();

        // Total dampening reduces karma score
        double totalDampening = playtimeDampening + villagerDampening + animalsDampening;
        karmaScore = Math.max(0.0, karmaScore - totalDampening);

        // Base multiplier: 1.0 + (karma / 100) * scale
        Double karmaScale = Config.Common.KARMA_SCALE.get();
        float karmaMultiplier = (float) (1.0f + (float) (karmaScore / 100.0) * karmaScale);

        // Scale with player level if enabled
        if (Config.Common.KARMA_SCALE_WITH_PLAYER_LEVEL.get()) {
            var levelCap = player.getData(CapabilityRegistry.LEVEL_CAP);
            float levelDifficultyIncrease = levelCap.getWorldDifficultyIncrease();
            karmaMultiplier *= levelDifficultyIncrease;
        }

        return Math.max(0.5f, karmaMultiplier); // Floor at 0.5x difficulty
    }

    private static int getPassiveMobKillCount(net.minecraft.stats.StatsCounter stats) {
        int total = 0;
        EntityType<?>[] passiveMobs = {
                EntityType.COW,
                EntityType.PIG,
                EntityType.SHEEP,
                EntityType.CHICKEN,
                EntityType.RABBIT,
                EntityType.HORSE,
                EntityType.DONKEY,
                EntityType.LLAMA,
                EntityType.CAT,
                EntityType.WOLF,
                EntityType.PARROT,
                EntityType.TURTLE,
                EntityType.FROG,
                EntityType.AXOLOTL,
                EntityType.BEE,
                EntityType.GOAT,
                EntityType.CAMEL,
                EntityType.SNIFFER
        };

        for (EntityType<?> entityType : passiveMobs) {
            try {
                total += stats.getValue(Stats.ENTITY_KILLED.get(entityType));
            } catch (Exception e) {
                // Skip if entity type not found
            }
        }

        return total;
    }
}
