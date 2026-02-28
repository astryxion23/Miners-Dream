package com.astryxion.astryxions_miners_dream.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MinersDreamItem extends Item {

    // Forge ores tag (modern constructor)
    private static final TagKey<Block> ORES_TAG =
            BlockTags.create(new ResourceLocation("forge", "ores"));

    public MinersDreamItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (!level.isClientSide && player != null) {

            Direction facing = context.getHorizontalDirection();
            Direction right = facing.getClockWise();
            BlockPos playerFeetPos = player.getOnPos();

            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            // Tunnel dimensions
            for (int l = 1; l <= 65; l++) {          // Length
                for (int w = -5; w <= 5; w++) {      // Width (11)
                    for (int h = 1; h <= 5; h++) {   // Height (5)

                        mutablePos.set(playerFeetPos)
                                .move(facing, l)
                                .move(right, w)
                                .move(Direction.UP, h);

                        BlockState blockState = level.getBlockState(mutablePos);

                        if (blockState.isAir()) {
                            continue;
                        }

                        boolean isTorchSpot =
                                (w == 0) && (h == 1) && ((l - 1) % 5 == 0);

                        if (isTorchSpot) {
                            BlockState floorState = level.getBlockState(mutablePos.below());
                            if (floorState.isSolid()) {
                                level.setBlock(
                                        mutablePos,
                                        Blocks.REDSTONE_TORCH.defaultBlockState(),
                                        3
                                );
                            } else {
                                level.destroyBlock(mutablePos, false);
                            }
                        } else {
                            boolean isOre = blockState.is(ORES_TAG);
                            if (!isOre) {
                                level.destroyBlock(mutablePos, false);
                            }
                        }
                    }
                }
            }

            // Consume item (not in creative)
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
