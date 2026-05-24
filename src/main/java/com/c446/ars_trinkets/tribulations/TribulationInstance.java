package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.registry.TribulationTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class TribulationInstance implements INBTSerializable<CompoundTag> {
    private static final int TICKS_PER_SECOND = 20;

    private ServerPlayer player;
    private float intensity;
    private float karmaScaledIntensity; // Runtime cached intensity with karma scaling applied
    private boolean active;
    private boolean failed;
    private int elapsedTicks;
    private int durationTicks = Config.Common.TRIBULATION_DURATION_BASE_SECONDS.get() * TICKS_PER_SECOND;
    private int phase = 1;
    private final List<ResourceKey<TribulationType>> typeKeys = new ArrayList<ResourceKey<TribulationType>>();
    private final List<ScheduledTask> tasks = new ArrayList<>();
    public final CompoundTag data = new CompoundTag();

    /**
     * Saved task NBT waiting for registry to become available (edge case on load).
     */
    private ListTag pendingTaskData;

    TribulationInstance() {
    }

    public TribulationInstance(float intensity) {
        this.intensity = intensity;
        this.durationTicks = calculateDurationTicks(intensity);
    }

    public void initialize(List<Holder<TribulationType>> types, Registry<TribulationType> registry) {
        typeKeys.clear();
        tasks.clear();
        ArsTrinkets.LOGGER.debug("[TribulationInstance.initialize] Initializing with {} type(s)", types.size());
        for (var type : types) {
            var key = type.getKey();
            if (key != null) {
                typeKeys.add(key);
                tasks.addAll(type.value().createTasks(this));
                ArsTrinkets.LOGGER.debug("[TribulationInstance.initialize] Added type: {}", key);
            }
        }
        active = !tasks.isEmpty();
        ArsTrinkets.LOGGER.debug("[TribulationInstance.initialize] Initialization complete: {} tasks created, active={}", tasks.size(), active);
    }

    /**
     * Called when the registry is available but tasks weren't built yet (deferred load).
     */
    void reinitializeIfNeeded(Registry<TribulationType> registry) {
        if (!tasks.isEmpty() || typeKeys.isEmpty()) return;
        ArsTrinkets.LOGGER.debug("[TribulationInstance.reinitializeIfNeeded] Reinitializing {} type(s) from deferred load", typeKeys.size());
        int taskIdx = 0;
        for (ResourceKey<TribulationType> key : typeKeys) {
            var type = registry.get(key);
            if (type == null) continue;
            for (var task : type.createTasks(this)) {
                if (pendingTaskData != null && taskIdx < pendingTaskData.size()) {
                    task.deserialize(pendingTaskData.getCompound(taskIdx), null);
                }
                tasks.add(task);
                taskIdx++;
            }
        }
        pendingTaskData = null;
        ArsTrinkets.LOGGER.debug("[TribulationInstance.reinitializeIfNeeded] Reinitialization complete: {} tasks loaded", tasks.size());
    }

    public void tick() {
        if (!active || player == null) return;

        elapsedTicks++;
        updatePhase();

        for (var task : tasks) {
            task.tick(this);
        }

        if (elapsedTicks >= durationTicks) {
            ArsTrinkets.LOGGER.info("[TribulationInstance.tick] Tribulation survived: player={}, durationTicks={}, intensity={}",
                    player.getName().getString(), durationTicks, intensity);
            deactivate();
        }
    }

    private static int calculateDurationTicks(float intensity) {
        float clampedIntensity = Math.max(0.0f, intensity);
        int seconds = Config.Common.TRIBULATION_DURATION_BASE_SECONDS.get()
                + Math.round((float)(clampedIntensity * Config.Common.TRIBULATION_DURATION_SECONDS_PER_INTENSITY.get()));
        return Math.min(Config.Common.TRIBULATION_DURATION_MAX_SECONDS.get(), seconds) * TICKS_PER_SECOND;
    }

    private void updatePhase() {
        int newPhase = Math.min(3, 1 + (elapsedTicks * 3 / Math.max(1, durationTicks)));
        if (newPhase != phase) {
            phase = newPhase;
            postEvent(new PhaseChangedEvent(this, phase));
            ArsTrinkets.LOGGER.debug("[TribulationInstance.tick] Phase changed: phase={}, elapsedTicks={}, durationTicks={}",
                    phase, elapsedTicks, durationTicks);
        }
    }

    public void postEvent(TribulationEvent event) {
        for (var task : tasks) {
            task.handleEvent(event, this);
        }
    }

    public void deactivate() {
        active = false;
        ArsTrinkets.LOGGER.debug("[TribulationInstance.deactivate] Deactivated tribulation instance");
    }

    public void fail() {
        failed = true;
        active = false;
        ArsTrinkets.LOGGER.debug("[TribulationInstance.fail] Failed tribulation instance");
    }

    public boolean isActive() {
        return active;
    }

    public boolean hasFailed() {
        return failed;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    public void setKarmaScaledIntensity(float karmaMultiplier) {
        this.karmaScaledIntensity = intensity * karmaMultiplier;
    }

    public float getIntensity() {
        // Return karma-scaled intensity if available (runtime), otherwise base intensity
        return karmaScaledIntensity > 0 ? karmaScaledIntensity : intensity;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        tag.putFloat("intensity", intensity);
        tag.putBoolean("active", active);
        tag.putBoolean("failed", failed);
        tag.putInt("elapsedTicks", elapsedTicks);
        tag.putInt("durationTicks", durationTicks);
        tag.putInt("phase", phase);

        var typesList = new ListTag();
        for (ResourceKey<TribulationType> key : typeKeys) {
            typesList.add(StringTag.valueOf(key.toString()));
        }
        tag.put("types", typesList);

        var tasksList = new ListTag();
        for (var task : tasks) {
            tasksList.add(task.serialize(provider));
        }
        tag.put("tasks", tasksList);
        tag.put("data", data.copy());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        intensity = tag.getFloat("intensity");
        active = tag.getBoolean("active");
        failed = tag.getBoolean("failed");
        elapsedTicks = tag.getInt("elapsedTicks");
        durationTicks = tag.contains("durationTicks", Tag.TAG_INT) ? tag.getInt("durationTicks") : calculateDurationTicks(intensity);
        phase = tag.contains("phase", Tag.TAG_INT) ? tag.getInt("phase") : 1;

        typeKeys.clear();
        var typesTag = tag.getList("types", Tag.TAG_STRING);
        var reg = provider.lookupOrThrow(TribulationTypeRegistry.REGISTRY_KEY);
        for (int i = 0; i < typesTag.size(); i++) {
            var key = ResourceKey.create(TribulationTypeRegistry.REGISTRY_KEY, ResourceLocation.parse(typesTag.getString(i)));
            typeKeys.add(key);
            if (reg.get(key).isEmpty()) {
                ArsTrinkets.LOGGER.error("[TribulationInstance.deserializeNBT] Unknown TribulationType key during deserialization: {}", key);
            }
        }

        var tasksTag = tag.getList("tasks", Tag.TAG_COMPOUND);
        tasks.clear();

        if (provider instanceof RegistryAccess ra) {
            var registry = ra.registry(TribulationTypeRegistry.REGISTRY_KEY).orElse(null);
            if (registry != null) {
                int taskIdx = 0;
                for (ResourceKey<TribulationType> key : typeKeys) {
                    var type = registry.get(key);
                    if (type == null) continue;
                    for (var task : type.createTasks(this)) {
                        if (taskIdx < tasksTag.size()) {
                            task.deserialize(tasksTag.getCompound(taskIdx), provider);
                        }
                        tasks.add(task);
                        taskIdx++;
                    }
                }
                pendingTaskData = null;
                if (tag.contains("data", Tag.TAG_COMPOUND)) {
                    data.merge(tag.getCompound("data"));
                }
                return;
            }
        }

        // Registry not available yet — defer task creation to reinitializeIfNeeded()
        pendingTaskData = tasksTag;
        if (tag.contains("data", Tag.TAG_COMPOUND)) {
            data.merge(tag.getCompound("data"));
        }
    }
}
