package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public abstract class ScheduledTask {
    protected final int interval;
    protected int ticks;

    protected ScheduledTask(int interval) {
        this.interval = interval;
        this.ticks = 0;
        ArsTrinkets.LOGGER.debug("[{}] Task created with interval={}", this.getClass().getSimpleName(), interval);
    }

    public final void tick(TribulationInstance instance) {
        ticks++;
        if (ticks % interval == 0) {
            ArsTrinkets.LOGGER.debug("[{}] Executing scheduled task (tick={})", this.getClass().getSimpleName(), ticks);
            execute(instance);
        }
    }

    public abstract void execute(TribulationInstance instance);

    public void handleEvent(TribulationEvent event, TribulationInstance instance) {}

    public CompoundTag serialize(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        tag.putInt("ticks", ticks);
        serializeExtra(tag, provider);
        return tag;
    }

    public void deserialize(CompoundTag tag, HolderLookup.Provider provider) {
        this.ticks = tag.getInt("ticks");
        ArsTrinkets.LOGGER.debug("[{}] Task deserialized (ticks={})", this.getClass().getSimpleName(), ticks);
        deserializeExtra(tag, provider);
    }

    protected void serializeExtra(CompoundTag tag, HolderLookup.Provider provider) {}

    protected void deserializeExtra(CompoundTag tag, HolderLookup.Provider provider) {}
}
