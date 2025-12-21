package com.c446.ars_trinkets.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class LevelModifiedEvent extends Event {
    public Player entity;
    public Integer levelCurrent;

    public static class Pre extends LevelModifiedEvent implements ICancellableEvent {
        protected Integer levelNext;

        public Pre(Player p, Integer newLevel, Integer oldLevel) {
            this.entity = p;
            this.levelNext = newLevel;
            this.levelCurrent = oldLevel;
        }
    }

    static class Post extends LevelModifiedEvent {
        Post(Player p, Integer newLevel) {
            this.entity = p;
            this.levelCurrent = newLevel;
        }
    }
}
