package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.tribulations.DamageResistanceMath;
import com.c446.ars_trinkets.registry.EffectsRegistry;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

public class BossEnhancementTask extends ScheduledTask {

    public BossEnhancementTask() {
        super(Config.Common.BOSS_ENHANCEMENT_INTERVAL.get());
    }

    @Override
    public void execute(TribulationInstance instance) {
        var player = instance.getPlayer();
        if (player == null || !player.isAlive()) return;

        var level = player.level();
        if (level.isClientSide) return;

        float intensity = instance.getIntensity();
        double spellDamage = player.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS);

        // Scale search radius with intensity and spell damage
        double radius = Config.Common.BOSS_ENHANCEMENT_RADIUS_BASE.get()
                + (intensity * Config.Common.BOSS_ENHANCEMENT_RADIUS_PER_INTENSITY.get())
                + (spellDamage * Config.Common.BOSS_ENHANCEMENT_RADIUS_PER_SPELL_DAMAGE.get());
        AABB searchArea = new AABB(
                player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );

        var mobs = level.getEntitiesOfClass(Mob.class, searchArea);
        ArsTrinkets.LOGGER.debug("[BossEnhancementTask] Executing: intensity={}, radius={}, mobs found={}", intensity, radius, mobs.size());
        for (var mob : mobs) {
            if (mob.isAlive() && mob.canAttack(player)) {
                ArsTrinkets.LOGGER.debug("\tEnhancing mob: {}, distance={}", mob.getDisplayName(), mob.distanceTo(player));
                enhanceMob(mob, player, instance);
            }
        }
    }

    private void enhanceMob(Mob mob, net.minecraft.world.entity.player.Player player, TribulationInstance instance) {
        ArsTrinkets.LOGGER.debug("\t[enhanceMob] Enhancing mob: {}, player={}, intensity={}", mob.getDisplayName(), player.getName().getString(), instance.getIntensity());
        float intensity = instance.getIntensity();
        double playerDamage = getAttributeValue(player, Attributes.ATTACK_DAMAGE, 1.0);
        int effectDuration = Config.Common.BOSS_ENHANCEMENT_EFFECT_DURATION.get();

        int strengthLevel = Math.toIntExact(Math.min(Config.Common.BOSS_ENHANCEMENT_STRENGTH_MAX.get(),
                Math.round(Config.Common.BOSS_ENHANCEMENT_STRENGTH_BASE.get()
                        + intensity * Config.Common.BOSS_ENHANCEMENT_STRENGTH_PER_INTENSITY.get()
                        + playerDamage * Config.Common.BOSS_ENHANCEMENT_STRENGTH_PER_PLAYER_DAMAGE.get())));
        if (strengthLevel > 0) {
            mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, effectDuration,
                    strengthLevel - 1, false, false));
        }

        int resistanceLevel = Math.min(Config.Common.BOSS_ENHANCEMENT_RESISTANCE_MAX.get(),
                Math.round((float) (Config.Common.BOSS_ENHANCEMENT_RESISTANCE_BASE.get()
                        + intensity * Config.Common.BOSS_ENHANCEMENT_RESISTANCE_PER_INTENSITY.get())));
        if (resistanceLevel > 0) {
            mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, effectDuration,
                    DamageResistanceMath.amplifierForConfiguredLevel(resistanceLevel), false, false));
        }

        int healthBoostLevel = Math.min(Config.Common.BOSS_ENHANCEMENT_HEALTH_MAX.get(),
                Math.round((float)(Config.Common.BOSS_ENHANCEMENT_HEALTH_BASE.get()
                        + intensity * Config.Common.BOSS_ENHANCEMENT_HEALTH_PER_INTENSITY.get())));
        if (healthBoostLevel > 0) {
            mob.addEffect(new MobEffectInstance(EffectsRegistry.TRIBULATION_HEALTH, effectDuration, healthBoostLevel - 1, false, false));
        }

        int damageBoostLevel = Math.toIntExact(Math.min(Config.Common.BOSS_ENHANCEMENT_DAMAGE_MAX.get(),
                Math.round(Config.Common.BOSS_ENHANCEMENT_DAMAGE_BASE.get()
                        + intensity * Config.Common.BOSS_ENHANCEMENT_DAMAGE_PER_INTENSITY.get()
                        + playerDamage * Config.Common.BOSS_ENHANCEMENT_DAMAGE_PER_PLAYER_DAMAGE.get()
                        + player.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS)
                        * Config.Common.BOSS_ENHANCEMENT_DAMAGE_PER_SPELL_DAMAGE.get())));
        if (damageBoostLevel > 0) {
            mob.addEffect(new MobEffectInstance(EffectsRegistry.TRIBULATION_DAMAGE, effectDuration,
                    damageBoostLevel - 1, false, false));
        }

    }

    private double getAttributeValue(net.minecraft.world.entity.player.Player player,
                                     Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, double defaultVal) {
        var attribute = player.getAttribute(attr);
        return attribute != null ? attribute.getValue() : defaultVal;
    }
}
