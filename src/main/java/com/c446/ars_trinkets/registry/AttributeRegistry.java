package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.api.perk.PerkAttributes.*;
public class AttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES;
    public static final DeferredHolder<Attribute, Attribute> SPELL_DAMAGE_ABSOLUTE;

    static {
        ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ArsTrinkets.MODID);

        SPELL_DAMAGE_ABSOLUTE =registerAttribute("spell_damage_increase", (id) ->  new RangedAttribute(id, 1d, 0d, 10_000d), "1d924b1e-db1a-4d49-a5cd-2c35f9f74c70");
    }

}
