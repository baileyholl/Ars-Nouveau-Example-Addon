package com.c446.ars_trinkets.datagen;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.components.SourceOrbComponent;
//import com.c446.ars_trinkets.item.SourceOrb;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ComponentRegistry {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ArsTrinkets.MODID);
    public static final StreamCodec<ByteBuf, SourceOrbComponent> BUF_SOURCE_ORB_COMPONENT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SourceOrbComponent::amount,
            SourceOrbComponent::new
    );

    public static final Codec<SourceOrbComponent> SOURCE_ORB_COMPONENT_CODEC = RecordCodecBuilder.create(builder ->
            builder.group(Codec.INT.fieldOf("stored_mana").forGetter(SourceOrbComponent::amount)).apply(builder, SourceOrbComponent::new)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SourceOrbComponent>> SOURCE_ORB_COMPONENT =
            COMPONENTS.registerComponentType("mana_storing_item",
                    builder -> builder
                            .networkSynchronized(BUF_SOURCE_ORB_COMPONENT_STREAM_CODEC)
                            .persistent(SOURCE_ORB_COMPONENT_CODEC)
            );

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String pName, UnaryOperator<DataComponentType.Builder<T>> pBuilder) {
        return COMPONENTS.register(pName, () -> pBuilder.apply(DataComponentType.builder()).build());
    }
}
