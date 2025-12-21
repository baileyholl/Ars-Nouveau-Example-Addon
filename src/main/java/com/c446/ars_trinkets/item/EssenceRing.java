package com.c446.ars_trinkets.item;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EssenceRing extends Item implements ICurioItem, IManaBonusItem {
    final float manaBonus ;
    final float manaRegen;

    public EssenceRing(Item.Properties p, float manaBonus, float manaRegen) {
        super(p);
        this.manaBonus = manaBonus;
        this.manaRegen = manaRegen;
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
