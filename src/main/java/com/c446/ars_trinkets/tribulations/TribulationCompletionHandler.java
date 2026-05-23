package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import net.minecraft.server.level.ServerPlayer;

public class TribulationCompletionHandler {

    public static void onTribulationComplete(ServerPlayer player, TribulationInstance completedTribulation) {
        var levelCap = player.getData(CapabilityRegistry.LEVEL_CAP);
        if (levelCap == null) return;

        var tribulations = player.getData(CapabilityRegistry.TRIBULATIONS);

        // If there are no more active tribulations and we have a pending level, apply it
        boolean hasActiveTribulations = tribulations.getInstances().stream()
                .anyMatch(TribulationInstance::isActive);

        ArsTrinkets.LOGGER.debug("[Tribulation] Checking completion for {}: activeCount={}, hasPending={}",
                player.getName().getString(),
                tribulations.getInstances().stream().filter(TribulationInstance::isActive).count(),
                tribulations.hasPendingLevel());

        if (!hasActiveTribulations && tribulations.hasPendingLevel()) {
            int pendingLevel = tribulations.getPendingLevel();
            ArsTrinkets.LOGGER.debug("[Tribulation] All tribulations done! Applying pending level: {} -> {}",
                    levelCap.level, pendingLevel);
            levelCap.unsafeSetLevel(pendingLevel);
            tribulations.clearPendingLevel();
            ArsTrinkets.LOGGER.debug("[Tribulation] Level applied silently via unsafeSetLevel");
        }
    }
}
