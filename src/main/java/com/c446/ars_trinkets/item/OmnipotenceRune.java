package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.network.CrownLivesPayload;
import com.c446.ars_trinkets.item.runes.AbstractRune;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.google.common.collect.Multimap;
import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class OmnipotenceRune extends AbstractRune {
    private static final int EXTRA_LIVES_PER_CYCLE = 9;
    private static final double MAX_HIT_FRACTION = 0.10D;
    private static final int BONUS_SPELL_SLOTS = 10;
    private static final ExtraLifeTracker LIFE_TRACKER = new ExtraLifeTracker(EXTRA_LIVES_PER_CYCLE);
    private static final Map<UUID, Integer> LAST_SENT_LIVES = new HashMap<>();

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
        /*map.put(
                CaelusApi.getInstance().getFallFlyingAttribute(),
                new AttributeModifier(id.withSuffix("_fall_flying"), 1.0D, AttributeModifier.Operation.ADD_VALUE)
        );*/

        return map;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        UUID playerId = slotContext.entity().getUUID();
        ArsTrinkets.OMNIPOTENT_PLAYER.add(playerId);
        LIFE_TRACKER.equip(playerId);
        if (slotContext.entity() instanceof ServerPlayer serverPlayer) {
            enableCreativeFlight(serverPlayer);
            sendRemainingLives(serverPlayer);
        }
        super.curioTick(slotContext, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        UUID playerId = slotContext.entity().getUUID();
        ArsTrinkets.OMNIPOTENT_PLAYER.remove(playerId);
        LIFE_TRACKER.unequip(playerId);
        LAST_SENT_LIVES.remove(playerId);
        if (slotContext.entity() instanceof ServerPlayer serverPlayer) {
            sendRemainingLives(serverPlayer);
        }
        if (slotContext.entity() instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative()) {
            serverPlayer.getAbilities().mayfly = false;
            serverPlayer.getAbilities().flying = false;
            serverPlayer.onUpdateAbilities();
        }
        super.onUnequip(slotContext, newStack, stack);
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return slotContext.entity() instanceof ServerPlayer serverPlayer && (serverPlayer.isCreative());
    }

    @SubscribeEvent
    public static void capIncomingDamage(LivingDamageEvent.Pre ev) {
        if (!(ev.getEntity() instanceof ServerPlayer serverPlayer)) return;
        if (!ArsTrinkets.OMNIPOTENT_PLAYER.contains(serverPlayer.getUUID())) return;

        float maxAllowedDamage = (float) (serverPlayer.getMaxHealth() * MAX_HIT_FRACTION);
        ev.setNewDamage(Math.min(ev.getNewDamage(), maxAllowedDamage));
    }

    private static void handleSaved(ServerPlayer serverPlayer, int livesLeft) {
        serverPlayer.displayClientMessage(Component.translatable("item.ars_trinkets.omnipotence_crown.saved_1"), false);
        serverPlayer.displayClientMessage(Component.translatable("item.ars_trinkets.omnipotence_crown.saved_2", livesLeft), false);
        serverPlayer.setHealth(serverPlayer.getMaxHealth());
        serverPlayer.invulnerableTime = Math.max(40, livesLeft * 40);
        serverPlayer.getFoodData().setFoodLevel(20);
        serverPlayer.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onEntityDeathPre(LivingDeathEvent ev) {
        if (!(ev.getEntity() instanceof ServerPlayer serverPlayer)) return;
        UUID spID = serverPlayer.getUUID();
        if (!ArsTrinkets.OMNIPOTENT_PLAYER.contains(spID)) return;

        if (!LIFE_TRACKER.tryPreventDeath(spID)) {
            sendRemainingLives(serverPlayer);
            return;
        }

        int updatedLives = LIFE_TRACKER.remaining(spID);
        handleSaved(serverPlayer, updatedLives);
        sendRemainingLives(serverPlayer);
        ev.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent ev) {
        UUID playerId = ev.getEntity().getUUID();
        if (ArsTrinkets.OMNIPOTENT_PLAYER.contains(playerId)) {
            LIFE_TRACKER.respawn(playerId);
            if (ev.getEntity() instanceof ServerPlayer serverPlayer) {
                sendRemainingLives(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent ev) {
        UUID playerId = ev.getEntity().getUUID();
        ArsTrinkets.OMNIPOTENT_PLAYER.remove(playerId);
        LIFE_TRACKER.unequip(playerId);
        LAST_SENT_LIVES.remove(playerId);
        if (ev.getEntity() instanceof ServerPlayer serverPlayer) {
            sendRemainingLives(serverPlayer);
        }
    }

    @Override
    public @NotNull ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    private static void enableCreativeFlight(ServerPlayer serverPlayer) {
        if (!serverPlayer.getAbilities().mayfly) {
            serverPlayer.getAbilities().mayfly = true;
            serverPlayer.onUpdateAbilities();
        }
    }

    private static void sendRemainingLives(ServerPlayer serverPlayer) {
        UUID playerId = serverPlayer.getUUID();
        int lives = ArsTrinkets.OMNIPOTENT_PLAYER.contains(playerId) ? LIFE_TRACKER.remaining(playerId) : 0;
        Integer previous = LAST_SENT_LIVES.put(playerId, lives);
        if (previous != null && previous == lives) return;
        PacketDistributor.sendToPlayer(serverPlayer, new CrownLivesPayload(lives));
    }
}
