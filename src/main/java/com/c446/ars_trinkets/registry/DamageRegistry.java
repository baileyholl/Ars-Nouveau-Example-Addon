package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class DamageRegistry {
    static final public ResourceKey<DamageType> AIR_SWORD;

    static {
        AIR_SWORD = ResourceKey.create(Registries.DAMAGE_TYPE, ArsTrinkets.prefix("air_sword"));
    }
}
