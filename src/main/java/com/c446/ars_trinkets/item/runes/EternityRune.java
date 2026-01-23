package com.c446.ars_trinkets.item.runes;

import com.c446.ars_trinkets.registry.ItemRegistry;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class EternityRune extends AbstractRune {
    public EternityRune(Properties pProperties, int level, ResourceLocation registeredName) {
        super(pProperties, level, registeredName);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = super.getAttributeModifiers(slotContext, id, stack);

        var map_life = ItemRegistry.LIFE_RUNE_2.get().getAttributeModifiers(slotContext, id, stack);
        var map_mage = ItemRegistry.MAGE_RUNE_2.get().getAttributeModifiers(slotContext, id, stack);
        var map_death = ItemRegistry.DEATH_RUNE_2.get().getAttributeModifiers(slotContext, id, stack);
        var map_warrior = ItemRegistry.WARRIOR_RUNE_2.get().getAttributeModifiers(slotContext, id, stack);

        map.putAll(map_life);
        map.putAll(map_mage);
        map.putAll(map_death);
        map.putAll(map_warrior);

        return map;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        if (slotContext.entity() instanceof Player p) {

        }
    }
}
