package com.c446.ars_trinkets.item.runes;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.glyphs.AttributeMapParsing;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.gen.throwables.InvalidAccessorException;
import org.w3c.dom.Attr;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.nio.file.Path;
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
    ) throws RuntimeException {
        var base = ICurioItem.super.getAttributeModifiers(slotContext, id, stack);
        if (Config.COMMON.isLoaded()) {

            if (!(stack.getItem() instanceof AbstractRune rune)) {
                return base;
            }

            var config = MAP.computeIfAbsent(
                    rune.registeredName,
                    loc -> AttributeMapParsing.buildMultiMap(
                            loc,
                            FMLPaths.CONFIGDIR.get().toAbsolutePath().resolve(Path.of(Config.Common.CURIOS_FILE_PATH.get())),
                            id
                    )
            );

            var configMap = config.map();
            var mode = config.mode();

            switch (mode) {
                case OVERRIDE -> {
//                    ArsTrinkets.LOGGER.debug("FOUND CONFIG IN OVERRIDE MODE for {}", rune.registeredName);
                    this.wasOverridden = true;

                    // Return a COPY to avoid shared mutable state bugs
                    return HashMultimap.create(configMap);
                }

                case MERGE -> {
  //                  ArsTrinkets.LOGGER.debug("FOUND CONFIG IN MERGE MODE for {}", rune.registeredName);
                    this.wasOverridden = false;

                    var merged = HashMultimap.create(base);
                    merged.putAll(configMap);
                    return merged;
                }

                default -> {
    //                ArsTrinkets.LOGGER.debug("FOUND CONFIG IN DEFAULT MODE for {}", rune.registeredName);
                    this.wasOverridden = false;
                    return base;
                }

            }


        }
        else {return base;}
    }

    float getMult() {
        return ((float) (this.level * 0.5));
    }
}
