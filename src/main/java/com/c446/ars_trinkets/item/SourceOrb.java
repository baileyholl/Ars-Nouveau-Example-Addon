package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.components.SourceOrbComponent;
import com.c446.ars_trinkets.datagen.ComponentRegistry;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.event.ManaCapEvents;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SourceOrb extends Item implements ICurioItem {
    public SourceOrb(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        var entity = slotContext.entity();
        if (entity instanceof ServerPlayer serverPlayer) {
            //TODO: add mana siphon or sum
            if (!stack.has(ComponentRegistry.SOURCE_ORB_COMPONENT)) {
                stack.set(ComponentRegistry.SOURCE_ORB_COMPONENT, new SourceOrbComponent(0));
            }

            var manaCap = CapabilityRegistry.getMana(entity);
            if (manaCap.getMaxMana() == manaCap.getCurrentMana()) {
                ManaUtil.getManaRegen(serverPlayer);
            }
            ArsTrinkets.SOURCE_ORB_CHECK.add(serverPlayer.getUUID());
        }

        ICurioItem.super.curioTick(slotContext, stack);
    }

    static {


    }
}
