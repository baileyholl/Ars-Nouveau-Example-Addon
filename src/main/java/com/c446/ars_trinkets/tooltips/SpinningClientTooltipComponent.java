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
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

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
public class SpinningClientTooltipComponent implements ClientTooltipComponent {

    private record StyledGlyph(String value, Style style) {
    }

    /**
     * Data carrier for this tooltip component.
     *
     * @param text   The visible tooltip text (rendered by {@link #renderText}).
     * @param phrase Optional phrase to spell out via SGA particles as they orbit.
     *               Only a–z characters are used; anything else is silently skipped.
     *               Pass {@code null} (or use the single-arg constructor) to fall
     *               back to random SGA letters.
     */
    public record SpinningTooltipData(Component text,
                                      String phrase,
                                      String groupKey,
                                      boolean renderParticles,
                                      boolean animateText,
                                      int blockWidth,
                                      int blockLineCount,
                                      int particleOwnerLineIndex,
                                      int centerLineIndex,
                                      ItemStack stack) implements TooltipComponent {
        /**
         * Convenience constructor — uses random SGA letters instead of a phrase.
         */
        public SpinningTooltipData(Component styledContent, ItemStack stk) {
            this(styledContent, null, styledContent.getString(), true, true,
                    0, 1, 0, -1, stk);
        }

        public SpinningTooltipData(Component styledContent, String phrase, String key, ItemStack stk) {
            this(styledContent, phrase, key, true, true,
                    0, 1, 0, -1, stk);
        }

    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    private final Component text;

    /**
     * Alpha-only, lower-cased version of the caller-supplied phrase.
     * {@code null} when no phrase was given (random-letter mode).
     */
    private final String phrase;
    private final String groupKey;
    private final boolean renderParticles;
    private final boolean animateText;
    private final int blockWidth;
    private final int blockLineCount;
    private final int particleOwnerLineIndex;
    private final int centerLineIndex;

    /**
     * Two independent caches — one for the normal view, one for the
     * sneak view — so that the ellipse ratio never bleeds between states.
     */
    private static final Map<String, List<EllipseParticle>> PARTICLE_CACHE =
            new ConcurrentHashMap<>();

    /**
     * Per cache-key cursor that advances by one each time a particle is
     * spawned, so particles spell out the phrase letter-by-letter and loop.
     * Shares the same key space as {@link #PARTICLE_CACHE}.
     */
    private static final Map<String, AtomicInteger> PHRASE_CURSOR =
            new ConcurrentHashMap<>();

    private final RandomSource random = RandomSource.create();

    // -----------------------------------------------------------------------
    // Tuning knobs
    // -----------------------------------------------------------------------

    /**
     * Extra pixels added beyond the text half-width to form the horizontal
     * semi-axis of the ellipse (semiA).
     */
    private static final float EXTRA_SEMI_A = 20f;

    /**
     * Base vertical semi-axis for the single anchor line that owns particles.
     */
    private static final float BASE_SEMI_B = 12f;

    /**
     * Spawn-ring width expressed as a fraction of the ellipse size.
     * Particles appear between {@code SCALE_MIN} and {@code SCALE_MAX}
     * of the full ellipse, then spiral inward.
     */
    private static final float SPAWN_SCALE_MIN = 0.90f;
    private static final float SPAWN_SCALE_MAX = 1.08f;

    /**
     * Base px/tick by which semiA shrinks (inward drift).
     */
    private static final float BASE_SHRINK = 0.28f;

    /**
     * Base angular speed in rad/tick — all particles spin the same direction.
     */
    private static final float BASE_ANGULAR_SPEED = 0.07f;

    /**
     * Hard cap on live particles per cache key.
     */
    private static final int MAX_PARTICLES = 100;

    /**
     * Probability [0, 1] of spawning one new particle per render tick.
     */
    private static final float SPAWN_CHANCE = 0.45f;

    /**
     * Amplitude (px) of the slow sinusoidal center drift applied each frame.
     * X and Y use different frequencies so the motion feels organic.
     */
    private static final float DRIFT_AMP_X = 3.0f;
    private static final float DRIFT_AMP_Y = 1.6f;
    private static final float DRIFT_FREQ_X = 0.040f;   // rad/tick
    private static final float DRIFT_FREQ_Y = 0.031f;   // rad/tick

    // 26 SGA letters (sga_a … sga_z) are vanilla particle-atlas sprites.
    private static final int SGA_COUNT = 26;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public SpinningClientTooltipComponent(SpinningTooltipData data) {
        this.text = data.text();
        // Strip everything that isn't a–z so atlas lookups always succeed.
        String raw = data.phrase();
        this.phrase = (raw != null && !raw.isEmpty())
                ? raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "")
                : null;
        this.groupKey = (data.groupKey() == null || data.groupKey().isBlank())
                ? data.text().getString()
                : data.groupKey();
        this.renderParticles = data.renderParticles();
        this.animateText = data.animateText();
        this.blockWidth = data.blockWidth();
        this.blockLineCount = data.blockLineCount();
        this.particleOwnerLineIndex = data.particleOwnerLineIndex();
        this.centerLineIndex = data.centerLineIndex();
    }

