package com.c446.ars_trinkets.client;

import com.c446.ars_trinkets.capabilities.MobLevelHandling;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.c446.ars_trinkets.network.MobSoulVisionState;
import com.c446.ars_trinkets.spells.glyphs.MobSoulRank;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public final class MobSoulNameTagHandler {
    private MobSoulNameTagHandler() {
    }

    @SubscribeEvent
    public static void renderMobSoulNameTag(RenderNameTagEvent event) {
        if (!MobSoulVisionState.enabled()
                || !(event.getEntity() instanceof Mob mob)
                || Minecraft.getInstance().player == null) {
            return;
        }

        int mobLevel = MobLevelHandling.calculateLevel(mob);
        if (mobLevel > MobSoulVisionState.viewerLevel()) {
            return;
        }

        MutableComponent title = MobSoulRank.format(mobLevel, 1);
        if (mob.hasData(CapabilityRegistry.LEVEL_CAP)) {
            title = MobSoulRank.format(mobLevel, mob.getData(CapabilityRegistry.LEVEL_CAP).cores);
        }
        event.setContent(title.append(Component.literal(" ")).append(event.getOriginalContent()));
    }
}
