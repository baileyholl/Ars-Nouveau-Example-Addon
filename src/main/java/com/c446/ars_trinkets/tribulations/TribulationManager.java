package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.events.LevelModifiedEvent;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.c446.ars_trinkets.registry.TribulationTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
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
        LOGGER.debug("[TribulationManager.startTribulation] Creating instance: player={}, types={}, intensity={}", player.getName().getString(), types.size(), intensity);
        var registry = Objects.requireNonNull(player.getServer()).registryAccess()
                .registryOrThrow(TribulationTypeRegistry.REGISTRY_KEY);
        var tribInstance = new TribulationInstance(intensity);
        tribInstance.setPlayer(player);
        LOGGER.debug("[TribulationManager.startTribulation] Initializing tribulation instance");
        tribInstance.initialize(types, registry);
        player.getData(CapabilityRegistry.TRIBULATIONS).add(tribInstance);
        LOGGER.info("[TribulationManager.startTribulation] Tribulation started for {}: {} type(s), intensity={}", player.getName().getString(), types.size(), intensity);
    }

    // -------------------------------------------------------------------------
    // Event dispatch
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        if (instance != null) {
            instance.handleServerTick(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (instance != null) {
            instance.handlePlayerJoin(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (instance != null) {
            instance.handlePlayerRespawn(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (instance != null && event.getEntity() instanceof ServerPlayer player) {
            instance.handlePlayerDeath(player);
        }
    }

    @SubscribeEvent
    public static void onPreLevelUp(LevelModifiedEvent.Pre event) {
        if (instance == null) {
            ArsTrinkets.LOGGER.warn("[TribulationManager.onPreLevelUp] Instance not initialized, skipping level-up event");
            return;
        }
        if (event.entity instanceof ServerPlayer player) {
            //event.setCanceled(true);
            //instance.handlePreLevelUp(player, event.newLevel);
        }
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
            tribInstance.postEvent(new PlayerDiedEvent(tribInstance));
        }
    }

    private void handlePreLevelUp(@NotNull ServerPlayer player, short newLevel) {
        System.err.println("HANDLING PRE-LEVEL UP");
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

        // Boss enhancement almost always applies (80% chance)
        List<Holder<TribulationType>> selected = new java.util.ArrayList<>();
        if (random.nextFloat() < 0.8f) {
            selected.add(TribulationTypeRegistry.BOSS_ENHANCEMENT);
            LOGGER.debug("[TribulationManager.handlePreLevelUp] Boss enhancement selected (80% roll)");
        }

        // Higher levels add additional random tribulations
        int additionalLayers = Math.max(0, (newLevel - 1) / 2);
        LOGGER.debug("[TribulationManager.handlePreLevelUp] Additional layers to select: {}", additionalLayers);
        for (int i = 0; i < additionalLayers; i++) {
            Holder<TribulationType> type = allTypes.get(random.nextInt(allTypes.size()));
            selected.add(type);
        }

        // Ensure at least one tribulation
        if (selected.isEmpty()) {
            selected.add(allTypes.get(random.nextInt(allTypes.size())));
            LOGGER.debug("[TribulationManager.handlePreLevelUp] Fallback tribulation selected (no selection)");
        }

        LOGGER.info("[TribulationManager.handlePreLevelUp] {} tribulation(s) selected for level {}", selected.size(), newLevel);

        float intensity = 0.5f + (newLevel * 0.75f);
        startTribulation(player, selected, intensity);
    }
}
