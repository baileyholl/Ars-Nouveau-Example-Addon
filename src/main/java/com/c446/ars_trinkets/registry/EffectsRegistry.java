package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class EffectsRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ArsTrinkets.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> AURA_EFFECT = EFFECTS
            .register("aura_effect", () -> new Incurable(MobEffectCategory.NEUTRAL, 2039587));

    public static final class Incurable extends MobEffect{
        public Incurable(MobEffectCategory pCategory, int pColor) {
            super(pCategory, pColor);
        }

        @Override
        public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
            return;
        }
    }
}
