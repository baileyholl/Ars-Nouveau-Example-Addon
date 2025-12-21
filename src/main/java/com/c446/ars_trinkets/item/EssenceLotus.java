package com.c446.ars_trinkets.item;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EssenceLotus extends Item implements ICurioItem, IManaBonusItem {
    final double manaRegen;
    final double manaBonus;

    public EssenceLotus(Properties p, Double manaRegen, double manaBonus) {
        super(p);
        this.manaRegen = manaRegen;
        this.manaBonus = manaBonus;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = ICurioItem.super.getAttributeModifiers(slotContext, id, stack);
        doManaOp(id, map);
        return map;
    }

    @Override
    public double getManaBonus() {
        return this.manaBonus;
    }

    @Override
    public double getManaRegen() {
        return this.manaRegen;
    }
}
