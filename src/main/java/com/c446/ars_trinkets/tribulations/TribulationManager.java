package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.events.LevelModifiedEvent;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.c446.ars_trinkets.registry.TribulationTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.c446.ars_trinkets.ArsTrinkets.LOGGER;

@EventBusSubscriber
public class TribulationManager {
    private static TribulationManager instance;

    public TribulationManager() {
        if (instance != null) {
            throw new IllegalStateException("Instance already exists!");
        }
        instance = this;
        LOGGER.info("[TribulationManager] Singleton instance initialized");
    }

    public static TribulationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("[Ars Trinkets]Instance not initialized yet!");
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void startTribulation(ServerPlayer player, List<Holder<TribulationType>> types, float intensity) {
        if (!isTribulationSystemEnabled()) {
            LOGGER.debug("[TribulationManager.startTribulation] Tribulation system disabled; skipping start for {}", player.getName().getString());
            return;
        }
        LOGGER.debug("[TribulationManager.startTribulation] Creating instance: player={}, types={}, intensity={}", player.getName().getString(), types.size(), intensity);
        var registry = Objects.requireNonNull(player.getServer()).registryAccess()
                .registryOrThrow(TribulationTypeRegistry.REGISTRY_KEY);
        var tribInstance = new TribulationInstance(intensity);
        tribInstance.setPlayer(player);
        LOGGER.debug("[TribulationManager.startTribulation] Initializing tribulation instance");
        tribInstance.initialize(types, registry);
        player.getData(CapabilityRegistry.TRIBULATIONS).add(tribInstance);
        player.displayClientMessage(Component.translatable("text.ars_trinkets.tribulation.begin")
                .withStyle(ChatFormatting.RED), false);
        LOGGER.info("[TribulationManager.startTribulation] Tribulation started for {}: {} type(s), intensity={}", player.getName().getString(), types.size(), intensity);
    }

