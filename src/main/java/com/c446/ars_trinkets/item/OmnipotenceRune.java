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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber
public class OmnipotenceRune extends AbstractRune {
    private static final int EXTRA_LIVES_PER_CYCLE = 9;
    private static final double MAX_HIT_FRACTION = 0.10D;
    private static final int BONUS_SPELL_SLOTS = 10;

    public OmnipotenceRune(Properties pProperties, int level, ResourceLocation registeredName) {
        super(pProperties, level, registeredName);
    }

    static void addGold(List<Component> list, String key) {
        list.add((Component.translatable(key)).withStyle(ChatFormatting.GOLD));
    }

    static void addLineBreak(List<Component> list) {
        list.add(Component.literal("\uE446S\uE446 "));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        //pTooltipComponents.add(Component.translatable("item.ars_trinkets.omnipotence_crown.desc1").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.STRIKETHROUGH));
        //addLineBreak(pTooltipComponents);
        addGold(pTooltipComponents, "item.ars_trinkets.omnipotence_crown.desc3");
        addGold(pTooltipComponents, "item.ars_trinkets.omnipotence_crown.desc4");
        addGold(pTooltipComponents, "item.ars_trinkets.omnipotence_crown.desc5");
        addLineBreak(pTooltipComponents);
        pTooltipComponents.add(Component.translatable("item.ars_trinkets.omnipotence_crown.desc2").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = super.getAttributeModifiers(slotContext, id, stack);

        map.put(AttributeRegistry.ALL, new AttributeModifier(id, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        map.put(
                AttributeRegistry.BONUS_GLYPH_SLOTS,
                new AttributeModifier(id.withSuffix("_bonus_glyph_slots"), BONUS_SPELL_SLOTS, AttributeModifier.Operation.ADD_VALUE)
        );

        return map;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        UUID playerId = slotContext.entity().getUUID();
        ArsTrinkets.OMNIPOTENT_PLAYER.add(playerId);
        remainingLives.putIfAbsent(playerId, EXTRA_LIVES_PER_CYCLE);
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
    public static void capIncomingDamage(LivingDamageEvent.Pre ev) {
        if (!(ev.getEntity() instanceof ServerPlayer serverPlayer)) return;
        if (!ArsTrinkets.OMNIPOTENT_PLAYER.contains(serverPlayer.getUUID())) return;

        float maxAllowedDamage = (float) (serverPlayer.getMaxHealth() * MAX_HIT_FRACTION);
        ev.setNewDamage(Math.min(ev.getNewDamage(), maxAllowedDamage));
    }

    private static int getRemainingLives(UUID playerId) {
        return remainingLives.getOrDefault(playerId, EXTRA_LIVES_PER_CYCLE);
    }

    private static void setRemainingLives(UUID playerId, int value) {
        remainingLives.put(playerId, Math.max(0, value));
    }

    private static void resetLifeCycle(UUID playerId) {
        setRemainingLives(playerId, EXTRA_LIVES_PER_CYCLE);
    }

    private static void handleSaved(ServerPlayer serverPlayer, int livesLeft) {
        serverPlayer.displayClientMessage(Component.translatable("item.ars_trinkets.omnipotence_crown.saved_1"), false);
        serverPlayer.displayClientMessage(Component.translatable("item.ars_trinkets.omnipotence_crown.saved_2", livesLeft), false);
        serverPlayer.setHealth(serverPlayer.getMaxHealth());
        serverPlayer.invulnerableTime = Math.max(40, livesLeft * 40);
        serverPlayer.getFoodData().setFoodLevel(20);
    }

    @SubscribeEvent
    public static void onEntityDeathPre(LivingDeathEvent ev) {
        if (!(ev.getEntity() instanceof ServerPlayer serverPlayer)) return;
        UUID spID = serverPlayer.getUUID();
        if (!ArsTrinkets.OMNIPOTENT_PLAYER.contains(spID)) return;

        int remaining = getRemainingLives(spID);
        if (remaining <= 0) {
            // A true death happened; next life starts with a full stock again.
            resetLifeCycle(spID);
            return;
        }

        int updatedLives = remaining - 1;
        setRemainingLives(spID, updatedLives);
        handleSaved(serverPlayer, updatedLives);
        ev.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent ev) {
        UUID playerId = ev.getEntity().getUUID();
        if (ArsTrinkets.OMNIPOTENT_PLAYER.contains(playerId)) {
            resetLifeCycle(playerId);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent ev) {
        UUID playerId = ev.getEntity().getUUID();
        ArsTrinkets.OMNIPOTENT_PLAYER.remove(playerId);
        remainingLives.remove(playerId);
    }

    @Override
    public @NotNull ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    static HashMap<UUID, Integer> remainingLives = new HashMap<>();
}
