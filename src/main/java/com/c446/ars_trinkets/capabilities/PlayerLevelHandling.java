package com.c446.ars_trinkets.capabilities;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.events.LevelModifiedEvent;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Objects;

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

        if (e.entity instanceof ServerPlayer sp) {
            sp.displayClientMessage(Component.literal("you are now a ").append(cap.getTitle()), false);
        }
    }

    @SubscribeEvent
    public static void spellDamageEvent(SpellDamageEvent e) {
        e.damage *= (float) e.caster.getAttributeValue(AttributeRegistry.SPELL_DAMAGE_ABSOLUTE);
    }

    @SubscribeEvent
    public static void tickEntity(PlayerTickEvent.Post e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;

        var loc = ArsTrinkets.prefix("divinity_stat_boost");
        double allValue = p.getAttributeValue(AttributeRegistry.ALL);

        for (var ai : p.getAttributes().getSyncableAttributes()) {
            // Never modify the ALL attribute itself
            if (ai.getAttribute() == AttributeRegistry.ALL) continue;

            var existing = ai.getModifier(loc);

            // Only update if missing or meaningfully different
            if (existing == null || Math.abs(existing.amount() - allValue) > 1.0E-6) {
                ai.addOrUpdateTransientModifier(
                        new AttributeModifier(
                                loc,
                                allValue,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                );
            }
        }

        //if level is above 6, fill food

        //if level is above 8, add aura of sanctity every 5sec ((prevent fire / poison, but is removed when hurt by a level 8+ player))
    }

}
