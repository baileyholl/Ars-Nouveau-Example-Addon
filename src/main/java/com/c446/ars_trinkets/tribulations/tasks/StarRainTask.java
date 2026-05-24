package com.c446.ars_trinkets.tribulations.tasks;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.tribulations.ScheduledTask;
import com.c446.ars_trinkets.tribulations.TribulationInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class StarRainTask extends ScheduledTask {
    private final RandomSource random;

    public StarRainTask() {
        super(Config.Common.STAR_RAIN_INTERVAL.get());
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
        double radius = Config.Common.STAR_RAIN_RADIUS_BASE.get()
                + (intensity * Config.Common.STAR_RAIN_RADIUS_PER_INTENSITY.get())
                + (spellDamage * Config.Common.STAR_RAIN_RADIUS_PER_SPELL_DAMAGE.get());
        int baseStrikeCount = (int)(Config.Common.STAR_RAIN_STRIKE_COUNT_BASE.get()
                + intensity * Config.Common.STAR_RAIN_STRIKE_COUNT_PER_INTENSITY.get());
        int strikeCount = Math.min(
                baseStrikeCount + random.nextInt(Config.Common.STAR_RAIN_STRIKE_COUNT_RANDOM_BOUND.get()),
                Config.Common.STAR_RAIN_STRIKE_COUNT_MAX.get()
        );

        ArsTrinkets.LOGGER.debug("[StarRainTask] Executing: intensity={}, strikes={}, radius={}", intensity, strikeCount, radius);

        Vec3 playerPos = player.position();
        for (int i = 0; i < strikeCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = random.nextDouble() * radius;
            double x = playerPos.x + Math.cos(angle) * distance;
            double z = playerPos.z + Math.sin(angle) * distance;

            int blockX = net.minecraft.util.Mth.floor(x);
            int blockZ = net.minecraft.util.Mth.floor(z);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockX, blockZ);

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
