package com.c446.ars_trinkets.registry;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import com.c446.ars_trinkets.tribulations.PlayerTribulations;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CapabilityRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES;
    public static final Supplier<AttachmentType<LevelingCapability>> LEVEL_CAP;
    public static final Supplier<AttachmentType<PlayerTribulations>> TRIBULATIONS;

    static {
        ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ArsTrinkets.MODID);
        LEVEL_CAP = ATTACHMENT_TYPES.register("level_cap", () -> AttachmentType.serializable(LevelingCapability::new).copyOnDeath().build());
        TRIBULATIONS = ATTACHMENT_TYPES.register("tribulations", () -> AttachmentType.serializable(PlayerTribulations::new).copyOnDeath().build());
    }
}
