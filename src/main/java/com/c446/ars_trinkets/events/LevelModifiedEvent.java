package com.c446.ars_trinkets.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class LevelModifiedEvent extends Event {
    public Player entity;
    public Integer levelCurrent;

    public static class Pre extends LevelModifiedEvent implements ICancellableEvent {
        public short newLevel;

        public short getNewLevel() {
            return newLevel;
        }

        public Pre(Player p, short newLevel, Integer oldLevel) {
            this.entity = p;
            this.newLevel = newLevel;
            this.levelCurrent = oldLevel;
        }
    }

   public static class Post extends LevelModifiedEvent {
        public Post(Player p, Integer newLevel) {
            this.entity = p;
            this.levelCurrent = newLevel;
        }
    }
}