    private static boolean isSneaking() {
        var p = Minecraft.getInstance().player;
        return p != null && p.isShiftKeyDown();
    }

    private static long gameTime() {
        var level = Minecraft.getInstance().level;
        return level != null ? level.getGameTime() : System.currentTimeMillis() / 50L;
    }

    // -----------------------------------------------------------------------
    // ClientTooltipComponent contract
    // -----------------------------------------------------------------------

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public int getWidth(Font font) {
        return font.width(text);
    }

    // -----------------------------------------------------------------------
    // EllipseParticle
    // -----------------------------------------------------------------------

    /**
     * A particle that orbits the tooltip center on a continuously shrinking
     * ellipse.
     *
     * <p>The ratio {@code semiB / semiA} is fixed at birth so the ellipse
     * shape is preserved throughout the inward spiral — only the scale changes.
     *
     * <pre>
     *   x = cx + semiA      * cos(angle)
     *   y = cy + semiA*ratio * sin(angle)
     * </pre>
     */
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

        EllipseParticle(float angle, float initSemiA, float initSemiB,
                        ResourceLocation spriteId, RandomSource rand) {
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

    // -----------------------------------------------------------------------
    // renderImage
    // -----------------------------------------------------------------------

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if (!renderParticles) {
            return;
        }

        int width = getWidth(font);
        int height = getHeight();
        long time = gameTime();
        int lineHeight = getHeight();

        int effectiveBlockWidth = Math.max(width, blockWidth);
        int effectiveBlockLines = Math.max(1, blockLineCount);
        int effectiveOwnerLine = Math.max(0, particleOwnerLineIndex);
        int blockHeight = lineHeight * effectiveBlockLines;

        // ── Ellipse axes ─────────────────────────────────────────────────
        // semiA tracks text width; semiB grows for each extra sneak line.
        float semiA = effectiveBlockWidth * 0.5f + EXTRA_SEMI_A;
        float semiB = Math.max(BASE_SEMI_B, blockHeight * 0.5f + 2f);

        // ── Time-based center drift ───────────────────────────────────────
        // Two independent sinusoids produce a slow, organic Lissajous-like
        // wander of the ellipse center. Frequencies are irrational multiples
        // so the path never repeats on a human-visible timescale.
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

        // ── Particle cache ────────────────────────────────────────────────
        // Separate key for sneak vs non-sneak so the ellipse aspect ratio
        // is always consistent within a given cache bucket.
        String cacheKey = groupKey + (isSneaking() ? "\0sneak" : "");
        List<EllipseParticle> particles =
                PARTICLE_CACHE.computeIfAbsent(cacheKey, k -> new ArrayList<>());

        // Scissor just outside the ellipse bounding box
        float margin = Math.max(semiA, semiB) + 10f;
        guiGraphics.enableScissor(
                (int) (x - margin), (int) (blockTop - margin),
                (int) (x + effectiveBlockWidth + margin), (int) (blockTop + blockHeight + margin)
        );

        // ── Spawn ─────────────────────────────────────────────────────────
        if (particles.size() < MAX_PARTICLES && random.nextFloat() < SPAWN_CHANCE) {
            float spawnAngle = random.nextFloat() * (float) (2 * Math.PI);
            float scale = SPAWN_SCALE_MIN
                    + random.nextFloat() * (SPAWN_SCALE_MAX - SPAWN_SCALE_MIN);

            // If a phrase was supplied, advance the cursor and pick that letter;
            // otherwise fall back to a uniformly random SGA glyph.
            final char letter;
            if (phrase != null && !phrase.isEmpty()) {
                AtomicInteger cursor = PHRASE_CURSOR.computeIfAbsent(
                        cacheKey, k -> new AtomicInteger(0));
                // getAndIncrement wraps naturally via modulo — no locking needed.
                letter = phrase.charAt(cursor.getAndIncrement() % phrase.length());
            } else {
                letter = (char) ('a' + random.nextInt(SGA_COUNT));
            }

            particles.add(new EllipseParticle(
                    spawnAngle,
                    semiA * scale,
                    semiB * scale,
                    ResourceLocation.withDefaultNamespace("sga_" + letter),
                    random));
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

        // ── Tick & draw ───────────────────────────────────────────────────
        Iterator<EllipseParticle> it = particles.iterator();
        while (it.hasNext()) {
            EllipseParticle p = it.next();
            p.tick();
            if (p.isDead()) {
                it.remove();
                continue;
            }

            TextureAtlasSprite sprite = atlas.getSprite(p.spriteId);

            // Alpha: ramp up during the outer 20 % of the journey, then
            // hold at 85 % until the particle dies at the core.
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

    // -----------------------------------------------------------------------
    // renderText — character-wave animation, unchanged from original
    // -----------------------------------------------------------------------

    @Override
    public void renderText(Font font, int x, int y,
                           Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        List<StyledGlyph> glyphs = collectStyledGlyphs();
        if (glyphs.isEmpty()) {
            return;
        }

        if (!animateText) {
            float xOffset = x;
            for (StyledGlyph glyph : glyphs) {
                FormattedCharSequence seq = FormattedCharSequence.forward(glyph.value(), glyph.style());
                font.drawInBatch(
                        seq,
                        xOffset, y + 2,
                        0xFFFFFFFF,
                        true, matrix, buffer,
                        Font.DisplayMode.NORMAL,
                        0, 15728880);
                xOffset += font.width(seq);
            }
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

            TextColor styleColor = glyph.style().getColor();
            int textColor = styleColor != null ? styleColor.getValue() : 0xFFFFFF;

            // Brightness pulse: dim on the "back" of the orbit cycle.
            float brightness = 0.75f + 0.25f * (float) Math.cos(phase);
            int cr = (int) (((textColor >> 16) & 0xFF) * brightness) & 0xFF;
            int cg = (int) (((textColor >> 8) & 0xFF) * brightness) & 0xFF;
            int cb = (int) ((textColor & 0xFF) * brightness) & 0xFF;
            int pulsedColor = (cr << 16) | (cg << 8) | cb;

            Style pulsedStyle = glyph.style().withColor(pulsedColor);
            FormattedCharSequence seq = FormattedCharSequence.forward(glyph.value(), pulsedStyle);

            font.drawInBatch(
                    seq,
                    xOffset, y + 2 + yOff,
                    0xFFFFFFFF,
                    true, matrix, buffer,
                    Font.DisplayMode.NORMAL,
                    0, 15728880);

            xOffset += font.width(seq);
        }
    }

    private List<StyledGlyph> collectStyledGlyphs() {
        List<StyledGlyph> glyphs = new ArrayList<>();
        text.visit((style, segment) -> {
            if (segment == null || segment.isEmpty()) {
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