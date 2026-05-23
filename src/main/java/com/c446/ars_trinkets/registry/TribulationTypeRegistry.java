package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.tribulations.TribulationType;
import com.c446.ars_trinkets.tribulations.tasks.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class TribulationTypeRegistry {

    public static final ResourceKey<Registry<TribulationType>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(ArsTrinkets.prefix("tribulation_type"));

    public static final DeferredRegister<TribulationType> TRIBULATION_TYPES =
            DeferredRegister.create(REGISTRY_KEY, ArsTrinkets.MODID);

    public static final DeferredHolder<TribulationType, TribulationType> LIGHTNING_RAIN =
            TRIBULATION_TYPES.register("lightning_rain",
                    () -> new TribulationType(instance -> List.of(new LightningRainTask())));

    public static final DeferredHolder<TribulationType, TribulationType> STAR_RAIN =
            TRIBULATION_TYPES.register("star_rain",
                    () -> new TribulationType(instance -> List.of(new StarRainTask())));

    public static final DeferredHolder<TribulationType, TribulationType> PERCEPTION_DISTORTION =
            TRIBULATION_TYPES.register("perception_distortion",
                    () -> new TribulationType(instance -> List.of(new PerceptionDistortionTask())));

    public static final DeferredHolder<TribulationType, TribulationType> AILMENTS =
            TRIBULATION_TYPES.register("ailments",
                    () -> new TribulationType(instance -> List.of(new AilmentsTask())));

    public static final DeferredHolder<TribulationType, TribulationType> BOSS_ENHANCEMENT =
            TRIBULATION_TYPES.register("boss_enhancement",
                    () -> new TribulationType(instance -> List.of(new BossEnhancementTask())));
}
