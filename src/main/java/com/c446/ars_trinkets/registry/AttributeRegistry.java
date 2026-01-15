package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class AttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, ArsTrinkets.MODID);;
    public static final DeferredHolder<Attribute, Attribute> SPELL_DAMAGE_ABSOLUTE;
    public static final DeferredHolder<Attribute, Attribute> ALL;
    
    static {
        ALL = ATTRIBUTES.register("ars_trinkets.perk.divinity", loc -> new RangedAttribute("increases the value of all attributes", 1, 0d, 10000d));
        SPELL_DAMAGE_ABSOLUTE = ATTRIBUTES.register("ars_trinkets.perk.spell_damage_increase_absolute", loc -> new RangedAttribute("spell damage increase (percent based instead of the flat AN one)", 1d, 0d, 10_000d));
    }

}
