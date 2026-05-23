package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class EffectsRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ArsTrinkets.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> AURA_EFFECT = EFFECTS
            .register("aura_effect", () -> new Incurable(MobEffectCategory.NEUTRAL, 2039587));

    public static final DeferredHolder<MobEffect, MobEffect> DISTORTION_EFFECT = EFFECTS
            .register("distortion_effect", () -> new Incurable(MobEffectCategory.HARMFUL, 0x5500FF));

    public static final DeferredHolder<MobEffect, MobEffect> STELLAR_BURN = EFFECTS
            .register("stellar_burn", () -> new Incurable(MobEffectCategory.HARMFUL, 0xFFFF00));

    public static final DeferredHolder<MobEffect, MobEffect> TRIBULATION_HEALTH = EFFECTS
            .register("tribulation_health", () -> new Incurable(MobEffectCategory.BENEFICIAL, 0xFF6B35)
                    .addAttributeModifier(Attributes.MAX_HEALTH, ArsTrinkets.prefix("tribulation_health"), 4.0, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> TRIBULATION_DAMAGE = EFFECTS
            .register("tribulation_damage", () -> new Incurable(MobEffectCategory.BENEFICIAL, 0xFF1744)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, ArsTrinkets.prefix("tribulation_damage"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

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
