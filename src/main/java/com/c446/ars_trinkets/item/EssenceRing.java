package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.Config;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EssenceRing extends Item implements ICurioItem, IManaBonusItem {
    final float manaBonus ;
    final float manaRegen;
    final int id;

    public EssenceRing(Item.Properties p, float manaBonus, float manaRegen, int id) {
        super(p);
        this.manaBonus = manaBonus;
        this.manaRegen = manaRegen;
        this.id = id;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = ICurioItem.super.getAttributeModifiers(slotContext, id, stack);
        doManaOp(id, map);
        return map;
    }

    public double getMult(){
        double mult = 0f;

        try {
            Config.Common.LOTUS_VALUES.get();
            mult = Config.Common.LOTUS_VALUES.get().get(this.id);
        } catch (ArrayIndexOutOfBoundsException ex) {
            mult = 1f;
        }

        return mult;
    }

    @Override
    public double getManaBonus() {
        return this.manaBonus * getMult();
    }

    @Override
    public double getManaRegen() {
        return this.manaRegen * getMult();
    }
}
