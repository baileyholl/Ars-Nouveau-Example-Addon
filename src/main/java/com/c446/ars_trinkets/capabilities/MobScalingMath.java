package com.c446.ars_trinkets.capabilities;

public final class MobScalingMath {
    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 9;
    private static final int MIN_CORES = 1;
    private static final int MAX_CORES = 9;
    private static final double CORE_DECAY = 0.7;

    private MobScalingMath() {
    }

    public static int levelForBaseStats(double health, double attack, double armor) {
        return levelForBaseStats(health, attack, armor, 1.0, 2.0, MIN_LEVEL, MAX_LEVEL);
    }

    public static int levelForBaseStats(double health, double attack, double armor,
                                        double scoreFloor, double scoreBase,
                                        int minLevel, int maxLevel) {
        double score = Math.max(scoreFloor, health + attack + armor);
        return Math.clamp((int) Math.floor(Math.log(score) / Math.log(scoreBase)), minLevel, maxLevel);
    }

    public static double healthMultiplier(int level, int cores) {
        return healthMultiplier(level, cores, 2.0, MIN_LEVEL, MAX_LEVEL, MIN_CORES, MAX_CORES);
    }

    public static double healthMultiplier(int level, int cores, double growthBase,
                                          int minLevel, int maxLevel,
                                          int minCores, int maxCores) {
        int boundedLevel = Math.clamp(level, minLevel, maxLevel);
        return Math.pow(growthBase, boundedLevel - minLevel) * Math.clamp(cores, minCores, maxCores);
    }

    public static double cappedHealthMultiplier(int level, int cores, double maximum) {
        return Math.min(healthMultiplier(level, cores), Math.max(1.0, maximum));
    }

    public static int mobLevelCapForPlayer(int playerLevel, int configuredMaximum, int minimum) {
        return mobLevelCapForPlayer(playerLevel, configuredMaximum, minimum, 1);
    }

    public static int mobCoreCapForPlayer(int playerCores, int configuredMaximum, int minimum) {
        return mobCoreCapForPlayer(playerCores, configuredMaximum, minimum, 3);
    }

    public static int mobLevelCapForPlayer(int playerLevel, int configuredMaximum,
                                           int minimum, int playerLevelOffset) {
        return Math.max(minimum, Math.min(configuredMaximum, playerLevel + playerLevelOffset));
    }

    public static int mobCoreCapForPlayer(int playerCores, int configuredMaximum,
                                          int minimum, int playerCoreOffset) {
        return Math.max(minimum, Math.min(configuredMaximum, playerCores + playerCoreOffset));
    }

    public static double combatStatMultiplier(int level, int cores) {
        return combatStatMultiplier(level, cores, MIN_LEVEL, MAX_LEVEL, MIN_CORES, MAX_CORES);
    }

    public static double combatStatMultiplier(int level, int cores,
                                              int minLevel, int maxLevel,
                                              int minCores, int maxCores) {
        return Math.clamp(level, minLevel, maxLevel) * (double) Math.clamp(cores, minCores, maxCores);
    }

    public static double scaledArmor(double baseArmor, int level, int cores, double cap) {
        return Math.min(Math.max(0.0, cap), Math.max(0.0, baseArmor) * combatStatMultiplier(level, cores));
    }

    public static double scaledArmor(double baseArmor, int level, int cores, double cap,
                                     int minLevel, int maxLevel, int minCores, int maxCores) {
        return Math.min(Math.max(0.0, cap), Math.max(0.0, baseArmor)
                * combatStatMultiplier(level, cores, minLevel, maxLevel, minCores, maxCores));
    }

    public static int rollCores(int level, double sample) {
        return rollCores(level, sample, MIN_LEVEL, MAX_LEVEL, MIN_CORES, MAX_CORES, CORE_DECAY, 12.0);
    }

    public static int rollCores(int level, double sample,
                                int minLevel, int maxLevel, int minCores, int maxCores,
                                double coreDecay, double levelBiasDivisor) {
        double boundedSample = Math.clamp(sample, 0.0, Math.nextDown(1.0));
        double totalWeight = 0.0;
        for (int core = minCores; core <= maxCores; core++) {
            totalWeight += coreWeight(level, core, minLevel, maxLevel, coreDecay, levelBiasDivisor);
        }

        double target = boundedSample * totalWeight;
        for (int core = minCores; core <= maxCores; core++) {
            target -= coreWeight(level, core, minLevel, maxLevel, coreDecay, levelBiasDivisor);
            if (target < 0.0) return core;
        }
        return maxCores;
    }

    public static double coreNineProbability(int level) {
        double totalWeight = 0.0;
        for (int core = MIN_CORES; core <= MAX_CORES; core++) {
            totalWeight += coreWeight(level, core);
        }
        return coreWeight(level, MAX_CORES) / totalWeight;
    }

    private static double coreWeight(int level, int core) {
        return coreWeight(level, core, MIN_LEVEL, MAX_LEVEL, CORE_DECAY, 12.0);
    }

    private static double coreWeight(int level, int core, int minLevel, int maxLevel,
                                    double coreDecay, double levelBiasDivisor) {
        double levelBias = 1.0 - Math.clamp(level, minLevel, maxLevel) / levelBiasDivisor;
        return Math.pow(coreDecay, (core - 1) * levelBias);
    }

    private static int clampCores(int cores) {
        return Math.clamp(cores, MIN_CORES, MAX_CORES);
    }
}
