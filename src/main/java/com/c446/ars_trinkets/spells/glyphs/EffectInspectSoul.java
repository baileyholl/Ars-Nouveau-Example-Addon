package com.c446.ars_trinkets.spells.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.c446.ars_trinkets.tribulations.KarmaCalculator;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectInspectSoul extends AbstractEffect {
    public static final EffectInspectSoul INSTANCE = new EffectInspectSoul(ArsTrinkets.prefix("glyph_soul_inspector"), "Peers at the soul of the target, giving you how many circles it has created, and how many cores they have.");

    private EffectInspectSoul(String tag, String description) {
        super(tag, description);
    }

    public EffectInspectSoul(ResourceLocation inspectSoul, String description) {
        super(inspectSoul, description);
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentAmplify.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
    defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 3);

        super.addDefaultAugmentLimits(defaults);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var entity = rayTraceResult.getEntity();

        if (entity instanceof LivingEntity livingEntity && livingEntity.hasData(CapabilityRegistry.LEVEL_CAP) && shooter instanceof ServerPlayer caster){
            var targetLevelCap = livingEntity.getData(CapabilityRegistry.LEVEL_CAP);
            var casterLevelCap = caster.getData(CapabilityRegistry.LEVEL_CAP);
            boolean canPeer = targetLevelCap.level <= (casterLevelCap.level + spellStats.getAmpMultiplier());

            if (canPeer) {
                caster.displayClientMessage(Component.translatable("text.ars_trinkets.souls.whispers.lookat").append(targetLevelCap.getTitle()), false);

                // If inspecting self, show karma breakdown
                if (entity == caster) {
                    displayKarmaBreakdown(caster);
                }
            }
            else {
                caster.displayClientMessage(Component.translatable("text.ars_trinkets.souls.whispers.forbidden"), false);
            }
        }

        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    private void displayKarmaBreakdown(ServerPlayer player) {
        var stats = player.getStats();

        // Get kill counts
        int playerKills = stats.getValue(Stats.ENTITY_KILLED.get(EntityType.PLAYER));
        int passiveMobKills = getPassiveMobKillCount(stats);
        int deaths = stats.getValue(Stats.CUSTOM.get(Stats.DEATHS));

        // Get positive actions
        int villagerTrades = stats.getValue(Stats.CUSTOM.get(Stats.TRADED_WITH_VILLAGER));
        int animalsBred = stats.getValue(Stats.CUSTOM.get(Stats.ANIMALS_BRED));

        // Get playtime in hours
        int playtimeTicks = stats.getValue(Stats.CUSTOM.get(Stats.TOTAL_WORLD_TIME));
        double playtimeHours = playtimeTicks / 72000.0;

        // Display karma breakdown
        player.displayClientMessage(Component.literal(""), false); // Blank line
        player.displayClientMessage(Component.translatable("text.ars_trinkets.karma.breakdown"), false);

        if (playerKills > 0) {
            player.displayClientMessage(Component.translatable("text.ars_trinkets.karma.player_kills", playerKills), false);
        }
        if (passiveMobKills > 0) {
            player.displayClientMessage(Component.translatable("text.ars_trinkets.karma.passive_kills", passiveMobKills), false);
        }
        if (deaths > 0) {
            player.displayClientMessage(Component.translatable("text.ars_trinkets.karma.deaths", deaths), false);
        }

        if (villagerTrades > 0) {
            player.displayClientMessage(Component.translatable("text.ars_trinkets.karma.villager_trades", villagerTrades), false);
        }
        if (animalsBred > 0) {
            player.displayClientMessage(Component.translatable("text.ars_trinkets.karma.animals_bred", animalsBred), false);
        }

        player.displayClientMessage(Component.translatable("text.ars_trinkets.karma.playtime", String.format("%.1f", playtimeHours)), false);
        player.displayClientMessage(Component.literal(""), false); // Blank line
    }

    private int getPassiveMobKillCount(net.minecraft.stats.StatsCounter stats) {
        int total = 0;
        EntityType<?>[] passiveMobs = {
                EntityType.COW,
                EntityType.PIG,
                EntityType.SHEEP,
                EntityType.CHICKEN,
                EntityType.RABBIT,
                EntityType.HORSE,
                EntityType.DONKEY,
                EntityType.LLAMA,
                EntityType.CAT,
                EntityType.WOLF,
                EntityType.PARROT,
                EntityType.TURTLE,
                EntityType.FROG,
                EntityType.AXOLOTL,
                EntityType.BEE,
                EntityType.GOAT,
                EntityType.CAMEL,
                EntityType.SNIFFER
        };

        for (EntityType<?> entityType : passiveMobs) {
            try {
                total += stats.getValue(Stats.ENTITY_KILLED.get(entityType));
            } catch (Exception e) {
                // Skip if entity type not found
            }
        }

        return total;
    }
}
