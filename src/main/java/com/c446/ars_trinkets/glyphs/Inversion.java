package com.c446.ars_trinkets.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Inversion extends AbstractEffect {
    public Inversion(ResourceLocation tag, String description) {
        super(tag, description);
    }

    public static final Inversion INSTANCE = new Inversion(ArsTrinkets.prefix("glyph_inversion"), "Inverts Caster and Targeted.");

    @Override
    protected int getDefaultManaCost() {
        return 500;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if (rayTraceResult.getEntity() instanceof LivingEntity l){
            spellContext.setCaster(l);
        }
    }
}
