package com.c446.ars_trinkets.datagen;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModEntityTags extends TagsProvider<EntityType<?>> {
    ModEntityTags(PackOutput pOutput, ResourceKey<? extends Registry<EntityType<?>>> pRegistryKey, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pRegistryKey, pLookupProvider, ArsTrinkets.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
    }
}
