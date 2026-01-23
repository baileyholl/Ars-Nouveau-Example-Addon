package com.c446.ars_trinkets.entities.red_lightning;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RedLightning extends LightningBolt {
    protected @Nullable LivingEntity owner;

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
    }

    public @Nullable LivingEntity getOwner() {
        return owner;
    }

    public RedLightning(EntityType<? extends LightningBolt> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


}
