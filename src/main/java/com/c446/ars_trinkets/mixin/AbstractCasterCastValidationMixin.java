package com.c446.ars_trinkets.mixin;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.spells.BonusGlyphSlotsResolver;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCaster.class)
public abstract class AbstractCasterCastValidationMixin {

    @Inject(
            method = "castSpell(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/network/chat/Component;Lcom/hollingsworth/arsnouveau/api/spell/Spell;)Lnet/minecraft/world/InteractionResultHolder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void arsTrinkets$validateRecipeLength(
            Level worldIn,
            LivingEntity playerIn,
            InteractionHand handIn,
            Component invalidMessage,
            Spell spell,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        if (!Config.Common.ENFORCE_BONUS_GLYPH_SLOTS_ON_CAST.get() || worldIn.isClientSide) {
            return;
        }

        AbstractCaster<?> caster = (AbstractCaster<?>) (Object) this;
        int maxSize = BonusGlyphSlotsResolver.maxRecipeSize(playerIn, caster);

        if (spell.size() <= maxSize) {
            return;
        }

        PortUtil.sendMessageNoSpam(
                playerIn,
                Component.translatable("text.ars_trinkets.spell_too_large")
        );

        ArsTrinkets.LOGGER.warn(
                "Rejected spell cast by {}: {} glyphs exceeds the allowed maximum of {}. "
                        + "Configure ars_trinkets.bonus_glyph_slots.enforce_on_cast=true/false "
                        + "to enable/disable enforcement and "
                        + "ars_trinkets.bonus_glyph_slots.stack_with_infinite_spells=true/false "
                        + "to enable/disable stacking with Ars Nouveau's Infinite Spells bonus.",
                playerIn.getName().getString(),
                spell.size(),
                maxSize
        );

        cir.setReturnValue(new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn)));
    }
}
