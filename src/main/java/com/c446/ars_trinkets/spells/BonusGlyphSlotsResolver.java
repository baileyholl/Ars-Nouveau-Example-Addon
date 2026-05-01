package com.c446.ars_trinkets.spells;

import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class BonusGlyphSlotsResolver {

    public static final int BASE_SPELL_RECIPE_SIZE = 10;

    private BonusGlyphSlotsResolver() {
    }

    public static int resolveCasterBonus(int originalBonus) {
        int casterBonus = clampToConfiguredCap(originalBonus);
        if (!Config.Common.ENABLE_BONUS_GLYPH_SLOTS.get()) {
            return casterBonus;
        }

        if (casterBonus > 0) {
            return casterBonus;
        }

        return clampToConfiguredCap(Config.Common.BONUS_GLYPH_SLOTS_FALLBACK.get());
    }

    public static int resolveGuiExtraSlots(int originalExtraSlots, @Nullable Player player) {
        int base = Math.max(0, originalExtraSlots);
        if (!Config.Common.ENABLE_BONUS_GLYPH_SLOTS.get()) {
            return base;
        }

        return clampToConfiguredCap(base + getPlayerBonus(player));
    }

    public static int resolveTotalBonus(@Nullable LivingEntity entity, @Nullable AbstractCaster<?> caster) {
        if (!Config.Common.ENABLE_BONUS_GLYPH_SLOTS.get()) {
            return 0;
        }

        int playerBonus = getPlayerBonus(entity);
        int casterBonus = caster == null ? 0 : clampToConfiguredCap(caster.getBonusGlyphSlots());

        if (playerBonus > 0 || casterBonus > 0) {
            return clampToConfiguredCap(playerBonus + casterBonus);
        }

        return clampToConfiguredCap(Config.Common.BONUS_GLYPH_SLOTS_FALLBACK.get());
    }

    public static int maxRecipeSize(@Nullable LivingEntity entity, @Nullable AbstractCaster<?> caster) {
        return BASE_SPELL_RECIPE_SIZE + resolveTotalBonus(entity, caster);
    }

    private static int getPlayerBonus(@Nullable LivingEntity entity) {
        if (entity == null) {
            return 0;
        }

        AttributeInstance instance = entity.getAttribute(AttributeRegistry.BONUS_GLYPH_SLOTS);
        if (instance == null) {
            return 0;
        }

        return clampToConfiguredCap((int) Math.floor(instance.getValue()));
    }

    private static int clampToConfiguredCap(int value) {
        return Math.max(0, Math.min(value, Config.Common.BONUS_GLYPH_SLOTS_CAP.get()));
    }
}

