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

/**
 * A tooltip component where particles spawn at the outer edge of the text
 * and spiral inward toward the center — a gravitational vortex effect.
 *
 * Text characters oscillate in scale/offset, pulsing in sync with the spin.
 */
public class SpinningClientTooltipComponent implements ClientTooltipComponent {

    public record SpinningTooltipData(Component text) implements TooltipComponent {}

    private final Component text;
    private static final Map<String, List<OrbitParticle>> PARTICLE_CACHE = new ConcurrentHashMap<>();
    private final RandomSource random = RandomSource.create();

    // -----------------------------------------------------------------------
    // Tuning knobs
    // -----------------------------------------------------------------------

    /** Minimum spawn radius in pixels from text center. */
    private static final float MIN_SPAWN_RADIUS = 20f;
    /** Maximum spawn radius in pixels from text center. */
    private static final float MAX_SPAWN_RADIUS = 45f;

    /** Base inward drift speed (px/tick). Actual speed is randomised around this. */
    private static final float BASE_RADIAL_SPEED = 0.35f;

    /** Base angular speed (rad/tick). All particles spin the same direction for a clean vortex. */
    private static final float BASE_ANGULAR_SPEED = 0.07f;

    /** Max live particles per tooltip key. */
    private static final int MAX_PARTICLES = 120;

    /** Probability of spawning a new particle each tick. */
    private static final float SPAWN_CHANCE = 0.45f;

    // Animated sprite sets (same registry as the flaming component)
    private static final int SOUL_FRAMES   = 11;   // soul_0 … soul_10
    private static final int SMOKE_FRAMES  = 12;   // big_smoke_0 … big_smoke_11
    private static final int TICKS_PER_FRAME = 4;

    // -----------------------------------------------------------------------

    public SpinningClientTooltipComponent(SpinningTooltipData data) {
        this.text = data.text();
    }

    @Override public int getHeight() { return 14; }
    @Override public int getWidth(Font font) { return font.width(text); }

    // -----------------------------------------------------------------------
    // Particle definitions
    // -----------------------------------------------------------------------

    /**
     * A particle that orbits the tooltip's text center in polar coordinates
     * while drifting inward (gravity toward the vortex core).
     */
    private static class OrbitParticle {
        float angle;       // current angle (radians)
        float radius;      // current distance from center (px)
        float angularVel;  // radians per tick — positive = counter-clockwise
        float radialVel;   // px per tick — negative = spiralling inward
        float age;
        int size;
        String baseName;

        OrbitParticle(float angle, float radius, String baseName, RandomSource rand) {
            this.angle      = angle;
            this.radius     = radius;
            this.baseName   = baseName;
            this.angularVel = BASE_ANGULAR_SPEED + rand.nextFloat() * 0.05f;
            this.radialVel  = -(BASE_RADIAL_SPEED + rand.nextFloat() * 0.25f);
            this.size       = 5 + rand.nextInt(5);
        }

        void tick() {
            angle  += angularVel;
            radius += radialVel;
            age++;
        }

        boolean isDead() { return radius <= 1f; }

        /** World-space X given the center. */
        float wx(float cx) { return cx + (float) (Math.cos(angle) * radius); }
        /** World-space Y given the center. */
        float wy(float cy) { return cy + (float) (Math.sin(angle) * radius); }

        ResourceLocation getSpriteId() {
            return ResourceLocation.withDefaultNamespace(baseName);
        }
    }

    /** Animated variant — same orbit logic, cycles through frames as it spirals in. */
    private static class AnimatedOrbitParticle extends OrbitParticle {
        private final int frameCount;
        private final int speed;

        AnimatedOrbitParticle(float angle, float radius, String baseName,
                              int frameCount, int speed, RandomSource rand) {
            super(angle, radius, baseName, rand);
            this.frameCount = frameCount;
            this.speed      = speed;
        }

        @Override
        ResourceLocation getSpriteId() {
            int frame = Math.min((int) (age / speed), frameCount - 1);
            return ResourceLocation.withDefaultNamespace(baseName + "_" + frame);
        }
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int width  = getWidth(font);
        int height = getHeight();

        // Center of the tooltip text in screen space
        float cx = x + width  * 0.5f;
        float cy = y + height * 0.5f;

        String key = text.getString();
        List<OrbitParticle> particles = PARTICLE_CACHE.computeIfAbsent(key, k -> new ArrayList<>());

