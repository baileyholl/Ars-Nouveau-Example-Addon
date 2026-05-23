package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.util.PhantomStateManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectPhantomMove extends AbstractEffect {
    public static final String ID = "phantom_move";

    public static final EffectPhantomMove INSTANCE = new EffectPhantomMove("shadow_move", "Fakes the caster's position for the next resolve.");

    public EffectPhantomMove(String tag, String description) {
        super(tag, description);
    }


    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);

        if (!(rayTraceResult.getEntity() instanceof LivingEntity target)) return;

        Vec3 targetPos = target.position();
        PhantomStateManager.PhantomData data = PhantomStateManager.ACTIVE_TRICKS
                .computeIfAbsent(spellContext.getUnwrappedCaster().getUUID(), k -> new PhantomStateManager.PhantomData());
        data.withPosition(targetPos);

    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    public String getName() {
        return ID;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }
}
