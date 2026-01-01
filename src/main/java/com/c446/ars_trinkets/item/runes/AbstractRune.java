package com.c446.ars_trinkets.item.runes;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class AbstractRune extends Item implements ICurioItem {
    protected final int level;

    public AbstractRune(Properties pProperties, int level) {
        super(pProperties);
        this.level = level;
    }

    float getMult(){
        return ((float) (this.level * 0.5));
    }
}
