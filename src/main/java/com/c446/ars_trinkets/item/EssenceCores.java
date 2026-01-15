package com.c446.ars_trinkets.item;

import com.c446.ars_trinkets.ArsTrinkets;
import com.c446.ars_trinkets.capabilities.LevelingCapability;
import com.c446.ars_trinkets.capabilities.PlayerLevelHandling;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class EssenceCores extends Item {
    public int coreLevel;

    public EssenceCores(Properties p, int coreLevel) {
        super(p);
        this.coreLevel = coreLevel;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        var t = super.useOn(pContext);

        var p = pContext.getPlayer();
        var st = p != null ? p.getItemInHand(pContext.getHand()) : null;

        if (st!=null) {
            st.setCount(st.getCount()-1);
            LevelingCapability.get(p).tryAddCore(1, this.coreLevel);
            ArsTrinkets.LOGGER.debug("adding core to {}", p.getDisplayName().toString());

           // p.hurt(new DamageSource(DamageTypes.GENERIC))

        } else {
            ArsTrinkets.LOGGER.debug("wtf why is that dude null if he did an interaction??");
        }

        return t;
    }
}
