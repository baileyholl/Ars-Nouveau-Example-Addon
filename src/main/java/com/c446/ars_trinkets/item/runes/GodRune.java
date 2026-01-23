package com.c446.ars_trinkets.item.runes;

import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.platform.services.ICuriosPlatform;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;

public class GodRune extends AbstractRune{
    public GodRune(Properties pProperties, int level, ResourceLocation registeredName) {
        super(pProperties, level, registeredName);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var map = super.getAttributeModifiers(slotContext, id, stack);

        if (this.wasOverridden)return map;
        //TODO : add apoth life steal
        //death
        map.put(Attributes.ATTACK_SPEED, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        //TODO : add apoth bonus regen
        //life
        map.put(Attributes.MAX_HEALTH, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.MOVEMENT_EFFICIENCY, new AttributeModifier(id, getMult()/2f, ADD_MULTIPLIED_TOTAL));
        //warrior
        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.ARMOR, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        map.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, getMult(), ADD_MULTIPLIED_TOTAL));
        //mage
        map.put(PerkAttributes.MAX_MANA, new AttributeModifier(id, getMult(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(PerkAttributes.MANA_REGEN_BONUS, new AttributeModifier(id, getMult(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        map.put(PerkAttributes.SPELL_DAMAGE_BONUS, new AttributeModifier(id, getMult(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return map;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        CuriosApi.getCuriosInventory(slotContext.entity()).ifPresent(i -> {

        });
    }
}