    // -------------------------------------------------------------------------
    // Event dispatch
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        if (instance != null && isTribulationSystemEnabled()) {
            instance.handleServerTick(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (instance != null && isTribulationSystemEnabled()) {
            instance.handlePlayerJoin(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (instance != null && isTribulationSystemEnabled()) {
            instance.handlePlayerRespawn(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (instance != null && isTribulationSystemEnabled() && event.getEntity() instanceof ServerPlayer player) {
            instance.handlePlayerDeath(player);
        }
    }

    @SubscribeEvent
    public static void onPreLevelUp(LevelModifiedEvent.Pre event) {
        if (!isTribulationSystemEnabled()) {
            return;
        }
        if (instance == null) {
            ArsTrinkets.LOGGER.warn("[TribulationManager.onPreLevelUp] Instance not initialized, skipping level-up event");
            return;
        }
        if (event.entity instanceof ServerPlayer player) {
            event.setCanceled(true);
            instance.handlePreLevelUp(player, event.newLevel);
        }
    }

    private static boolean isTribulationSystemEnabled() {
        return Config.Common.TRIBULATION_SYSTEM_ENABLED.get();
    }

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    protected void handleServerTick(ServerTickEvent.Pre event) {
        for (var player : event.getServer().getPlayerList().getPlayers()) {
            var tribulations = player.getData(CapabilityRegistry.TRIBULATIONS);
            if (!tribulations.getInstances().isEmpty()) {
                LOGGER.debug("[TribulationManager.handleServerTick] Processing {} active tribulation(s) for {}", tribulations.getInstances().size(), player.getName().getString());
            }

            // Detect completed tribulations before removal
            for (var tribInstance : tribulations.getInstances()) {
                if (!tribInstance.isActive()) {
                    LOGGER.info("[TribulationManager.handleServerTick] Tribulation completed for {}", player.getName().getString());
                    TribulationCompletionHandler.onTribulationComplete(player, tribInstance);
                }
            }

            tribulations.removeInactive();
            for (var tribInstance : tribulations.getInstances()) {
                tribInstance.setPlayer(player);
                float karmaMultiplier = KarmaCalculator.calculateKarmaMultiplier(player);
                tribInstance.setKarmaScaledIntensity(karmaMultiplier);
                tribInstance.tick();
            }
        }
    }

    private void handlePlayerJoin(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var tribulations = player.getData(CapabilityRegistry.TRIBULATIONS);
        if (!tribulations.getInstances().isEmpty()) {
            LOGGER.debug("[TribulationManager.handlePlayerJoin] Player joined with {} active tribulation(s)", tribulations.getInstances().size());
        }
        var registry = player.getServer().registryAccess()
                .registryOrThrow(TribulationTypeRegistry.REGISTRY_KEY);
        for (var tribInstance : tribulations.getInstances()) {
            tribInstance.setPlayer(player);
            tribInstance.reinitializeIfNeeded(registry);
        }
    }

    private void handlePlayerRespawn(PlayerEvent.@NotNull PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var tribulations = player.getData(CapabilityRegistry.TRIBULATIONS);
        if (!tribulations.getInstances().isEmpty()) {
            LOGGER.debug("[TribulationManager.handlePlayerRespawn] Player respawned with {} active tribulation(s)", tribulations.getInstances().size());
        }
        var registry = player.getServer().registryAccess()
                .registryOrThrow(TribulationTypeRegistry.REGISTRY_KEY);
        for (var tribInstance : tribulations.getInstances()) {
            tribInstance.setPlayer(player);
            tribInstance.reinitializeIfNeeded(registry);
        }
    }

    private void handlePlayerDeath(@NotNull ServerPlayer player) {
        var tribulations = player.getData(CapabilityRegistry.TRIBULATIONS);
        if (!tribulations.getInstances().isEmpty()) {
            LOGGER.debug("[TribulationManager.handlePlayerDeath] Player died with {} active tribulation(s)", tribulations.getInstances().size());
        }
        for (var tribInstance : tribulations.getInstances()) {
            if (!tribInstance.isActive()) continue;
            tribInstance.fail();
            tribInstance.postEvent(new PlayerDiedEvent(tribInstance));
        }
    }

    private void handlePreLevelUp(@NotNull ServerPlayer player, short newLevel) {
        LOGGER.info("[TribulationManager.handlePreLevelUp] Level-up Pre event: {} -> level {}", player.getName().getString(), newLevel);

        var tribulations = player.getData(CapabilityRegistry.TRIBULATIONS);
        tribulations.setPendingLevel(newLevel);
        LOGGER.debug("[TribulationManager.handlePreLevelUp] Pending level saved: {}", newLevel);

        RandomSource random = RandomSource.create();
        List<Holder<TribulationType>> allTypes = List.of(
                TribulationTypeRegistry.LIGHTNING_RAIN.getDelegate(),
                TribulationTypeRegistry.STAR_RAIN.getDelegate(),
                TribulationTypeRegistry.PERCEPTION_DISTORTION.getDelegate(),
                TribulationTypeRegistry.AILMENTS.getDelegate(),
                TribulationTypeRegistry.BOSS_ENHANCEMENT.getDelegate()
        );

        List<Holder<TribulationType>> selected = new java.util.ArrayList<>();
        selected.add(TribulationTypeRegistry.LIGHTNING_RAIN.getDelegate());
        LOGGER.debug("[TribulationManager.handlePreLevelUp] Lightning rain selected (guaranteed)");

        // Boss enhancement almost always applies (configurable chance)
        if (random.nextFloat() < Config.Common.TRIBULATION_BOSS_ENHANCEMENT_CHANCE.get()) {
            selected.add(TribulationTypeRegistry.BOSS_ENHANCEMENT.getDelegate());
            LOGGER.debug("[TribulationManager.handlePreLevelUp] Boss enhancement selected (chance roll)");
        }

        // Higher levels add additional random tribulations
        int additionalLayers = Math.max(0, (newLevel - 1) / Config.Common.TRIBULATION_ADDITIONAL_LAYER_LEVELS_PER_STEP.get());
        LOGGER.debug("[TribulationManager.handlePreLevelUp] Additional layers to select: {}", additionalLayers);
        for (int i = 0; i < additionalLayers; i++) {
            Holder<TribulationType> type = allTypes.get(random.nextInt(allTypes.size()));
            selected.add(type);
        }

        LOGGER.info("[TribulationManager.handlePreLevelUp] {} tribulation(s) selected for level {}", selected.size(), newLevel);

        float intensity = (float)(Config.Common.TRIBULATION_INTENSITY_BASE.get()
                + (newLevel * Config.Common.TRIBULATION_INTENSITY_PER_LEVEL.get()));
        startTribulation(player, selected, intensity);
    }
}
