package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.capabilities.LevelingCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResetLevelItemGeneric extends Item {
    public ResetLevelItemGeneric(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        if (pTooltipFlag.hasShiftDown()) pTooltipComponents.add(Component.translatable("item.ars_trinkets.putrid_heart.desc").withStyle(ChatFormatting.RED));
    }

    @Override
    public InteractionResult useOn(@NotNull UseOnContext pContext) {
        var t = super.useOn(pContext);
        var p = pContext.getPlayer() != null ? pContext.getPlayer() : null;

        if (p != null) {
            LevelingCapability.get(p).reset();
        }
        return t;
    }
}
