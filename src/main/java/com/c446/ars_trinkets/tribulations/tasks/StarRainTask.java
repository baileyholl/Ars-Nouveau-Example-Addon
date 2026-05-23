package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StarRainTask extends ScheduledTask {
    private final RandomSource random;

    public StarRainTask() {
        super(10);
        this.random = RandomSource.create();
    }

    @Override
    public void execute(TribulationInstance instance) {
        var player = instance.getPlayer();
        if (player == null || !player.isAlive()) return;

        Level level = player.level();
        if (level.isClientSide) return;

        float intensity = instance.getIntensity();
        double spellDamage = player.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS);

        // Scale with intensity and spell damage
        double radius = 48.0 + (intensity * 12.0) + (spellDamage * 3.0);
        int baseStrikeCount = (int)(1 + intensity * 0.5);
        int strikeCount = Math.min(baseStrikeCount + random.nextInt(3), 8);

        ArsTrinkets.LOGGER.debug("[StarRainTask] Executing: intensity={}, strikes={}, radius={}", intensity, strikeCount, radius);

        Vec3 playerPos = player.position();
        for (int i = 0; i < strikeCount; i++) {
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
                }
            }
        }
    }
}
