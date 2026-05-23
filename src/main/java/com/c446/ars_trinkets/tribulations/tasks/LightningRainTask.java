package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningRainTask extends ScheduledTask {
    private final RandomSource random;

    public LightningRainTask() {
        super(20);
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

        // Scale radius and frequency with intensity and spell damage
        double radius = 32.0 + (intensity * 8.0) + (spellDamage * 2.0);
        int strikeChance = Math.max(1, (int)(20 - (intensity * 2)));

        if (random.nextInt(strikeChance) != 0) return;

        ArsTrinkets.LOGGER.debug("[LightningRainTask] Executing: intensity={}, radius={}, spellDamage={}", intensity, radius, spellDamage);

        Vec3 playerPos = player.position();
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = random.nextDouble() * radius;
        double x = playerPos.x + Math.cos(angle) * distance;
        double z = playerPos.z + Math.sin(angle) * distance;

        int y = level.getMaxBuildHeight();
        var blockPos = new net.minecraft.core.BlockPos((int) x, y, (int) z);
        while (y > level.getMinBuildHeight() && level.getBlockState(blockPos.below()).isAir()) {
            y--;
        }

        if (y > level.getMinBuildHeight()) {
            var lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
            if (lightning != null) {
                lightning.setPos(x, y, z);
                level.addFreshEntity(lightning);
                ArsTrinkets.LOGGER.debug("[LightningRainTask] Lightning strike created at ({}, {}, {})", x, y, z);
            }
        }
    }

    private double getAttributeValue(net.minecraft.world.entity.player.Player player,
                                     Holder<Attribute> attr, double defaultVal) {
        var attribute = player.getAttribute(attr);
        return attribute != null ? attribute.getValue() : defaultVal;
    }
}
