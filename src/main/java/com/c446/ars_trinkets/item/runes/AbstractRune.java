package com.c446.ars_trinkets.item.runes;

import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.spells.glyphs.AttributeMapParsing;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;

public abstract class AbstractRune extends Item implements ICurioItem {
    protected final int level;
    protected final ResourceLocation registeredName;
    protected boolean wasOverridden = false;

    public AbstractRune(Properties pProperties, int level, ResourceLocation registeredName) {
        super(pProperties);
        this.level = level;
        this.registeredName = registeredName;
    }

    HashMap<ResourceLocation, AttributeMapParsing.AttributeConfigResult> MAP = new HashMap<>();

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            ResourceLocation id,
            ItemStack stack
    ) {
        // Get the base modifiers from the default Curio implementation
        Multimap<Holder<Attribute>, AttributeModifier> base = ICurioItem.super.getAttributeModifiers(slotContext, id, stack);

        if (!Config.COMMON.isLoaded()) {
            return base;
        }

        // Only run for AbstractRune items
        if (!(stack.getItem() instanceof AbstractRune rune)) {
            return base;
        }

        // Compute or fetch the cached config for this rune
        AttributeMapParsing.AttributeConfigResult config = MAP.computeIfAbsent(
                rune.registeredName,
                loc -> AttributeMapParsing.buildMultiMapForItem(
                        loc,
                        AttributeMapParsing.cfg,
                        id
                )
        );

        Multimap<Holder<Attribute>, AttributeModifier> configMap = config.map();
        AttributeMapParsing.AttributeConfigResult.Mode mode = config.mode();

        switch (mode) {
            case OVERRIDE -> {
                this.wasOverridden = true;
                // Return a COPY to avoid mutating the cached map
                return HashMultimap.create(configMap);
            }

            case MERGE -> {
                this.wasOverridden = false;
                // Merge base and config modifiers
                Multimap<Holder<Attribute>, AttributeModifier> merged = HashMultimap.create(base);
                merged.putAll(configMap);
                return merged;
            }

            default -> {
                this.wasOverridden = false;
                return base;
            }
        }
    }

    float getMult() {
        return ((float) (this.level * 0.5));
    }
}
