package com.c446.ars_trinkets.network;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CrownLivesPayload(int remainingLives) implements CustomPacketPayload {
    public static final Type<CrownLivesPayload> TYPE = new Type<>(ArsTrinkets.prefix("crown_lives"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CrownLivesPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, CrownLivesPayload::remainingLives, CrownLivesPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CrownLivesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> CrownLivesState.setRemainingLives(payload.remainingLives()));
    }
}
