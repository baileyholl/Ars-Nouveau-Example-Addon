package com.c446.ars_trinkets.client;

import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.network.CrownLivesState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public final class CrownHud {
    private static final int HEART_SPRITE_OFFSET = 3;
    private static final int HEART_SPRITE_SIZE = 9;
    private static final ResourceLocation HEART_TEXTURE = ArsTrinkets.prefix("textures/item/putrid_heart.png");
    private CrownHud() {
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        int remainingLives = CrownLivesState.remainingLives();
        if (minecraft.player == null || minecraft.options.hideGui || remainingLives == 0) return;

        GuiGraphics graphics = event.getGuiGraphics();
        CrownHudLayout layout = CrownHudLayout.aboveArmorBar(
                minecraft.getWindow().getGuiScaledWidth(),
                minecraft.getWindow().getGuiScaledHeight(),
                remainingLives,
                Config.Common.CROWN_LIVES_HUD_X_OFFSET.get(),
                Config.Common.CROWN_LIVES_HUD_Y_OFFSET.get()
        );
        for (int i = 0; i < remainingLives; i++) {
            graphics.blit(
                    HEART_TEXTURE,
                    layout.x() + (i * CrownHudLayout.ICON_SIZE),
                    layout.y(),
                    HEART_SPRITE_OFFSET,
                    HEART_SPRITE_OFFSET,
                    HEART_SPRITE_SIZE,
                    HEART_SPRITE_SIZE,
                    16,
                    16
            );
        }
    }
}
