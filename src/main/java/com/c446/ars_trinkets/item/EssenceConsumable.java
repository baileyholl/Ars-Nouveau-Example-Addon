package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EssenceConsumable extends Item {
    final int level;
    final int soulQuantity;

    public EssenceConsumable(Properties pProperties, int minLevel, int soulQuantity) {
        super(pProperties.food(
                new FoodProperties.Builder()
                        .nutrition(0)
                        .saturationModifier(0f)
                        .build()
        ));
        this.level = minLevel;
        this.soulQuantity = soulQuantity;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack result = super.finishUsingItem(pStack, pLevel, pLivingEntity);

        if (pLevel instanceof ServerLevel && pLivingEntity instanceof ServerPlayer p) {
            var cap = LevelingCapability.get(p);
            ArsTrinkets.LOGGER.debug("trying to use essence {} @ {}", this.level, cap.level);
            ArsTrinkets.LOGGER.debug("old soul count {}", cap.souls);
            if (cap.level <= this.level) {
                ArsTrinkets.LOGGER.debug("to add {}", this.soulQuantity);
                cap.addSoul(this.soulQuantity, p);
                ArsTrinkets.LOGGER.debug("new soul count {}", cap.souls);
            }
        }

        return result;
    }
}