        // Slightly generous scissor so outer-ring particles aren't clipped harshly
        guiGraphics.enableScissor(
                (int)(x - MAX_SPAWN_RADIUS - 8),
                (int)(y - MAX_SPAWN_RADIUS - 8),
                (int)(x + width  + MAX_SPAWN_RADIUS + 8),
                (int)(y + height + MAX_SPAWN_RADIUS + 8)
        );

        // ---- Spawn new particles ----
        if (particles.size() < MAX_PARTICLES && random.nextFloat() < SPAWN_CHANCE) {
            float spawnAngle  = random.nextFloat() * (float) (2 * Math.PI);
            float spawnRadius = MIN_SPAWN_RADIUS + random.nextFloat() * (MAX_SPAWN_RADIUS - MIN_SPAWN_RADIUS);
            float typeRoll    = random.nextFloat();

            if (typeRoll < 0.40f) {
                particles.add(new AnimatedOrbitParticle(
                        spawnAngle, spawnRadius, "soul", SOUL_FRAMES, TICKS_PER_FRAME, random));
            } else if (typeRoll < 0.65f) {
                particles.add(new AnimatedOrbitParticle(
                        spawnAngle, spawnRadius, "big_smoke", SMOKE_FRAMES, TICKS_PER_FRAME, random));
            } else {
                particles.add(new OrbitParticle(
                        spawnAngle, spawnRadius, "soul_fire_flame", random));
            }
        }

        // ---- Color from Component style ----
        TextColor styleColor = text.getStyle().getColor();
        int colorInt = styleColor != null ? styleColor.getValue() : 0x00FBFF;
        float r = ((colorInt >> 16) & 0xFF) / 255f;
        float g = ((colorInt >>  8) & 0xFF) / 255f;
        float b = ( colorInt        & 0xFF) / 255f;

        TextureAtlas atlas = Minecraft.getInstance().particleEngine.textureAtlas;
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        RenderSystem.enableBlend();

        Iterator<OrbitParticle> it = particles.iterator();
        while (it.hasNext()) {
            OrbitParticle p = it.next();
            p.tick();

            if (p.isDead()) { it.remove(); continue; }

            TextureAtlasSprite sprite = atlas.getSprite(p.getSpriteId());

            // Fade in from the outer edge; fade out near the core
            float maxR  = MAX_SPAWN_RADIUS;
            float normR = Math.min(p.radius / maxR, 1f);   // 1 = outer, 0 = core
            // Fade: ramp up for the first 20 % of the journey, then linearly fade
            float alpha = normR < 0.8f ? normR / 0.8f : 1f;
            alpha *= 0.85f; // general cap

            if (p.baseName.contains("smoke")) {
                RenderSystem.setShaderColor(0.15f, 0.15f, 0.15f, alpha * 0.45f);
            } else {
                RenderSystem.setShaderColor(r, g, b, alpha);
            }

            int px = (int) p.wx(cx) - p.size / 2;
            int py = (int) p.wy(cy) - p.size / 2;
            guiGraphics.blit(px, py, 0, p.size, p.size, sprite);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.disableScissor();
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        String rawString = text.getString();
        long time = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.getGameTime()
                : System.currentTimeMillis() / 50;

        TextColor styleColor = text.getStyle().getColor();
        int textColor = styleColor != null ? styleColor.getValue() : 0xFFFFFF;

        float xOffset = x;
        int len = rawString.length();

        for (int i = 0; i < len; i++) {
            String letter = String.valueOf(rawString.charAt(i));

            // Each character bobs vertically in a wave that rotates with the vortex,
            // so the whole word appears to "spin" in place.
            double phase = time * BASE_ANGULAR_SPEED + (2.0 * Math.PI * i / len);
            float yOff   = (float) (Math.sin(phase) * 1.2);

            // Slight brightness pulse: dim on the "back" of the orbit cycle
            float brightness = 0.75f + 0.25f * (float) Math.cos(phase);
            int r = (int)(((textColor >> 16) & 0xFF) * brightness) & 0xFF;
            int g = (int)(((textColor >>  8) & 0xFF) * brightness) & 0xFF;
            int b = (int)(( textColor        & 0xFF) * brightness) & 0xFF;
            int pulsedColor = (r << 16) | (g << 8) | b;

            font.drawInBatch(
                    letter,
                    xOffset, y + 2 + yOff,
                    pulsedColor | 0xFF000000,
                    true, matrix, buffer,
                    Font.DisplayMode.NORMAL,
                    0, 15728880
            );
            xOffset += font.width(letter);
        }
    }
}