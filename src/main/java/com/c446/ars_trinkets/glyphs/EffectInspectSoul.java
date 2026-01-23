package com.c446.ars_trinkets.glyphs;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
            }
            else {
                caster.displayClientMessage(Component.translatable("text.ars_trinkets.souls.whispers.forbidden"), false);
                //can't look at. inflict damage.
            }
        }


        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }
}
