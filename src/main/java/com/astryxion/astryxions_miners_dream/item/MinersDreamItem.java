package com.astryxion.astryxions_miners_dream.item;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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

    private static final TagKey<Block> FORGE_ORES_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "ores"));

    public MinersDreamItem(Properties properties) {
        super(properties);
    }

    private static boolean isOreBlock(BlockState state) {
        if (state.is(FORGE_ORES_TAG) || state.is(ConventionalBlockTags.ORES)) {
            return true;
        }
        if (state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.COPPER_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.EMERALD_ORES)
                || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.REDSTONE_ORES)) {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id == null) {
            return false;
        }
        String path = id.getPath();
        if (path.endsWith("_ore")) {
            return true;
        }
        return path.startsWith("ore_");
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
                            BlockPos floorPos = mutablePos.below();
                            BlockState floorState = level.getBlockState(floorPos);
                            if (floorState.isFaceSturdy(level, floorPos, Direction.UP)) {
                                level.setBlock(
                                        mutablePos,
                                        Blocks.REDSTONE_TORCH.defaultBlockState(),
                                        3
                                );
                            } else {
                                level.destroyBlock(mutablePos, false);
                            }
                        } else {
                            boolean isOre = isOreBlock(blockState);
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
