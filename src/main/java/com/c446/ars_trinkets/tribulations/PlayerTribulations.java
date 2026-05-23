package com.c446.ars_trinkets.tribulations;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class PlayerTribulations implements INBTSerializable<CompoundTag> {
    private final List<TribulationInstance> instances = new ArrayList<>();
    private int pendingLevel = -1;

    public List<TribulationInstance> getInstances() {
        return instances;
    }

    public void add(TribulationInstance instance) {
        instances.add(instance);
    }

    public void removeInactive() {
        instances.removeIf(i -> !i.isActive());
    }

    public void setPendingLevel(int level) {
        this.pendingLevel = level;
    }

    public int getPendingLevel() {
        return pendingLevel;
    }

    public void clearPendingLevel() {
        this.pendingLevel = -1;
    }

    public boolean hasPendingLevel() {
        return pendingLevel >= 0;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        var list = new ListTag();
        for (var instance : instances) {
            list.add(instance.serializeNBT(provider));
        }
        tag.put("instances", list);
        tag.putInt("pendingLevel", pendingLevel);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        instances.clear();
        var list = tag.getList("instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            var instance = new TribulationInstance();
            instance.deserializeNBT(provider, list.getCompound(i));
            instances.add(instance);
        }
        this.pendingLevel = tag.getInt("pendingLevel");
    }
}
