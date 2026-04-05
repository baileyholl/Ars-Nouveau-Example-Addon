package com.c446.ars_trinkets.tooltips;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlamingClientTooltipComponent implements ClientTooltipComponent {
    public record FlamingTooltipData(Component text) implements TooltipComponent {}

    private final Component text;
    private static final Map<String, List<TooltipParticle>> PARTICLE_CACHE = new ConcurrentHashMap<>();
    private final RandomSource random = RandomSource.create();

    // --- Animation Constants ---
    // Change these to adjust the feel of your particles
    private static final int SOUL_FRAMES = 11;       // soul_0 to soul_10
    private static final int SMOKE_FRAMES = 12;      // big_smoke_0 to big_smoke_11
    private static final int TICKS_PER_FRAME = 5;    // 4 FPS (20 ticks / 5)

    private static final ResourceLocation SOUL_FIRE_FLAME = ResourceLocation.withDefaultNamespace("soul_fire_flame");

    public FlamingClientTooltipComponent(FlamingTooltipData data) {
        this.text = data.text();
    }

    @Override public int getHeight() { return 14; }
    @Override public int getWidth(Font font) { return font.width(text); }

    // --- Particle Logic ---

    private static class TooltipParticle {
        float x, y, vx, vy, age, maxAge;
        float noiseSeed;
        int size;
        String baseName;

        TooltipParticle(float x, float y, String baseName, RandomSource rand) {
            this.x = x; this.y = y;
            this.baseName = baseName;
            this.vx = (rand.nextFloat() - 0.5f) * 0.15f;
            this.vy = -0.06f - rand.nextFloat() * 0.1f;
            this.maxAge = 30 + rand.nextInt(30);
            this.noiseSeed = rand.nextFloat() * 100f;
            this.size = 6 + rand.nextInt(4);
        }

        void tick() {
            x += (float) (vx + (Math.sin(age * 0.1 + noiseSeed) * 0.05));
            y += vy;
            age++;
        }

        boolean isDead() { return age >= maxAge; }

        ResourceLocation getSpriteId() {
            return ResourceLocation.withDefaultNamespace(baseName);
        }
    }

    private static class MultiFrameTooltipParticle extends TooltipParticle {
        private final int frameCount;
        private final int speed;

        MultiFrameTooltipParticle(float x, float y, String baseName, int frameCount, int speed, RandomSource rand) {
            super(x, y, baseName, rand);
            this.frameCount = frameCount;
            this.speed = speed;
            // Life is strictly tied to completing the animation cycle
            this.maxAge = (float) frameCount * speed;
        }

        @Override
        ResourceLocation getSpriteId() {
            int frame = Math.min((int) (age / speed), frameCount - 1);
            return ResourceLocation.withDefaultNamespace(baseName + "_" + frame);
        }
    }

    // --- Rendering ---

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int width = getWidth(font);
        int height = getHeight();
        String key = text.getString();
        List<TooltipParticle> particles = PARTICLE_CACHE.computeIfAbsent(key, k -> new ArrayList<>());

        guiGraphics.enableScissor(x - 15, y - 40, x + width + 15, y + height + 15);

        // Random Spawning Logic
        if (particles.size() < 150 && random.nextFloat() < 0.3f) {
            float spawnX = x + random.nextInt(width);
            float spawnY = y + 8;
            float rType = random.nextFloat();

            if (rType < 0.4f) {
                // Soul animation (0-10)
                particles.add(new MultiFrameTooltipParticle(spawnX, spawnY, "soul", SOUL_FRAMES, TICKS_PER_FRAME, random));
            } else if (rType < 0.7f) {
                // Smoke animation (0-11)
                particles.add(new MultiFrameTooltipParticle(spawnX, spawnY, "big_smoke", SMOKE_FRAMES, TICKS_PER_FRAME, random));
            } else {
                // Static Soul Fire Flame
                particles.add(new TooltipParticle(spawnX, spawnY, "soul_fire_flame", random));
            }
        }

        // Color setup from Component style
        TextColor styleColor = text.getStyle().getColor();
        int colorInt = styleColor != null ? styleColor.getValue() : 0x00FBFF;
        float r = ((colorInt >> 16) & 0xFF) / 255f;
        float g = ((colorInt >> 8) & 0xFF) / 255f;
        float b = (colorInt & 0xFF) / 255f;

        TextureAtlas atlas = Minecraft.getInstance().particleEngine.textureAtlas;
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        RenderSystem.enableBlend();

        Iterator<TooltipParticle> it = particles.iterator();
        while (it.hasNext()) {
            TooltipParticle p = it.next();
            p.tick();

            if (p.isDead()) {
                it.remove();
                continue;
            }

            TextureAtlasSprite sprite = atlas.getSprite(p.getSpriteId());

            float lifePct = p.age / p.maxAge;
            float alpha = 1.0f - lifePct;

            // Differentiate colors/opacity based on type
            if (p.baseName.contains("smoke")) {
                RenderSystem.setShaderColor(0.2f, 0.2f, 0.2f, alpha * 0.5f); // Dark smoke
            } else {
                RenderSystem.setShaderColor(r, g, b, alpha * 0.9f); // Glowing soul bits
            }

            guiGraphics.blit((int)p.x, (int)p.y, 0, p.size, p.size, sprite);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        guiGraphics.disableScissor();
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        String rawString = text.getString();
        long time = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : System.currentTimeMillis() / 50;
        float xOffset = x;

        TextColor styleColor = text.getStyle().getColor();
        int textColor = styleColor != null ? styleColor.getValue() : 0xFFFFFF;

        for (int i = 0; i < rawString.length(); i++) {
            String letter = String.valueOf(rawString.charAt(i));
            float yOff = (float) (Math.sin(time * 0.15 + i) * 0.8);
            font.drawInBatch(letter, xOffset, y + 2 + yOff, textColor, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
            xOffset += font.width(letter);
        }
    }
}

