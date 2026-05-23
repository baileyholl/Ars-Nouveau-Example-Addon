package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
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
        super(200);
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
        double radius = 64.0 + (intensity * 16.0) + (spellDamage * 4.0);
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
        double spellDamage = getAttributeValue(player, PerkAttributes.SPELL_DAMAGE_BONUS, 0.0);
        double playerDamage = getAttributeValue(player, Attributes.ATTACK_DAMAGE, 1.0);

        int effectDuration = 200;

        int strengthLevel = Math.toIntExact(Math.min(4, Math.round(1 + (intensity * 0.8f) + (playerDamage * 0.1f))));
        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, effectDuration, strengthLevel - 1, false, false));

        int resistanceLevel = Math.round(intensity * 0.8f);
        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, effectDuration, resistanceLevel, false, false));

        int speedLevel = Math.toIntExact(Math.round(1 + (spellDamage * 0.2f)));
        if (speedLevel > 0) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, effectDuration, speedLevel - 1, false, false));
        }

        int healthBoostLevel = Math.min(4, Math.round(intensity * 0.5f));
        if (healthBoostLevel > 0) {
            mob.addEffect(new MobEffectInstance(EffectsRegistry.TRIBULATION_HEALTH, effectDuration, healthBoostLevel - 1, false, false));
        }

        int damageBoostLevel = Math.toIntExact(Math.min(4, Math.round(1 + (playerDamage * 0.15f) + (spellDamage * 0.1f))));
        if (damageBoostLevel > 0) {
            mob.addEffect(new MobEffectInstance(EffectsRegistry.TRIBULATION_DAMAGE, effectDuration, damageBoostLevel - 1, false, false));
        }
    }

    private double getAttributeValue(net.minecraft.world.entity.player.Player player,
                                     Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, double defaultVal) {
        var attribute = player.getAttribute(attr);
        return attribute != null ? attribute.getValue() : defaultVal;
    }
}
