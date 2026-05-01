package com.c446.ars_trinkets.tooltips;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A tooltip component where SGA-glyph particles orbit the tooltip text
 * on a time-warped ellipse.
 *
 * <p>The ellipse center drifts with slow sinusoidal noise. When the player is
 * sneaking the vertical semi-axis expands to cover additional tooltip lines,
 * and a separate particle cache is kept for each state so the ellipse ratio
 * never bleeds across transitions.
 *
 * <p>SGA sprites ({@code minecraft:sga_a} … {@code minecraft:sga_z}) are
 * read directly from the vanilla particle atlas — no extra registration needed.
 */
public class SpinningClientTooltipComponent extends WiggleClientTooltipComponent {
    public static class SpinningTooltipData extends WiggleTooltipData {
        int blockWidth;
        int blockLineCount;
        int particleOwnerLineIndex;
        int centerLineIndex;

        public SpinningTooltipData(Component text, String phrase, String groupKey, int blockWidth, int blockLineCount, int particleOwnerLineIndex, int centerLineIndex, ItemStack stack) {
            super(text, phrase, groupKey, stack);

            this.blockWidth=blockWidth;
            this.blockLineCount=blockLineCount;
            this.particleOwnerLineIndex=particleOwnerLineIndex;
            this.centerLineIndex=centerLineIndex;

        }

        public SpinningTooltipData(Component text, String groupeKey, int blockWidth, int blockLineCount, int particleOwnerLineIndex, int centerLineIndex, ItemStack stack) {
            super(text, null, groupeKey, stack);
            this.centerLineIndex=centerLineIndex;
            this.particleOwnerLineIndex=particleOwnerLineIndex;
            this.blockLineCount=blockLineCount;
            this.blockWidth=blockWidth;
        }

    }

    /**
     * Alpha-only, lower-cased version of the caller-supplied phrase.
     * {@code null} when no phrase was given (random-letter mode).
     */
    private final String phrase;
    private final String groupKey;
    private final int blockWidth;
    private final int blockLineCount;
    private final int particleOwnerLineIndex;
    private final int centerLineIndex;


    private static final Map<String, List<EllipseParticle>> PARTICLE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, AtomicInteger> PHRASE_CURSOR = new ConcurrentHashMap<>();

    private final RandomSource random = RandomSource.create();
    private static final float EXTRA_SEMI_A = 20f;
    private static final float BASE_SEMI_B = 12f;
    private static final float SPAWN_SCALE_MIN = 0.90f;
    private static final float SPAWN_SCALE_MAX = 1.08f;
    private static final float BASE_SHRINK = 0.28f;
    private static final float BASE_ANGULAR_SPEED = 0.07f;
    private static final int MAX_PARTICLES = 100;
    private static final float SPAWN_CHANCE = 0.45f;
    private static final float DRIFT_AMP_X = 3.0f;
    private static final float DRIFT_AMP_Y = 1.6f;
    private static final float DRIFT_FREQ_X = 0.040f;   // rad/tick
    private static final float DRIFT_FREQ_Y = 0.031f;   // rad/tick
    private static final int SGA_COUNT = 26;

