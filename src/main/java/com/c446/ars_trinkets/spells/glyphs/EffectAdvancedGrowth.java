package com.c446.ars_trinkets.spells.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;

public class EffectAdvancedGrowth extends AbstractEffect {
    public static final EffectAdvancedGrowth INSTANCE = new EffectAdvancedGrowth(ResourceLocation.parse("arsomega:glyph_advanced_grow"), "Advanced Growth");

    public EffectAdvancedGrowth(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        // Use AOE calculation like vanilla grow effect
        for (BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats)) {
            if (BlockUtil.destroyRespectsClaim(shooter, world, blockpos) && world instanceof ServerLevel serverLevel) {
                // Try vanilla bonemeal first
                ItemStack stack = new ItemStack(Items.BONE_MEAL, 64);

                if (BoneMealItem.applyBonemeal(stack, world, blockpos, ANFakePlayer.getPlayer(serverLevel))) {
                    if (!world.isClientSide) {
                        world.levelEvent(1505, blockpos, 0);
                    }
                } else {
                    // Try water plants on adjacent block
                    BlockPos relative = blockpos.relative(rayTraceResult.getDirection());
                    boolean flag = world.getBlockState(blockpos).isFaceSturdy(world, blockpos, rayTraceResult.getDirection());

                    if (flag && BoneMealItem.growWaterPlant(stack, world, relative, rayTraceResult.getDirection())) {
                        if (!world.isClientSide) {
                            world.levelEvent(1505, relative, 0);
                        }
                    } else {
                        // Try advanced growth for non-bonemealable crops
                        tryAdvancedGrowth(world, blockpos);
                    }
                }
            }
        }
    }

    private boolean tryAdvancedGrowth(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof CactusBlock || block instanceof SugarCaneBlock) {
            return growCactusOrSugarCane(world, pos, block);
        } else if (block instanceof VineBlock) {
            return growVine(world, pos, state, block);
        } else if (block instanceof NetherWartBlock) {
            return growNetherWart(world, pos, state, block);
        } else if (block instanceof ChorusPlantBlock || block instanceof ChorusFlowerBlock) {
            return growChorus(world, pos);
        }

        return false;
    }

    private boolean growCactusOrSugarCane(Level world, BlockPos pos, Block block) {
        BlockPos topPos = findTopmostGrowable(world, pos, block);
        BlockState topState = world.getBlockState(topPos);

        if (!topState.hasProperty(BlockStateProperties.AGE_15) || !world.isEmptyBlock(topPos.above())) {
            return false;
        }

        // Check height limit (max 3 blocks)
        int plantHeight = 1;
        while (world.getBlockState(topPos.below(plantHeight)).is(block)) {
            plantHeight++;
        }

        if (plantHeight >= 3) {
            return false;
        }

        // Apply growth
        if (!world.isClientSide) {
            world.levelEvent(2005, pos, 0);
        }

        int currentAge = topState.getValue(BlockStateProperties.AGE_15);
        int newAge = Math.min(currentAge + world.random.nextInt(20), 15);
        world.setBlock(topPos, topState.setValue(BlockStateProperties.AGE_15, newAge), 3);

        if (world instanceof ServerLevel serverWorld) {
            world.getBlockState(topPos).randomTick(serverWorld, topPos, world.random);
        }

        return true;
    }

    private boolean growVine(Level world, BlockPos pos, BlockState state, Block block) {
        if (!state.isRandomlyTicking()) {
            return false;
        }

        if (world.isClientSide) {
            world.levelEvent(1505, pos, 0);
        } else {
            world.levelEvent(2005, pos, 0);

            if (world instanceof ServerLevel serverWorld) {
                int cycles = 7 + world.random.nextInt(7);
                for (int i = 0; i <= cycles; i++) {
                    state.randomTick(serverWorld, pos, world.random);
                }
                state.updateNeighbourShapes(world, pos, 3);
            }
        }

        return true;
    }

    private boolean growNetherWart(Level world, BlockPos pos, BlockState state, Block block) {
        if (!state.isRandomlyTicking()) {
            return false;
        }

        if (!world.isClientSide) {
            world.levelEvent(2005, pos, 0);

            if (world instanceof ServerLevel serverWorld) {
                int cycles = (1 + world.random.nextInt(2)) * 11;
                for (int i = 0; i <= cycles; i++) {
                    state.randomTick(serverWorld, pos, world.random);
                }
            }
        }

        return true;
    }

    private boolean growChorus(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            world.levelEvent(2005, pos, 0);

            if (world instanceof ServerLevel serverWorld) {
                List<BlockPos> flowers = findChorusFlowers(world, pos);

                for (BlockPos flowerPos : flowers) {
                    int cycles = (1 + world.random.nextInt(2)) * 11;
                    for (int i = 0; i <= cycles; i++) {
                        BlockState flowerState = world.getBlockState(flowerPos);
                        flowerState.randomTick(serverWorld, flowerPos, world.random);
                    }
                }
            }
        }

        return true;
    }

    private BlockPos findTopmostGrowable(Level world, BlockPos pos, Block block) {
        BlockPos current = pos;
        while (world.getBlockState(current.above()).is(block)) {
            current = current.above();
        }
        return current;
    }

    private List<BlockPos> findChorusFlowers(Level world, BlockPos startPos) {
        List<BlockPos> flowers = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new LinkedList<>();
        toCheck.add(startPos);

        while (!toCheck.isEmpty()) {
            BlockPos current = toCheck.poll();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            BlockState state = world.getBlockState(current);
            if (state.getBlock() instanceof ChorusFlowerBlock) {
                flowers.add(current);
            }

            if (state.getBlock() instanceof ChorusPlantBlock || state.getBlock() instanceof ChorusFlowerBlock) {
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = current.relative(dir);
                    if (!visited.contains(neighbor)) {
                        toCheck.add(neighbor);
                    }
                }
            }
        }

        return flowers;
    }

    @Override
    protected int getDefaultManaCost() {
        return 250;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
    }

    @Override
    public String getBookDescription() {
        return "An advanced variant of the Grow spell that can accelerate the growth of plants that normally don't respond to bonemeal, such as Cactus, Sugar Cane, Vines, Nether Wart, and Chorus Plants. Also works on all plants that respond to normal bonemeal.";
    }

    @Override
    public @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
