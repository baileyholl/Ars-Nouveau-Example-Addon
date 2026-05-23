package com.c446.ars_trinkets.tribulations;

public abstract class TribulationEvent {
    private final TribulationInstance instance;

    protected TribulationEvent(TribulationInstance instance) {
        this.instance = instance;
    }

    public TribulationInstance getInstance() {
        return instance;
    }
}
