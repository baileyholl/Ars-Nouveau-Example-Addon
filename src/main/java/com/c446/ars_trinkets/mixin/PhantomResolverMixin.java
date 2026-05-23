package com.c446.ars_trinkets.mixin;

import com.c446.ars_trinkets.util.PhantomStateManager;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*
@Mixin(SpellResolver.class)
public class PhantomResolverMixin {

    @Shadow
    public SpellContext spellContext;

    @Inject(method = "onResolveEffect", at = @At("TAIL"))
    private void arsTrinkets$cleanupPhantomState(Level world, HitResult result, CallbackInfo ci) {
        PhantomStateManager.unregister(this.spellContext.getUnwrappedCaster().getUUID());
    }
}
*/