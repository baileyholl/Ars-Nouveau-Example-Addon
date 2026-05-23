package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.util.PhantomStateManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectPhantomLook extends AbstractEffect {
    public static final String ID = "phantom_look";

    public static final EffectPhantomLook INSTANCE = new EffectPhantomLook("shadow_look", "Fakes the caster's position for the next resolve.");

    public EffectPhantomLook(String tag, String description) {
        super(tag, description);
    }


    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, context, resolver);

        if (!(rayTraceResult.getEntity() instanceof LivingEntity target)) {
            return;
        }

        Vec3 lookDirection = target.position().subtract(context.getCaster().getPosition()).normalize();
        PhantomStateManager.PhantomData data = PhantomStateManager.ACTIVE_TRICKS
                .computeIfAbsent(context.getUnwrappedCaster().getUUID(), k -> new PhantomStateManager.PhantomData());
        data.withLook(lookDirection);

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
        return Set.of(
                //PHI is the vertical axis.
                AugmentExtract.INSTANCE,      // phi   += 90°
                AugmentAmplify.INSTANCE,      // phi   += 60°
                AugmentDampen.INSTANCE,       // phi   += 30°
                AugmentFortune.INSTANCE,      // theta += 90°
                AugmentExtendTime.INSTANCE,   // theta += 60°
                AugmentDurationDown.INSTANCE, // theta += 30°
                AugmentAOE.INSTANCE,          // theta is anti-clockwise.
                AugmentRandomize.INSTANCE,    // phi is anti-clockwise.
                AugmentSensitive.INSTANCE,    // both are anti-clockwise.
                AugmentPierce.INSTANCE,       // UNASSIGNED
                AugmentSplit.INSTANCE         // UNASSIGNED
        );
    }
}
