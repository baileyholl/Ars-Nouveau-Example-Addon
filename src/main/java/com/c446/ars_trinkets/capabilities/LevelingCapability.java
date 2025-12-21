package com.c446.ars_trinkets.capabilities;

import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.events.CoresModifiedEvent;
import com.c446.ars_trinkets.events.LevelModifiedEvent;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class LevelingCapability implements INBTSerializable<CompoundTag> {
    short level; // in range of 0-10 ; 10: divine 0: NONE
    int cores; // in range of 0-9
    long souls; //
    boolean cursed = false;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var cTag = new CompoundTag();
        cTag.putLong("souls", this.souls);
        cTag.putShort("level", this.level);
        cTag.putBoolean("cursed", this.cursed);
        return cTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag cTag) {
        this.level = cTag.getShort("level");
        this.souls = cTag.getLong("souls");
        this.cursed = cTag.getBoolean("cursed");
    }

    public static void addSoul(Player p, int soulNbr) {
        if (!p.hasData(CapabilityRegistry.LEVEL_CAP)) {
            p.setData(CapabilityRegistry.LEVEL_CAP, new LevelingCapability());
        }

        var cap = p.getData(CapabilityRegistry.LEVEL_CAP);

        if (cap.souls + soulNbr >= Config.Common.SOUL_QUANTITY_FOR_LEVEL.get().get(cap.level)) {
            cap.level += 1;
            var pre = new LevelModifiedEvent.Pre(p, (int) cap.level, cap.level + 1);
            NeoForge.EVENT_BUS.post(pre);

            if (!pre.isCanceled()) {
                cap.level += 1;
            }
        }
    }

    public static void tryAddCore(Player p, int coreNumbers, int coreLevel) {
        var cap = p.getData(CapabilityRegistry.LEVEL_CAP);
        if (cap.level > coreLevel) {
            return;
        } else{
            var event = new CoresModifiedEvent(p, cap.cores, cap.cores+1);
            NeoForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                cap.cores = event.newCore;
            }

        }
    }

    public int getBonusMana() {
        return Config.Common.MANA_BONUS_PER_LEVEL.get().get(this.level - 1);
    }

    public int getBonusRegen() {
        return Config.Common.MANA_REGEN_BONUS_PER_LEVEL.get().get(this.level - 1);
    }

    public double getDamageMult() {
        return Config.Common.DAMAGE_BONUS_PER_LEVEL.get().get(this.level - 1);
    }

    public Component getTitle() {
        return Component.translatable("text.ars_trinkets.titles." + (this.cursed ? "dsc" + this.level : "asc" + this.level));
    }
}
