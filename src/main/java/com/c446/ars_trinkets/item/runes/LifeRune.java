package com.c446.ars_trinkets.item.runes;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;

public class LifeRune extends AbstractRune {
    public LifeRune(Properties pProperties, int level) {
        super(pProperties, level);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = super.getAttributeModifiers(slotContext, id, stack);

        //TODO : add apoth bonus regen
        map.put(Attributes.MAX_HEALTH, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.MOVEMENT_EFFICIENCY, new AttributeModifier(id, getMult()/2f, ADD_MULTIPLIED_TOTAL));

        return map;
    }
}
