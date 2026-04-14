package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import com.c446.ars_trinkets.item.runes.AbstractRune;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import org.jetbrains.annotations.Contract;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

@EventBusSubscriber
public class OmnipotenceRune extends AbstractRune {
    public OmnipotenceRune(Properties pProperties, int level, ResourceLocation registeredName) {
        super(pProperties, 1, registeredName);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("item.ars_trinkets.omnipotence_crown.desc"));



        if (pTooltipFlag.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("item.ars_trinkets.omnipotence_crown.desc.shift").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = super.getAttributeModifiers(slotContext, id, stack);

        map.put(AttributeRegistry.ALL, new AttributeModifier(id, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        return map;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ArsTrinkets.OMNIPOTENT_PLAYER.add(slotContext.entity().getUUID());
        super.curioTick(slotContext, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ArsTrinkets.OMNIPOTENT_PLAYER.remove(slotContext.entity().getUUID());
        super.onUnequip(slotContext, newStack, stack);
    }

    @SubscribeEvent
    public static void listen(ArmorHurtEvent ev) {
        if (ArsTrinkets.OMNIPOTENT_PLAYER.contains(ev.getEntity().getUUID())) ev.setCanceled(true);
    }
}
