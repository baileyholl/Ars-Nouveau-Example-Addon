package com.c446.ars_trinkets.item.runes;

import com.c446.ars_trinkets.glyphs.AttributeMapParsing;
import com.c446.ars_trinkets.registry.ItemRegistry;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;

public class DeathRune extends AbstractRune {
    public DeathRune(Properties pProperties, int level, ResourceLocation registeredName) {
        super(pProperties, level, registeredName);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = super.getAttributeModifiers(slotContext, id, stack);
        if (super.wasOverridden)return map;

        //TODO : add apoth life steal
        map.put(Attributes.ATTACK_SPEED, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));

        return map;
    }
}
