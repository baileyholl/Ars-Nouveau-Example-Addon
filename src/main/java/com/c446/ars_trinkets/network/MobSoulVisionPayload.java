package com.c446.ars_trinkets.network;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MobSoulVisionPayload(int viewerLevel, boolean enabled) implements CustomPacketPayload {
    public static final Type<MobSoulVisionPayload> TYPE = new Type<>(ArsTrinkets.prefix("mob_soul_vision"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MobSoulVisionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, MobSoulVisionPayload::viewerLevel,
                    ByteBufCodecs.BOOL, MobSoulVisionPayload::enabled,
                    MobSoulVisionPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MobSoulVisionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> MobSoulVisionState.set(payload.viewerLevel(), payload.enabled()));
    }
}
