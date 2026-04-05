package com.c446.ars_trinkets.spells.glyphs.forms;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.registry.EffectsRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static com.c446.ars_trinkets.ArsTrinkets.prefix;
import static com.c446.ars_trinkets.Config.Common.AURA_BASE_RADIUS;

public class AuraForm extends AbstractCastMethod {

    public static AuraForm INSTANCE = new AuraForm(prefix("glyph_aura"), "Aura Glyph");

    public AuraForm(ResourceLocation tag, String description) {
        super(tag, description);
    }

    public void startEffect(LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver, Entity mob) {
        boolean isUpperHalfCircle = spellStats.hasBuff(AugmentDampen.INSTANCE);
        boolean isLowerHalfCircle = spellStats.hasBuff(AugmentAmplify.INSTANCE);
        boolean isSolidSphere = !spellStats.hasBuff(AugmentPierce.INSTANCE);
        boolean targetsEntities = !spellStats.hasBuff(AugmentSensitive.INSTANCE);

        int baseDuration = Config.Common.AURA_BASE_DURATION.get();
        int targetDelay = Math.max(1, Config.Common.AURA_BASE_DELAY.get() - Config.Common.AURA_BASE_ACCELERATE.get() * Math.abs((int) spellStats.getAccMultiplier()));
        int finalDuration = 20 + baseDuration * (int) spellStats.getDurationMultiplier();

        playerEntity.addEffect(new MobEffectInstance(EffectsRegistry.AURA_EFFECT, finalDuration));

        if (targetsEntities) {
            ArsTrinkets.setInterval(() -> {
                float radius = AURA_BASE_RADIUS.getAsInt() + (int) spellStats.getAoeMultiplier()*5;
                BlockPos entityBlockPos = playerEntity.blockPosition();
                int centerX = entityBlockPos.getX();
                int centerY = entityBlockPos.getY();
                int centerZ = entityBlockPos.getZ();
                int lowerY = centerY - 2;
                int upperYThreshold = centerY - 1;
                double radiusPlus = radius + 0.5;
                double radiusPlusSq = radiusPlus * radiusPlus;
                double radiusMinus = radius - 1 + 0.5;
                double radiusMinusSq = radiusMinus * radiusMinus;
                AABB area = new AABB(entityBlockPos).inflate(radius, radius, radius);

                for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, area)) {
                    if (entity.equals(playerEntity)) continue;

                    BlockPos pos = entity.blockPosition();
                    int posX = pos.getX();
                    int posY = pos.getY();
                    int posZ = pos.getZ();

                    // Squared distance calculation
                    double dx = posX - centerX;
                    double dy = posY - centerY;
                    double dz = posZ - centerZ;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    // Check conditions with short-circuiting
                    if ((!isLowerHalfCircle || posY <= lowerY) &&
                            (!isUpperHalfCircle || posY > upperYThreshold) &&
                            (distSq <= radiusPlusSq) &&
                            (isSolidSphere || distSq >= radiusMinusSq)) {

                        resolver.onResolveEffect(world, new EntityHitResult(entity));
                    }
                }
            }, targetDelay, finalDuration);
        } else {
            ArsTrinkets.setInterval(() -> {
                int radius = 5 + (int) spellStats.getAoeMultiplier();
                BlockPos entityBlockPos = mob != null ? mob.blockPosition() : playerEntity.blockPosition();
                int centerX = entityBlockPos.getX();
                int centerY = entityBlockPos.getY();
                int centerZ = entityBlockPos.getZ();
                int lowerY = centerY - 2;
                int upperYThreshold = centerY - 1;
                double radiusPlus = radius + 0.5;
                double radiusPlusSq = radiusPlus * radiusPlus;
                double radiusMinus = radius - 1 + 0.5;
                double radiusMinusSq = radiusMinus * radiusMinus;

                for (BlockPos pos : BlockPos.withinManhattan(entityBlockPos, radius, radius, radius)) {
                    int posX = pos.getX();
                    int posY = pos.getY();
                    int posZ = pos.getZ();

                    // Squared distance calculation
                    double dx = posX - centerX;
                    double dy = posY - centerY;
                    double dz = posZ - centerZ;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    // Check conditions with short-circuiting
                    if ((!isLowerHalfCircle || posY <= lowerY) &&
                            (!isUpperHalfCircle || posY > upperYThreshold) &&
                            (distSq <= radiusPlusSq) &&
                            (isSolidSphere || distSq >= radiusMinusSq)) {

                        BlockHitResult hitResult = new BlockHitResult(
                                new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false
                        );
                        resolver.onResolveEffect(world, hitResult);
                    }
                }
            }, targetDelay, finalDuration);
        }
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {

        startEffect(playerEntity, world, spellStats, context, resolver, null);

        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        startEffect(context.getPlayer(), context.getLevel(), spellStats, spellContext, resolver, null);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        startEffect(caster, caster.getCommandSenderWorld(), spellStats, spellContext, resolver, null);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        startEffect(caster, caster.getCommandSenderWorld(), spellStats, spellContext, resolver, caster);
        return CastResolveType.SUCCESS;
    }

    @Override
    public int getDefaultManaCost() {
        return 2500;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        //AOE increases radius
        //Amplify makes the sphere solid
        //Dampen makes it a dome (upper half hemisphere)
        //Pierce makes it an inverted dome (lower half hemisphere)
        //Sensitive makes it target entities instead of blocks
        return augmentSetOf(AugmentAOE.INSTANCE,
                AugmentAmplify.INSTANCE,
                AugmentDampen.INSTANCE,
                AugmentSensitive.INSTANCE,
                AugmentExtendTime.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentAccelerate.INSTANCE);
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(), 20);
        defaults.put(AugmentAccelerate.INSTANCE.getRegistryName(), 4);
        defaults.put(AugmentDampen.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentPierce.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }
}
