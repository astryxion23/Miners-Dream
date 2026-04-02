package com.astryxion.astryxions_miners_dream.item;

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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MinersDreamItem extends Item {

    private static final TagKey<Block> FORGE_ORES_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "ores"));

    private static final TagKey<Block> C_ORES_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores"));

    public MinersDreamItem(Properties properties) {
        super(properties);
    }

    static boolean isOreBlock(BlockState state) {
        if (state.is(FORGE_ORES_TAG) || state.is(C_ORES_TAG)) {
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

        if (!level.isClientSide && player != null && level instanceof ServerLevel serverLevel) {

            Direction facing = context.getHorizontalDirection();
            Direction right = facing.getClockWise();
            BlockPos playerFeetPos = player.getOnPos();

            if (!MinersDreamTunnelScheduler.enqueueIfIdle(
                    serverLevel,
                    player.getUUID(),
                    context.getHand(),
                    player.getAbilities().instabuild,
                    playerFeetPos,
                    facing,
                    right
            )) {
                return InteractionResult.PASS;
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
