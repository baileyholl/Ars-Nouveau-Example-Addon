package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.capabilities.LevelingCapability;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class LevelReset extends Item {
    public LevelReset(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(@NotNull UseOnContext pContext) {
        var t = super.useOn(pContext);
        var p = pContext.getPlayer() != null ? pContext.getPlayer() : null;

        if (p != null) {
            LevelingCapability.get(p).reset();
        }
        return t;
    }
}
