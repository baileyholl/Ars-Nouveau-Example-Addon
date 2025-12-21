package com.c446.ars_trinkets.capabilities;

import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class LevelingCapability implements INBTSerializable<CompoundTag> {
    short level; // in range of 0-10 ; 10: divine 0: NONE
    long souls; //

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var cTag = new CompoundTag();
        cTag.putLong("souls", this.souls);
        cTag.putShort("level", this.level);
        return cTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag cTag) {
        this.level = cTag.getShort("level");
        this.souls = cTag.getLong("souls");
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

    public int getBonusMana() {
        return Config.Common.MANA_BONUS_PER_LEVEL.get().get(this.level - 1);
    }

    public int getBonusRegen() {
        return Config.Common.MANA_REGEN_BONUS_PER_LEVEL.get().get(this.level - 1);
    }

    public double getDamageMult() {
        return Config.Common.DAMAGE_BONUS_PER_LEVEL.get().get(this.level - 1);
    }
}
