package com.example.an_addon.item;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ExampleCosmetic extends ModItem implements ICosmeticItem {

    public ExampleCosmetic(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity entity, InteractionHand pUsedHand) {
        if(entity instanceof IDecoratable starbuncle ){ //&& canWear(entity)
            starbuncle.setCosmeticItem(pStack.split(1));
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(pStack, pPlayer, entity, pUsedHand);
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

    @Override
    public ItemTransforms.TransformType getTransformType() {
        return ItemTransforms.TransformType.HEAD;
    }

    @Override
    public Vec3 getTranslations() {
        return new Vec3(0,0.43,0);
    }

    @Override
    public Vec3 getScaling() {
        return new Vec3(1,1,1);
    }

}
