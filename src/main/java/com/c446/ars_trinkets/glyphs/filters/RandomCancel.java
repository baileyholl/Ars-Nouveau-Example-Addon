package com.c446.ars_trinkets.glyphs.filters;

import com.c446.ars_trinkets.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.c446.ars_trinkets.ArsTrinkets.prefix;

public class RandomCancel extends AbstractEffect {
    public static final RandomCancel QUARTER = new RandomCancel(prefix("glyph_random_25"), "has a 25% chance of cancelling the spell", 0.25f);
    public static final RandomCancel HALF = new RandomCancel(prefix("glyph_random_50"), "has a 50% chance of cancelling the spell", 0.5f);
    public static final RandomCancel THREE_FOURTHS = new RandomCancel(prefix("glyph_random_75"), "has a 75% chance of cancelling the spell", 0.75f);

    float odd; // odd to cancle the cast
    public RandomCancel(ResourceLocation tag, String description, float odd) {
        super(tag, description);
        this.odd = odd;
    }

    @Override
    protected int getDefaultManaCost() {
        return 50;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel level && level.random.nextDouble() <= this.odd) {
            spellContext.setCanceled(true);
        }
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }
}
