package com.c446.ars_trinkets.mixin;

import com.c446.ars_trinkets.spells.BonusGlyphSlotsResolver;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiSpellBook.class)
public abstract class GuiSpellBookMixin {

    @Inject(method = "getExtraGlyphSlots", at = @At("RETURN"), cancellable = true)
    private void arsTrinkets$addPlayerGlyphSlots(CallbackInfoReturnable<Integer> cir) {
        Player player = Minecraft.getInstance().player;
        cir.setReturnValue(BonusGlyphSlotsResolver.resolveGuiExtraSlots(cir.getReturnValue(), player));
    }
}

