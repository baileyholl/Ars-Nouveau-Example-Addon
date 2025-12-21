package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.capabilities.LevelingCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EssenceCores extends Item {
    public int coreLevel;

    public EssenceCores(Properties p, int coreLevel) {
        super(p);
        this.coreLevel = coreLevel;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof Player p && pStack.getItem() instanceof EssenceCores c) {
            LevelingCapability.tryAddCore(p, 1, c.coreLevel);
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}
