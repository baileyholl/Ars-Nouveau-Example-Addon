package com.c446.ars_trinkets.tribulations;

public class PhaseChangedEvent extends TribulationEvent {
    private final int newPhase;

    public PhaseChangedEvent(TribulationInstance instance, int newPhase) {
        super(instance);
        this.newPhase = newPhase;
    }

    public int getNewPhase() {
        return newPhase;
    }
}
