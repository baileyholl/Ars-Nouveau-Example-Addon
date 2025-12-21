package com.c446.ars_trinkets.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class CoresModifiedEvent extends Event implements ICancellableEvent {
    public final Player player;
    public int oldCore;
    public int newCore;

    public CoresModifiedEvent(Player player, int oldCore, int newCore) {
        this.player = player;
        this.newCore=newCore;
        this.oldCore=oldCore;
    }
}
