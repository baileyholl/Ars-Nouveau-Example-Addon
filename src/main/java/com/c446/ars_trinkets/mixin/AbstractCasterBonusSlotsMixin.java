package com.c446.ars_trinkets.mixin;

import com.c446.ars_trinkets.spells.BonusGlyphSlotsResolver;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCaster.class)
public abstract class AbstractCasterBonusSlotsMixin {

    @Inject(method = "getBonusGlyphSlots", at = @At("RETURN"), cancellable = true)
    private void arsTrinkets$resolveBonusGlyphSlots(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BonusGlyphSlotsResolver.resolveCasterBonus(cir.getReturnValue()));
    }
}

