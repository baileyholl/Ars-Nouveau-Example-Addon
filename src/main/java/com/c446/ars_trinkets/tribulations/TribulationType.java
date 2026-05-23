package com.c446.ars_trinkets.tribulations;

import com.c446.ars_trinkets.ArsTrinkets;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Function;

public class TribulationType {


    private final Function<TribulationInstance, List<ScheduledTask>> taskFactory;

    public TribulationType(Function<TribulationInstance, List<ScheduledTask>> taskFactory) {
        this.taskFactory = taskFactory;
    }

    public List<ScheduledTask> createTasks(TribulationInstance instance) {
        return taskFactory.apply(instance);
    }
}
