package com.c446.ars_trinkets.capabilities;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.Config;
import com.c446.ars_trinkets.events.LevelModifiedEvent;
import com.c446.ars_trinkets.registry.AttributeRegistry;
import com.c446.ars_trinkets.registry.CapabilityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import top.theillusivec4.curios.platform.NeoForgeCurios;

public class LevelingCapability implements INBTSerializable<CompoundTag> {
    public short level; // in range of 0-10 ; 10: divine 0: NONE
    public int cores; // in range of 0-9
    public long souls; //
    public boolean cursed = false;

    public float getWorldDifficultyIncrease(){
        switch (this.level){
            default: return 1f;
            case 1 : return 1.5f;
            case 2 : return 2.5f;
            case 3 : return 3f;
            case 4 : return 4f;
            case 5 : return 6f;
            case 6 : return 8f;
            case 7 : return 10f;
            case 8 : return 15f;
            case 9 : return 25f;
            case 10: return 40f;
        }


    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var cTag = new CompoundTag();
        cTag.putLong("souls", this.souls);
        cTag.putShort("level", this.level);
        cTag.putInt("cores", this.cores);
        cTag.putBoolean("cursed", this.cursed);
        return cTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag cTag) {
        this.level = cTag.getShort("level");
        this.souls = cTag.getLong("souls");
        this.cores = cTag.getInt("cores");
        this.cursed = cTag.getBoolean("cursed");
    }

    public static LevelingCapability get(Player p) {
        return p.getData(CapabilityRegistry.LEVEL_CAP);
    }

    public void addSoul(int soulsToAdd, Player player) {
        var soulQuantityNeeded = Config.Common.SOUL_QUANTITY_FOR_LEVEL.get().get(this.level);
        ArsTrinkets.LOGGER.debug("souls for level-up : {}", soulQuantityNeeded);
        if (souls + soulsToAdd >= soulQuantityNeeded) {
            ArsTrinkets.LOGGER.debug("attempting to level up");

            var pre = new LevelModifiedEvent.Pre(player, (short) (this.level + 1), (int) this.level);
            NeoForge.EVENT_BUS.post(pre);

            if (pre.isCanceled() || pre.newLevel > Config.Common.MAX_LEVEL_ALLOWED.getAsInt()) {
                ArsTrinkets.LOGGER.debug("level-up event cancelled ! -- new level : {} -- old level : {}", pre.getNewLevel(), this.level);
            } else{
                level = pre.newLevel;

                var post = new LevelModifiedEvent.Post(player, (int) this.level);
                NeoForge.EVENT_BUS.post(post);
            }


        } else {

            //TODO: implement soul-steal
            this.souls += soulsToAdd;//* player.getAttributeValue();
        }
    }

    public void tryAddCore(int coreNumbers, int coreLevel) {
        if (this.level <= coreLevel * 3) {
            this.cores += coreNumbers;
            ArsTrinkets.LOGGER.debug("adding {} core @ {} cores ; level {} @ level {}", coreNumbers, this.cores, coreLevel, level);
        }
    }

    public int getCoreMult() {
        return (this.cores + 1);
    }

    public int getBonusMana() {
        return this.level == 0 ? 0 : Config.Common.MANA_BONUS_PER_LEVEL.get().get(this.level - 1) * getCoreMult();
    }

    public int getBonusRegen() {
        return this.level == 0 ? 0 : Config.Common.MANA_REGEN_BONUS_PER_LEVEL.get().get(this.level - 1) * getCoreMult();
    }

    public double getDamageMult() {
        return this.level == 0 ? 1d : Config.Common.DAMAGE_BONUS_PER_LEVEL.get().get(this.level - 1);
    }

    public Component getTitle() {
        if (this.level == 0) return Component.translatable("text.ars_trinkets.titles.asc0");

        return Component.translatable("text.ars_trinkets.titles." + (this.cursed ? "dsc" + (this.level): "asc" + (this.level )));
    }

    public void unsafeSetLevel(int newLevelForced) {
        this.level = (short) newLevelForced;
    }

    public void reset() {
        this.level = 0;
        this.souls = 0;
        this.cores = 0;
        this.cursed = false;
    }
}
