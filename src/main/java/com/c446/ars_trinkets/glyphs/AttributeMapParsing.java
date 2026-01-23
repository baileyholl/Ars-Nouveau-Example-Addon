package com.c446.ars_trinkets.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class AttributeMapParsing {
    public record AttributeConfigResult(Mode mode, Multimap<Holder<Attribute>, AttributeModifier> map) {
        public enum Mode {
            MERGE, OVERRIDE
        }
    }

    private static ResourceLocation resolveModifierId(String rawId, ResourceLocation autoLoc) {
        if ("auto".equals(rawId)) {
            return (autoLoc);
        }

        return ResourceLocation.parse(rawId);
    }

    public static AttributeConfigResult buildMultiMap(ResourceLocation item, Path customConfigFilePath, ResourceLocation autoLoc) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        AttributeConfigResult.Mode mode = AttributeConfigResult.Mode.MERGE;

        ArsTrinkets.LOGGER.info("DEBUG: CONFIGURABLE ITEM FOUND !\n{}", item);

        if (!Files.exists(customConfigFilePath)) {
            ArsTrinkets.LOGGER.error("ERROR: FILE NOT FOUND!");;
            return new AttributeConfigResult(mode, map);
        }

        try (Reader reader = Files.newBufferedReader(customConfigFilePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (!root.has(item.toString())) {
                ArsTrinkets.LOGGER.error("ERROR: CONFIG FILE DID NOT HAVE REQUESTED ITEM!");
                return new AttributeConfigResult(mode, map);
            }

            JsonObject itemBlock = root.getAsJsonObject(item.toString());

            if (itemBlock.has("mode")) {
                ArsTrinkets.LOGGER.info("MODE \"{}\" FOUND", itemBlock.get("mode"));
                mode = AttributeConfigResult.Mode.valueOf(itemBlock.get("mode").getAsString().toUpperCase());
            }

            JsonObject attributes = itemBlock.getAsJsonObject("attributes");

            for (var entry : attributes.entrySet()) {
                ResourceLocation attributeId = ResourceLocation.parse(entry.getKey());

                Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId);
                if (attribute == null) continue;

                Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);

                JsonArray modifiers = entry.getValue().getAsJsonArray();

                for (JsonElement element : modifiers) {
                    JsonObject obj = element.getAsJsonObject();

                    ResourceLocation id = resolveModifierId(obj.get("id").getAsString(), autoLoc);

                    double amount = obj.get("amount").getAsDouble();

                    AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(obj.get("operation").getAsString().toUpperCase());

                    map.put(holder, new AttributeModifier(id, amount, operation));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AttributeConfigResult(mode, map);
    }
}
