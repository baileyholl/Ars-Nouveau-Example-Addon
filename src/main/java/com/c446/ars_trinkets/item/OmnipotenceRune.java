package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.item.runes.AbstractRune;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber
public class OmnipotenceRune extends AbstractRune {
    public OmnipotenceRune(Properties pProperties, int level, ResourceLocation registeredName) {
        super(pProperties, 1, registeredName);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("item.ars_trinkets.omnipotence_crown.desc1").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.STRIKETHROUGH));
        pTooltipComponents.add(Component.translatable("item.ars_trinkets.omnipotence_crown.desc2").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.RED));
        //has the escape codes to mark a spinning tooltip.
        pTooltipComponents.add(Component.literal("second line"));
        pTooltipComponents.add(Component.literal("third line"));

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

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @SubscribeEvent
    public static void listen(ArmorHurtEvent ev) {
        if (ArsTrinkets.OMNIPOTENT_PLAYER.contains(ev.getEntity().getUUID())) ev.setCanceled(true);
    }

    public static void handleSaved(ServerPlayer serverPlayer) {
        serverPlayer.displayClientMessage(Component.translatable("item.ars_trinkets.omnipotence_crown.saved_1"), false);
        serverPlayer.displayClientMessage(Component.translatable("item.ars_trinkets.omnipotence_crown.saved_2", remainingLives.get(serverPlayer.getUUID())), false);
        serverPlayer.setHealth(serverPlayer.getMaxHealth());
        serverPlayer.invulnerableTime = remainingLives.get(serverPlayer.getUUID()) * 40;
        serverPlayer.getFoodData().setFoodLevel(20);
    }

    @SubscribeEvent
    public static void onEntityDeathPre(LivingDeathEvent ev) {
        if (!(ev.getEntity() instanceof ServerPlayer serverPlayer)) return;
        UUID spID = serverPlayer.getUUID();
        if (ArsTrinkets.OMNIPOTENT_PLAYER.contains(spID)) {
            if (remainingLives.containsKey(spID)) {
                int remaining = remainingLives.get(spID);
                if (remaining > 0) {
                    remainingLives.put(spID, remaining - 1);
                    handleSaved(serverPlayer);
                    ev.setCanceled(true);
                } else {
                    remainingLives.put(spID, 9);
                    //player has ran out of lives. reset but still let him die.
                }
            } else {
                remainingLives.put(ev.getEntity().getUUID(), 9); // 9 extra lives
                ev.setCanceled(true);
            }
        }
    }

    @Override
    public @NotNull ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    static HashMap<UUID, Integer> remainingLives = new HashMap<>();
}
