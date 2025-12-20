package com.c446.ars_trinkets.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EssenceRing implements ICurioItem, IManaBonusItem {
    final float manaBonus ;
    final float manaRegen;

    public EssenceRing(float manaBonus, float manaRegen) {
        this.manaBonus = manaBonus;
        this.manaRegen = manaRegen;
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
