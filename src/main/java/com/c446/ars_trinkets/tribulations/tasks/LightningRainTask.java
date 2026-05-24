package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.Util;
import com.c446.ars_trinkets.entities.red_lightning.RedLightning;
import com.c446.ars_trinkets.registry.EntityRegistry;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LightningRainTask extends ScheduledTask {
    private final RandomSource random;

    public LightningRainTask() {
        super(Config.Common.LIGHTNING_RAIN_INTERVAL.get());
        this.random = RandomSource.create();
    }

    @Override
    public void execute(TribulationInstance instance) {
        var player = instance.getPlayer();
        if (player == null || !player.isAlive()) return;

        Level level = player.level();
        if (level.isClientSide) return;

        float intensity = instance.getIntensity();
        double spellDamage = getAttributeValue(player, PerkAttributes.SPELL_DAMAGE_BONUS, 0.0);
        spellDamage += getAttributeValue(player, PerkAttributes.WARDING, 0f);

        // Scale radius and frequency with intensity and spell damage
        double radius = Config.Common.LIGHTNING_RAIN_RADIUS_BASE.get()
                + (intensity * Config.Common.LIGHTNING_RAIN_RADIUS_PER_INTENSITY.get())
                + (spellDamage * Config.Common.LIGHTNING_RAIN_RADIUS_PER_SPELL_DAMAGE.get());
        int strikeChance = Math.max(1, (int) (Config.Common.LIGHTNING_RAIN_STRIKE_CHANCE_BASE.get()
                - (intensity * Config.Common.LIGHTNING_RAIN_STRIKE_CHANCE_REDUCTION_PER_INTENSITY.get())));

        if (random.nextInt(strikeChance) != 0) return;

        ArsTrinkets.LOGGER.debug("[LightningRainTask] Executing: intensity={}, radius={}, spellDamage={}", intensity, radius, spellDamage);

        Vec3 playerPos = player.position();
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = random.nextDouble() * radius;
        double x = playerPos.x + Math.cos(angle) * distance;
        double z = playerPos.z + Math.sin(angle) * distance;

        int blockX = net.minecraft.util.Mth.floor(x);
        int blockZ = net.minecraft.util.Mth.floor(z);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockX, blockZ);

        if (y > level.getMinBuildHeight()) {
            if (!(level instanceof ServerLevel serverLevel)) return;

            RedLightning lightning = EntityRegistry.RED_LIGHTNING.get().create(serverLevel);
            if (lightning != null) {
                Vec3 strikePos = new Vec3(x, y, z);
                lightning.setOwner(player);
                lightning.setPos(strikePos);
                lightning.setDamage(0);
                lightning.setVisualOnly(true);
                serverLevel.addFreshEntity(lightning);
                damageNearbyTargets(serverLevel, player, lightning, strikePos, intensity, spellDamage);
                ArsTrinkets.LOGGER.debug("[LightningRainTask] Lightning strike created at ({}, {}, {})", x, y, z);
            }
        }
    }

    private void damageNearbyTargets(ServerLevel serverLevel, LivingEntity shooter, RedLightning lightning, Vec3 pos, float intensity, double spellDamage) {
        double size = Config.Common.LIGHTNING_RAIN_DAMAGE_SIZE_BASE.get()
                + (intensity * Config.Common.LIGHTNING_RAIN_DAMAGE_SIZE_PER_INTENSITY.get());
        double damage = (Config.Common.LIGHTNING_RAIN_DAMAGE_BASE.get()
                + (spellDamage * Config.Common.LIGHTNING_RAIN_DAMAGE_PER_SPELL_DAMAGE.get())) * intensity;
        double sizeSquared = size * size;

        serverLevel.getEntities(lightning, AABB.ofSize(pos, size * 2, size * 2, size * 2), target ->
                target instanceof LivingEntity livingTarget && target != shooter && !shooter.isAlliedTo(livingTarget)
        ).forEach(target -> {
            double distance = target.distanceToSqr(pos);
            if (distance >= sizeSquared || !Util.hasLineOfSight(serverLevel, pos.add(0, 2, 0), target.getBoundingBox().getCenter())) {
                return;
            }

            LivingEntity livingTarget = (LivingEntity) target;
            float damageMult = getDamageMult(shooter, livingTarget);
            float finalDamage = (float) (damage * (1 - distance / sizeSquared));
            finalDamage *= damageMult;

            var damageType = damageMult > Config.Common.LIGHTNING_RAIN_DAMAGE_MULTIPLIER_THRESHOLD.get()
                    ? DamageTypes.GENERIC
                    : DamageTypes.LIGHTNING_BOLT;
            //only target Friendly
            if (!(livingTarget instanceof Monster)) {
                livingTarget.invulnerableTime = 0;
                livingTarget.hurt(DamageUtil.source(serverLevel, damageType, shooter), finalDamage);
            }
            if (livingTarget instanceof Creeper creeper) {
                creeper.thunderHit(serverLevel, lightning);
            }
        });
    }

    private static float getDamageMult(@NotNull LivingEntity shooter, LivingEntity target) {
        float targetHealth = target.getMaxHealth();
        float casterHealth = Math.max(1.0f, shooter.getHealth());
        float healthDifferenceRatio = (targetHealth - casterHealth) / casterHealth;
        float damageMult = (float) (1.0f + (healthDifferenceRatio / Config.Common.LIGHTNING_RAIN_DAMAGE_MULTIPLIER_DIVISOR.get()));
        damageMult = Math.min(damageMult, Config.Common.LIGHTNING_RAIN_DAMAGE_MULTIPLIER_MAX.get().floatValue());
        damageMult = Math.max(damageMult, Config.Common.LIGHTNING_RAIN_DAMAGE_MULTIPLIER_MIN.get().floatValue());
        return damageMult;
    }

    private double getAttributeValue(net.minecraft.world.entity.player.Player player,
                                     Holder<Attribute> attr, double defaultVal) {
        var attribute = player.getAttribute(attr);
        return attribute != null ? attribute.getValue() : defaultVal;
    }
}
