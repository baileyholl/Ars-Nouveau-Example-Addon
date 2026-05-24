package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

        if (completedTribulation.hasFailed()) {
            if (tribulations.hasPendingLevel()) {
                tribulations.clearPendingLevel();
                player.displayClientMessage(Component.translatable("text.ars_trinkets.tribulation.failure")
                        .withStyle(ChatFormatting.RED), false);
            }
            return;
        }

        if (!hasActiveTribulations && tribulations.hasPendingLevel()) {
            int pendingLevel = tribulations.getPendingLevel();
            ArsTrinkets.LOGGER.debug("[Tribulation] All tribulations done! Applying pending level: {} -> {}",
                    levelCap.level, pendingLevel);
            levelCap.unsafeSetLevel(pendingLevel);
            tribulations.clearPendingLevel();
            player.displayClientMessage(Component.translatable("text.ars_trinkets.tribulation.success")
                    .withStyle(ChatFormatting.GREEN), false);
            player.displayClientMessage(Component.translatable("text.ars_trinkets.level_up_" + pendingLevel)
                    .withStyle(ChatFormatting.GREEN), false);
            ArsTrinkets.LOGGER.debug("[Tribulation] Level applied silently via unsafeSetLevel");
        }
    }
}
