package com.c446.ars_trinkets.mixin;

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
            method = "castSpell(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/world/InteractionResultHolder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void arsTrinkets$validateRecipeLength(
            Level world,
            LivingEntity entity,
            InteractionHand hand,
            Component invalidMessage,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        if (!Config.Common.ENFORCE_BONUS_GLYPH_SLOTS_ON_CAST.get() || world.isClientSide) {
            return;
        }

        AbstractCaster<?> caster = (AbstractCaster<?>) (Object) this;
        Spell spell = caster.getSpell(world, entity, hand, caster);
        int maxSize = BonusGlyphSlotsResolver.maxRecipeSize(entity, caster);

        if (spell.size() <= maxSize) {
            return;
        }

        if (invalidMessage != null) {
            PortUtil.sendMessageNoSpam(entity, invalidMessage);
        } else {
            PortUtil.sendMessageNoSpam(entity, Component.translatable("text.ars_trinkets.spell_too_large", maxSize));
        }

        cir.setReturnValue(new InteractionResultHolder<>(InteractionResult.SUCCESS, entity.getItemInHand(hand)));
    }
}

