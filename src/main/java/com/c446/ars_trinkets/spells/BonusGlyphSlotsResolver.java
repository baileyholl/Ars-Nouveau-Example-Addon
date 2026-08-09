package com.c446.ars_trinkets.spells;

import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class BonusGlyphSlotsResolver {

    public static final int BASE_SPELL_RECIPE_SIZE = 10;

    private BonusGlyphSlotsResolver() {
    }

    public static int resolveCasterBonus(int originalBonus) {
        int casterBonus = Math.max(0, originalBonus);
        if (!Config.Common.ENABLE_BONUS_GLYPH_SLOTS.get()) {
            return casterBonus;
        }

        if (casterBonus > 0) {
            return casterBonus;
        }

        return Math.max(0, Config.Common.BONUS_GLYPH_SLOTS_FALLBACK.get());
    }

    public static int resolveGuiExtraSlots(int originalExtraSlots, @Nullable Player player) {
        int base = Math.max(0, originalExtraSlots);
        if (!Config.Common.ENABLE_BONUS_GLYPH_SLOTS.get()) {
            return base;
        }

        if (ServerConfig.INFINITE_SPELLS.get()) {
            return base + getPlayerBonus(player);
        }

        int playerBonus = getPlayerBonus(player);
        if (Config.Common.STACK_BONUS_GLYPH_SLOTS_WITH_INFINITE_SPELLS.get()) {
            return base + playerBonus;
        }

        return Math.max(base, playerBonus);
    }

    public static int resolveTotalBonus(@Nullable LivingEntity entity, @Nullable AbstractCaster<?> caster) {
        if (!Config.Common.ENABLE_BONUS_GLYPH_SLOTS.get()) {
            return 0;
        }

        int playerBonus = getPlayerBonus(entity);
        int casterBonus = caster == null ? 0 : Math.max(0, caster.getBonusGlyphSlots());
        int arsNouveauBonus = getArsNouveauBonusGlyphSlots();
        int casterAndArsNouveauBonus = Math.max(arsNouveauBonus, casterBonus);

        if (ServerConfig.INFINITE_SPELLS.get()) {
            return infiniteModeBonus(arsNouveauBonus, playerBonus, casterBonus);
        }

        if (Config.Common.STACK_BONUS_GLYPH_SLOTS_WITH_INFINITE_SPELLS.get()) {
            if (playerBonus > 0 || casterAndArsNouveauBonus > 0) {
                return playerBonus + casterAndArsNouveauBonus;
            }
        } else if (playerBonus > 0 || casterAndArsNouveauBonus > 0) {
            return Math.max(playerBonus, casterAndArsNouveauBonus);
        }

        return Math.max(0, Config.Common.BONUS_GLYPH_SLOTS_FALLBACK.get());
    }

    public static int maxRecipeSize(@Nullable LivingEntity entity, @Nullable AbstractCaster<?> caster) {
        return BASE_SPELL_RECIPE_SIZE + resolveTotalBonus(entity, caster);
    }

    private static int getArsNouveauBonusGlyphSlots() {
        if (!ServerConfig.INFINITE_SPELLS.get()) {
            return 0;
        }

        return Math.max(0, ServerConfig.INF_SPELLS_LENGHT_MODIFIER.get());
    }

    private static int getPlayerBonus(@Nullable LivingEntity entity) {
        if (entity == null) {
            return 0;
        }

        AttributeInstance instance = entity.getAttribute(AttributeRegistry.BONUS_GLYPH_SLOTS);
        if (instance == null) {
            return 0;
        }

        return Math.max(0, (int) Math.floor(instance.getValue()));
    }

    static int infiniteModeBonus(int arsNouveauBonus, int playerBonus, int casterBonus) {
        // The caster can expose the same native Infinite Spells bonus, so take the larger native value once.
        // Player/item attribute bonuses are flat-additive on top of that native value.
        return Math.max(0, Math.max(arsNouveauBonus, casterBonus)) + Math.max(0, playerBonus);
    }
}
