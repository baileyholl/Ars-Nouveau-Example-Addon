package com.example.an_addon.glyphs.filters;

import com.example.an_addon.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;

import static com.example.an_addon.ArsTrinkets.MODID;

public class IsSelf extends AbstractEffect {
    public IsSelf(ResourceLocation tag, String description) {
        super(tag, description);
    }

    public static final IsSelf INSTANCE = new IsSelf(new ResourceLocation(MODID, "glyph_filter_self"), "Filter Self");

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, net.minecraft.world.level.Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if (!(rayTraceResult.getEntity().equals(shooter))) {
            spellContext.setCanceled(true);
        }
    }


    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Collections.emptySet();
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.MANIPULATION);
    }
}

