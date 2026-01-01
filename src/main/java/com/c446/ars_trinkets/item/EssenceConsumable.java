package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
        super(pProperties);
        this.level = minLevel;
        this.soulQuantity = soulQuantity;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel() instanceof ServerLevel serverLevel) {
            var pStack = pContext.getItemInHand();
            var p = (ServerPlayer) pContext.getPlayer();
            var cap = LevelingCapability.get(p);
            ArsTrinkets.LOGGER.debug("trying to use essence {} @ {}", this.level, cap.level);
            ArsTrinkets.LOGGER.debug("old soul count {}", cap.souls);
            if (cap.level <= this.level) {
                pStack.setCount(pStack.getCount() - 1);
                ArsTrinkets.LOGGER.debug("to add {}", this.soulQuantity);

                cap.addSoul(this.soulQuantity, p);

                ArsTrinkets.LOGGER.debug("new soul count {}", cap.souls);
            }
        }
        return super.useOn(pContext);
    }
}
