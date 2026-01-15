package com.c446.ars_trinkets.glyphs.screen_stuff;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class OpenGuiGlyph extends AbstractEffect {
    static final public OpenGuiGlyph OPEN_GUI = new OpenGuiGlyph("open_gui", "opens a debug GUI");

    public OpenGuiGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }



    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);


    }
}
