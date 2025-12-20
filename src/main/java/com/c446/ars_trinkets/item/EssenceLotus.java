package com.c446.ars_trinkets.item;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import static com.hollingsworth.arsnouveau.api.perk.PerkAttributes.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

public class EssenceLotus implements ICurioItem, IManaBonusItem {
    final float manaRegen;
    final float manaBonus;

    public EssenceLotus(float manaRegen, float manaBonus) {
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
    public float getManaBonus() {
        return this.manaBonus;
    }

    @Override
    public float getManaRegen() {
        return this.manaRegen;
    }
}
