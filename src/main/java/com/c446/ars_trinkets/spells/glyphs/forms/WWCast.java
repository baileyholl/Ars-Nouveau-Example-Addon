package com.c446.ars_trinkets.spells.glyphs.forms;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class WWCast extends AbstractCastMethod {
    public WWCast(String tag, String description) {
        super(tag, description);
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack itemStack, LivingEntity livingEntity, Level level, SpellStats spellStats, SpellContext spellContext, SpellResolver spellResolver) {
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext useOnContext, SpellStats spellStats, SpellContext spellContext, SpellResolver spellResolver) {
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockHitResult, LivingEntity livingEntity, SpellStats spellStats, SpellContext spellContext, SpellResolver spellResolver) {
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack itemStack, LivingEntity livingEntity, Entity entity, InteractionHand interactionHand, SpellStats spellStats, SpellContext spellContext, SpellResolver spellResolver) {
        return CastResolveType.SUCCESS;
    }

    @Override
    protected int getDefaultManaCost() {
        return 50000;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }
}