    public SpinningClientTooltipComponent(SpinningTooltipData data) {
        super(data);
        this.text = data.text;
        String raw = data.phrase;
        this.phrase = (raw != null && !raw.isEmpty()) ? raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "") : null;
        this.groupKey = (data.groupKey == null || data.groupKey.isBlank()) ? data.text.getString() : data.groupKey;
        this.blockWidth = data.blockWidth;
        this.blockLineCount = data.blockLineCount;
        this.particleOwnerLineIndex = data.particleOwnerLineIndex;
        this.centerLineIndex = data.centerLineIndex;
    }

    private static boolean isSneaking() {
        var p = Minecraft.getInstance().player;
        return p != null && p.isShiftKeyDown();
    }

    private static long gameTime() {
        var level = Minecraft.getInstance().level;
        return level != null ? level.getGameTime() : System.currentTimeMillis() / 50L;
    }

    private static class EllipseParticle {
        /**
         * Current horizontal semi-axis (px). Decreases each tick.
         */
        float semiA;
        /**
         * Fixed aspect ratio (semiB / semiA at birth).
         * Keeping this constant means the particle always traces a
         * geometrically similar ellipse as it spirals in.
         */
        final float ratio;
        float angle;
        final float angularVel;   // rad/tick
        final float shrinkRate;   // px/tick removed from semiA
        float age;
        final int size;
        final ResourceLocation spriteId;

        EllipseParticle(float angle, float initSemiA, float initSemiB, ResourceLocation spriteId, RandomSource rand) {
            this.angle = angle;
            this.semiA = initSemiA;
            this.ratio = initSemiB / Math.max(initSemiA, 0.01f);
            this.spriteId = spriteId;
            this.angularVel = BASE_ANGULAR_SPEED + rand.nextFloat() * 0.05f;
            this.shrinkRate = BASE_SHRINK + rand.nextFloat() * 0.15f;
            this.size = 5 + rand.nextInt(5);
        }

        float semiB() {
            return semiA * ratio;
        }

        void tick() {
            angle += angularVel;
            semiA -= shrinkRate;
            age++;
        }

        boolean isDead() {
            return semiA <= 1f;
        }

        float wx(float cx) {
            return cx + (float) (Math.cos(angle) * semiA);
        }

        float wy(float cy) {
            return cy + (float) (Math.sin(angle) * semiB());
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        int width = getWidth(font);
        long time = gameTime();
        int lineHeight = getHeight();

        int effectiveBlockWidth = Math.max(width, blockWidth);
        int effectiveBlockLines = Math.max(1, blockLineCount);
        int effectiveOwnerLine = Math.max(0, particleOwnerLineIndex);
        int blockHeight = lineHeight * effectiveBlockLines;

        float semiA = effectiveBlockWidth * 0.5f + EXTRA_SEMI_A;
        float semiB = Math.max(BASE_SEMI_B, blockHeight * 0.5f + 2f);

        float driftX = (float) (Math.sin(time * DRIFT_FREQ_X) * DRIFT_AMP_X);
        float driftY = (float) (Math.cos(time * DRIFT_FREQ_Y) * DRIFT_AMP_Y);

        float blockTop = y - (effectiveOwnerLine * lineHeight);
        float cx = x + effectiveBlockWidth * 0.5f + driftX;
        float cy;
        if (centerLineIndex >= 0) {
            cy = blockTop + ((centerLineIndex + 0.5f) * lineHeight) + driftY;
        } else {
            cy = blockTop + (blockHeight * 0.5f) + driftY;
        }

        String cacheKey = groupKey + (isSneaking() ? "\0sneak" : "");
        List<EllipseParticle> particles = PARTICLE_CACHE.computeIfAbsent(cacheKey, k -> new ArrayList<>());
        float margin = Math.max(semiA, semiB) + 10f;
        guiGraphics.enableScissor((int) (x - margin), (int) (blockTop - margin), (int) (x + effectiveBlockWidth + margin), (int) (blockTop + blockHeight + margin));
        if (particles.size() < MAX_PARTICLES && random.nextFloat() < SPAWN_CHANCE) {
            float spawnAngle = random.nextFloat() * (float) (2 * Math.PI);
            float scale = SPAWN_SCALE_MIN + random.nextFloat() * (SPAWN_SCALE_MAX - SPAWN_SCALE_MIN);

            final char letter;
            if (phrase != null && !phrase.isEmpty()) {
                AtomicInteger cursor = PHRASE_CURSOR.computeIfAbsent(cacheKey, k -> new AtomicInteger(0));
                // getAndIncrement wraps naturally via modulo — no locking needed.
                letter = phrase.charAt(cursor.getAndIncrement() % phrase.length());
            } else {
                letter = (char) ('a' + random.nextInt(SGA_COUNT));
            }

            particles.add(new EllipseParticle(spawnAngle, semiA * scale, semiB * scale, ResourceLocation.withDefaultNamespace("sga_" + letter), random));
        }

        // ── Color from Component style ────────────────────────────────────
        TextColor styleColor = text.getStyle().getColor();
        int colorInt = styleColor != null ? styleColor.getValue() : 0xFFFFFF;
        float r = ((colorInt >> 16) & 0xFF) / 255f;
        float g = ((colorInt >> 8) & 0xFF) / 255f;
        float b = (colorInt & 0xFF) / 255f;

        TextureAtlas atlas = Minecraft.getInstance().particleEngine.textureAtlas;
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        RenderSystem.enableBlend();

        Iterator<EllipseParticle> it = particles.iterator();
        while (it.hasNext()) {
            EllipseParticle p = it.next();
            p.tick();
            if (p.isDead()) {
                it.remove();
                continue;
            }

            TextureAtlasSprite sprite = atlas.getSprite(p.spriteId);

            float norm = Math.min(p.semiA / semiA, 1f);
            float alpha = (norm < 0.8f ? norm / 0.8f : 1f) * 0.85f;

            RenderSystem.setShaderColor(r, g, b, alpha);

            int px = (int) p.wx(cx) - p.size / 2;
            int py = (int) p.wy(cy) - p.size / 2;
            guiGraphics.blit(px, py, 0, p.size, p.size, sprite);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.disableScissor();
    }
}