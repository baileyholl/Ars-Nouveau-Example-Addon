package com.c446.ars_trinkets.capabilities;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.datagen.AttributeTagsProviders;
import com.c446.ars_trinkets.entities.red_lightning.RedLightning;
import com.c446.ars_trinkets.events.LevelModifiedEvent;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

import static com.c446.ars_trinkets.registry.AttributeRegistry.ATTRIBUTES;

@EventBusSubscriber
public class PlayerLevelHandling {
    //TODO: stop using the old hacky way and use AttributeFix to override default maximum value.
    @SubscribeEvent
    public static void addMana(MaxManaCalcEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (p.hasData(CapabilityRegistry.LEVEL_CAP)) {
                var cap = p.getData(CapabilityRegistry.LEVEL_CAP);

                if (cap.level == 0) return;

                else {
                    e.setMax(e.getMax() + cap.getBonusMana());
                }
            }
        }
    }

    @SubscribeEvent
    public static void addRegen(ManaRegenCalcEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (p.hasData(CapabilityRegistry.LEVEL_CAP)) {
                var cap = p.getData(CapabilityRegistry.LEVEL_CAP);

                if (cap.level == 0) return;

                else {
                    e.setRegen(e.getRegen() + cap.getBonusRegen());
                }
            }
        }
    }

    //TODO: patchouli book, add titles back in, on level-up broadcasting etc...
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre d) {
        //System.out.println(FMLPaths.CONFIGDIR.get());
        if (d.getSource().getEntity() instanceof Player attacker && attacker.hasData(CapabilityRegistry.LEVEL_CAP)) {
            d.setNewDamage((float) (d.getNewDamage() * attacker.getData(CapabilityRegistry.LEVEL_CAP).getDamageMult()));
        }

        if (d.getEntity() instanceof Player victim) {
            d.setNewDamage((float) (d.getNewDamage() / victim.getData(CapabilityRegistry.LEVEL_CAP).getDamageMult()));
        }
    }

    @SubscribeEvent
    public static void onLevelUp(LevelModifiedEvent.Pre e) {
        ArsTrinkets.LOGGER.debug("current level : {}", e.levelCurrent);
        ArsTrinkets.LOGGER.debug("next level : {}", e.newLevel);

        var cap = LevelingCapability.get(e.entity);

        ArsTrinkets.LOGGER.debug("mana bonus : {}", cap.getBonusMana());
        ArsTrinkets.LOGGER.debug("mana regen : {}", cap.getBonusRegen());
        ArsTrinkets.LOGGER.debug("damage mult: {}", cap.getDamageMult());

        if (cap.level >= Config.Common.MAX_LEVEL_ALLOWED.getAsInt()) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLevelUpPost(LevelModifiedEvent.Post e){
        if (e.entity instanceof ServerPlayer sp) {
            var cap = sp.getData(CapabilityRegistry.LEVEL_CAP);
            sp.displayClientMessage(Component.translatable("text.ars_trinkets.ritual_" + (cap.level)).append(cap.getTitle()), false);
        }
    }

    @SubscribeEvent
    public static void spellDamageEvent(SpellDamageEvent e) {
        try {
            e.damage *= (float) e.caster.getAttributeValue(AttributeRegistry.SPELL_DAMAGE_ABSOLUTE);
        } catch (IllegalArgumentException ignored){

        }
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().stream().filter(e -> e == EntityType.PLAYER).forEach(e -> {
            ATTRIBUTES.getEntries().forEach((v) -> {
                event.add(e, v);
            });
        });
    }


    static final ResourceLocation DIVINITY_STAT_BOOST_LOCATION = ArsTrinkets.prefix("divinity_stat_boost");
    static final HashMap<UUID, Double> LAST_DIVINITY_VALUE = new HashMap<>();
    // Track last attribute values per player to detect changes
    static final HashMap<UUID, HashMap<Holder<Attribute>, Double>> LAST_ATTRIBUTE_VALUES = new HashMap<>();

    private static void removeDivBoost(Map.Entry<Holder<Attribute>, AttributeInstance> ai) {
        if (ai.getValue().removeModifier(DIVINITY_STAT_BOOST_LOCATION)) {
            //ArsTrinkets.LOGGER.debug("successfully removed " + ai.getKey().getRegisteredName() + "'s boost.");
        }
    }

    private static boolean shouldIncludeAttribute(Map.Entry<Holder<Attribute>, AttributeInstance> ai) {
        // Always exclude if in blacklist
        if (ai.getKey().is(AttributeTagsProviders.DIVINITY_BLACKLIST)) {
            return false;
        }

        double value = ai.getValue().getValue();

        // Check "no increase if negative" tag
        if (ai.getKey().is(AttributeTagsProviders.DIVINITY_NO_INCREASE_IF_NEGATIVE) && value < 0) {
            //ArsTrinkets.LOGGER.warn("attribute {} is negative and shouldn't be increased.", ai.getKey().getRegisteredName());
            removeDivBoost(ai);
            return false;
        }

        // Check "no increase if positive" tag
        if (ai.getKey().is(AttributeTagsProviders.DIVINITY_NO_INCREASE_IF_POSITIVE) && value > 0) {
            //ArsTrinkets.LOGGER.warn("attribute {} is positive and shouldn't be increased.", ai.getKey().getRegisteredName());
            removeDivBoost(ai);
            return false;
        }

        return true;
    }

    @SubscribeEvent
    public static void tickEntity(PlayerTickEvent.Pre e) {
        Player p = e.getEntity();
        UUID playerId = p.getUUID();

        // Get current divinity value
        double allValue = p.getAttributeValue(AttributeRegistry.ALL) - 1;
        double lastAllValue = LAST_DIVINITY_VALUE.getOrDefault(playerId, 0d);

        // Get or create attribute value tracking for this player
        HashMap<Holder<Attribute>, Double> lastAttrValues = LAST_ATTRIBUTE_VALUES
                .computeIfAbsent(playerId, k -> new HashMap<>());

        // Check if we need to process attributes
        boolean divinityChanged = allValue != lastAllValue;
        boolean needToProcess = divinityChanged;

        // If divinity didn't change, check if any attribute base values changed (e.g., from armor)
        if (!divinityChanged) {
            for (var ai : p.getAttributes().attributes.entrySet()) {
                double currentValue = ai.getValue().getBaseValue();
                Double lastValue = lastAttrValues.get(ai.getKey());

                // If we haven't seen this attribute before or its base value changed
                if (lastValue == null || lastValue != currentValue) {
                    needToProcess = true;
                    break;
                }
            }
        }

        needToProcess = p.tickCount % 20 == 0;

        if (needToProcess) {
//            ArsTrinkets.LOGGER.debug("updating player attributes for {}", p.getName().getString());

            // Process all attributes
            for (var ai : p.getAttributes().attributes.entrySet()) {
                Holder<Attribute> attribute = ai.getKey();
                AttributeInstance instance = ai.getValue();

                // Never modify the ALL attribute itself
                if (attribute.value() == AttributeRegistry.ALL.get()) {
                    // Still track its base value
                    lastAttrValues.put(attribute, instance.getBaseValue());
                    continue;
                }

                // Update the tracked base value
                lastAttrValues.put(attribute, instance.getBaseValue());

                // Check if attribute should be included
                if (shouldIncludeAttribute(ai)) {
                    var existing = instance.getModifier(DIVINITY_STAT_BOOST_LOCATION);

                    // Only update if missing or meaningfully different
                    if (existing == null || existing.amount() != allValue) {
                        //                        ArsTrinkets.LOGGER.debug("adding/updating boost to: {}", attribute.getRegisteredName());
                        instance.removeModifier(DIVINITY_STAT_BOOST_LOCATION);
                        instance.addTransientModifier(new AttributeModifier(
                                DIVINITY_STAT_BOOST_LOCATION,
                                allValue,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        ));
                    }
                } else {
                    // Attribute shouldn't have the boost - ensure it's removed
                    var existing = instance.getModifier(DIVINITY_STAT_BOOST_LOCATION);
                    if (existing != null) {
                        ArsTrinkets.LOGGER.debug("removing boost from: {}", attribute.getRegisteredName());
                        instance.removeModifier(DIVINITY_STAT_BOOST_LOCATION);
                    }
                }
            }

            // Update tracked values
            LAST_DIVINITY_VALUE.put(playerId, allValue);
            LAST_ATTRIBUTE_VALUES.put(playerId, lastAttrValues);
        }

        //if level is above 6, fill food

        //if level is above 8, add aura of sanctity every 5sec ((prevent fire / poison, but is removed when hurt by a level 8+ player))
    }

    // Cleanup when player logs out to prevent memory leaks
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        UUID playerId = e.getEntity().getUUID();
        LAST_DIVINITY_VALUE.remove(playerId);
        LAST_ATTRIBUTE_VALUES.remove(playerId);
    }

    public static void onMobSpawn(FinalizeSpawnEvent e){

    }

}
