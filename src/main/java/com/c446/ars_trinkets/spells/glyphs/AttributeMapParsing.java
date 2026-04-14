package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Utility class to parse JSON configs and build attribute maps for items or entities.
 */
public class AttributeMapParsing {
    public record AttributeConfigResult(Mode mode, Multimap<Holder<Attribute>, AttributeModifier> map) {
        public enum Mode {
            MERGE, OVERRIDE
        }
    }

    public static Path cfg = FMLPaths.CONFIGDIR.get().toAbsolutePath().resolve(Path.of(Config.Common.CURIOS_FILE_PATH.get()));

    /**
     * Core method to parse a JSON config and build a Multimap of attributes → modifiers.
     *
     * @param id               ResourceLocation of the item or entity.
     * @param customConfigFile Path to the custom JSON config.
     * @param autoLoc          ResourceLocation to resolve auto-generated IDs.
     * @param type             Either "item" or "entity".
     * @return AttributeConfigResult containing the mode and attribute-modifier map.
     */
    public static AttributeConfigResult buildMultiMap(ResourceLocation id, Path customConfigFile, ResourceLocation autoLoc, String type) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        AttributeConfigResult.Mode mode = AttributeConfigResult.Mode.MERGE;

        ArsTrinkets.LOGGER.info("DEBUG: CONFIGURABLE {} FOUND: {}", type.toUpperCase(), id);

        if (!Files.exists(customConfigFile)) {
            ArsTrinkets.LOGGER.error("ERROR: CONFIG FILE NOT FOUND!");
            return new AttributeConfigResult(mode, map);
        }

        try (Reader reader = Files.newBufferedReader(customConfigFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (!root.has(id.toString())) {
                ArsTrinkets.LOGGER.error("ERROR: CONFIG FILE DID NOT HAVE REQUESTED {}!", type.toUpperCase());
                return new AttributeConfigResult(mode, map);
            }

            JsonObject objBlock = root.getAsJsonObject(id.toString());

            // Check type match
            if (objBlock.has("type")) {
                String configType = objBlock.get("type").getAsString();
                if (!configType.equalsIgnoreCase(type)) {
                    ArsTrinkets.LOGGER.warn("SKIPPING CONFIG: TYPE MISMATCH (expected {}, got {})", type, configType);
                    return new AttributeConfigResult(mode, map);
                }
            }

            // Set mode if specified
            if (objBlock.has("mode")) {
                String modeStr = objBlock.get("mode").getAsString().toUpperCase();
                ArsTrinkets.LOGGER.info("MODE \"{}\" FOUND", modeStr);
                mode = AttributeConfigResult.Mode.valueOf(modeStr);
            }

            JsonObject attributes = objBlock.getAsJsonObject("attributes");

            for (var entry : attributes.entrySet()) {
                ResourceLocation attributeId = ResourceLocation.parse(entry.getKey());
                Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId);
                if (attribute == null) continue;

                Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
                JsonArray modifiers = entry.getValue().getAsJsonArray();

                for (JsonElement element : modifiers) {
                    JsonObject modObj = element.getAsJsonObject();

                    ResourceLocation modId = resolveModifierId(modObj.get("id").getAsString(), autoLoc);
                    double amount = modObj.get("amount").getAsDouble();
                    AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(modObj.get("operation").getAsString().toUpperCase());

                    map.put(holder, new AttributeModifier(modId, amount, operation));
                }
            }

        } catch (Exception e) {
            ArsTrinkets.LOGGER.error("FAILED TO PARSE CONFIG FOR {}: {}", type.toUpperCase(), id, e);
        }

        return new AttributeConfigResult(mode, map);
    }

    /**
     * Convenience overload for items.
     */
    public static AttributeConfigResult buildMultiMapForItem(ResourceLocation item, Path configFile, ResourceLocation autoLoc) {
        return buildMultiMap(item, configFile, autoLoc, "item");
    }

    /**
     * Convenience overload for entities.
     */
    public static AttributeConfigResult buildMultiMapForEntity(ResourceLocation entity, Path configFile, ResourceLocation autoLoc) {
        return buildMultiMap(entity, configFile, autoLoc, "entity");
    }

    /**
     * Resolves the ID for the AttributeModifier, using "auto" to generate unique IDs.
     */
    private static ResourceLocation resolveModifierId(String id, ResourceLocation autoLoc) {
        if ("auto".equalsIgnoreCase(id)) {
            // Example: combine autoLoc with a UUID or timestamp for uniqueness
            return new ResourceLocation(autoLoc.getNamespace(), autoLoc.getPath() + "_" + java.util.UUID.randomUUID());
        }
        return ResourceLocation.tryParse(id);
    }

    public static boolean tryApplyTo(@NotNull LivingEntity livingEntity) {
        ResourceLocation loc = BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType());

/*        if (buildMultiMapForEntity(loc, cfg, resolveModifierId("auto", null)));
        */return false;

    }

}
