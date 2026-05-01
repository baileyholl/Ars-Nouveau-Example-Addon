package com.c446.ars_trinkets.tooltips;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.*;

public class WiggleClientTooltipComponent implements ClientTooltipComponent {

    private record StyledGlyph(String value, Style style) {
    }

    public static class WiggleTooltipData implements TooltipComponent {
        final Component text;
        final String phrase;
        final String groupKey;
        final ItemStack stack;

        public WiggleTooltipData(Component text, String phrase, String groupKey, ItemStack stack) {
            this.text = text;
            this.phrase = phrase;
            this.groupKey = groupKey;
            this.stack = stack;

        }
    }

    protected Component text;

    public WiggleClientTooltipComponent(WiggleTooltipData data) {
        this.text = data.text;
        // Strip everything that isn't a–z so atlas lookups always succeed.
        String raw = data.phrase;
        if ((data.groupKey == null || data.groupKey.isBlank())) {
            data.text.getString();
        }
    }

    private static boolean isSneaking() {
        var p = Minecraft.getInstance().player;
        return p != null && p.isShiftKeyDown();
    }

    private static long gameTime() {
        var level = Minecraft.getInstance().level;
        return level != null ? level.getGameTime() : System.currentTimeMillis() / 50L;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public int getWidth(Font font) {
        return font.width(text);
    }

    float BASE_ANGULAR_SPEED = .07f;

    @Override
    public void renderText(@NotNull Font font, int x, int y, @NotNull Matrix4f matrix, MultiBufferSource.@NotNull BufferSource buffer) {
        List<StyledGlyph> glyphs = collectStyledGlyphs();
        if (glyphs.isEmpty()) {
            return;
        }

        long time = gameTime();

        float xOffset = x;
        int len = glyphs.size();

        for (int i = 0; i < len; i++) {
            StyledGlyph glyph = glyphs.get(i);

            // Each character bobs vertically in a wave that rotates with
            // the ellipse angular speed, so the whole word appears to spin.
            double phase = time * BASE_ANGULAR_SPEED + (2.0 * Math.PI * i / len);
            float yOff = (float) (Math.sin(phase) * 1.2);

            int pulsedColor = getPulsedColor(glyph, phase);

            Style pulsedStyle = glyph.style().withColor(pulsedColor);
            FormattedCharSequence seq = FormattedCharSequence.forward(glyph.value(), pulsedStyle);

            font.drawInBatch(seq, xOffset, y + 2 + yOff, 0xFFFFFFFF, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);

            xOffset += font.width(seq);
        }
    }

    private static int getPulsedColor(StyledGlyph glyph, double phase) {
        TextColor styleColor = glyph.style().getColor();
        int textColor = styleColor != null ? styleColor.getValue() : 0xFFFFFF;

        // Brightness pulse: dim on the "back" of the orbit cycle.
        float brightness = 0.75f + 0.25f * (float) Math.cos(phase);
        int cr = (int) (((textColor >> 16) & 0xFF) * brightness) & 0xFF;
        int cg = (int) (((textColor >> 8) & 0xFF) * brightness) & 0xFF;
        int cb = (int) ((textColor & 0xFF) * brightness) & 0xFF;
        return (cr << 16) | (cg << 8) | cb;
    }

    private List<StyledGlyph> collectStyledGlyphs() {
        List<StyledGlyph> glyphs = new ArrayList<>();
        text.visit((style, segment) -> {
            if (segment.isEmpty()) {
                return Optional.empty();
            }

            for (int i = 0; i < segment.length(); i++) {
                glyphs.add(new StyledGlyph(String.valueOf(segment.charAt(i)), style));
            }
            return Optional.empty();
        }, Style.EMPTY);
        return glyphs;
    }
}