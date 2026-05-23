package com.c446.ars_trinkets.mixin;

import com.c446.ars_trinkets.util.PhantomStateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*
@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getLookAngle", at = @At("RETURN"), cancellable = true)
    private void arsTrinkets$interceptLookAngle(CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        PhantomStateManager.PhantomData data = PhantomStateManager.get(entity.getUUID());
        if (data != null && data.look != null && PhantomStateManager.isSpellContext()) {
            cir.setReturnValue(data.look);
        }
    }

    @Inject(method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void arsTrinkets$interceptEyePosition(CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        PhantomStateManager.PhantomData data = PhantomStateManager.get(entity.getUUID());
        if (data != null && data.position != null && PhantomStateManager.isSpellContext()) {
            cir.setReturnValue(data.position);
        }
    }

    @Inject(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void arsTrinkets$interceptEyePositionPartial(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        PhantomStateManager.PhantomData data = PhantomStateManager.get(entity.getUUID());
        if (data != null && data.position != null && PhantomStateManager.isSpellContext()) {
            cir.setReturnValue(data.position);
        }
    }

    @Inject(method = "position()Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void arsTrinkets$interceptPosition(CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        PhantomStateManager.PhantomData data = PhantomStateManager.get(entity.getUUID());
        if (data != null && data.position != null && PhantomStateManager.isSpellContext()) {
            cir.setReturnValue(data.position);
        }
    }
}
*/