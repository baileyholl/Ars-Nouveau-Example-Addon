package com.c446.ars_trinkets.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.c446.ars_trinkets.Util.CreateParticleBeam;

public class SonicBoom extends AbstractEffect
        implements IDamageEffect {
    public SonicBoom(ResourceLocation tag, String description) {
        super(tag, description);
    }

    public static final SonicBoom INSTANCE = new SonicBoom(new ResourceLocation(ArsTrinkets.MODID, "glyph_sonic_boom"), "Sonic Boom");

    double damage;
    double damageBonusTimes;
    float AMP_DAMAGE = 13.3f;
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE);
    }
    //

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public int getDefaultManaCost() {
        return 1200;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.NECROMANCY);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nonnull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (world instanceof ServerLevel level && entity instanceof LivingEntity living) {
            int range = (int) (3+2 * spellStats.getAoeMultiplier());
            damage = 1+DAMAGE.get() + spellStats.getAmpMultiplier() * this.AMP_DAMAGE;
            Vec3 eyesPosTar = living.getEyePosition();
            Vec3 eyesPosPla = shooter.getEyePosition();
            DamageSource source = DamageUtil.source(level, DamageTypes.SONIC_BOOM, shooter);
            CreateParticleBeam(eyesPosPla, eyesPosTar, level, ParticleTypes.SONIC_BOOM.getType(), 2,2);
            level.sendParticles(ParticleTypes.EXPLOSION, eyesPosTar.x, eyesPosTar.y, eyesPosTar.z,1,0,10,0,2);
            attemptDamage(level, shooter, spellStats, spellContext, resolver, entity, source, (float) damage);
            for (Entity e : world.getEntities(shooter, new AABB(
                    living.position().add(range, range, range), living.position().subtract(range, range, range)))) {
                if (e.equals(living) || !(e instanceof LivingEntity))
                    continue;
                attemptDamage(world, shooter, spellStats, spellContext, resolver, e, source, (float) ((float) damage * 0.8));

                level.sendParticles(ParticleTypes.EXPLOSION, e.getX(), e.getY(), e.getZ(),1,0,0,0,2);
            }

        }
    }
    @Override
    public void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(),4);
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(),4);
    }
    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 16);
        addAmpConfig(builder, 10);
    }
}
