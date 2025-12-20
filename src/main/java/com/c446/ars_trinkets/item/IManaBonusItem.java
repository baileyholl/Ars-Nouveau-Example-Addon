package com.c446.ars_trinkets.item;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import static com.hollingsworth.arsnouveau.api.perk.PerkAttributes.*;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

public interface IManaBonusItem {
    float getManaBonus();
    float getManaRegen();

    default void doManaOp(ResourceLocation id, Multimap<Holder<Attribute>, AttributeModifier> map){
        map.put(MAX_MANA, new AttributeModifier(id, this.getManaBonus(), ADD_VALUE));
        map.put(MANA_REGEN_BONUS, new AttributeModifier(id, this.getManaRegen(), ADD_VALUE));
    }
}
