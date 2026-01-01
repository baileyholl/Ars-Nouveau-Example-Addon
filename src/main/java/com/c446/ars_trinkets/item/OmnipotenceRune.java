package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import com.c446.ars_trinkets.item.runes.AbstractRune;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class OmnipotenceRune extends AbstractRune {
    public OmnipotenceRune(Properties pProperties, int level) {
        super(pProperties, 1);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = super.getAttributeModifiers(slotContext, id, stack);

        map.put(AttributeRegistry.ALL, new AttributeModifier(id, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        return map;
    }
}
