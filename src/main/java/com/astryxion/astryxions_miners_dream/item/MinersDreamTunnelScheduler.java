package com.astryxion.astryxions_miners_dream.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayDeque;
import java.util.UUID;

/**
 * Runs Miner's Dream excavation over many server ticks so block updates do not freeze the game thread.
 */
public final class MinersDreamTunnelScheduler {

    private static final int MAX_BLOCK_MUTATIONS_PER_TICK = 96;

    private static boolean registered;
    private static final ArrayDeque<TunnelTask> QUEUE = new ArrayDeque<>();

    private MinersDreamTunnelScheduler() {}

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, MinersDreamTunnelScheduler::onServerTickPost);
    }

    private static void onServerTickPost(ServerTickEvent.Post event) {
        if (QUEUE.isEmpty()) {
            return;
        }
        TunnelTask task = QUEUE.peekFirst();
        if (task.level.isClientSide || task.level.getServer() == null) {
            QUEUE.removeFirst();
            return;
        }
        boolean finished = task.tickBatch();
        if (finished) {
            QUEUE.removeFirst();
        }
    }

    /**
     * @return false if this player already has a tunnel running in this level (avoid overlapping jobs / item exploits)
     */
    public static boolean enqueueIfIdle(
            ServerLevel level,
            UUID playerId,
            InteractionHand hand,
            boolean instabuild,
            BlockPos playerFeetPos,
            Direction facing,
            Direction right
    ) {
        for (TunnelTask existing : QUEUE) {
            if (existing.isSamePlayerAndLevel(playerId, level)) {
                return false;
            }
        }
        QUEUE.addLast(new TunnelTask(level, playerId, hand, instabuild, playerFeetPos, facing, right));
        return true;
    }

    private static final class TunnelTask {
        private final ServerLevel level;
        private final UUID playerId;
        private final InteractionHand hand;
        private final boolean instabuild;
        private final BlockPos playerFeetPos;
        private final Direction facing;
        private final Direction right;
        private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        private int l = 1;
        private int w = -5;
        private int h = 1;

        TunnelTask(
                ServerLevel level,
                UUID playerId,
                InteractionHand hand,
                boolean instabuild,
                BlockPos playerFeetPos,
                Direction facing,
                Direction right
        ) {
            this.level = level;
            this.playerId = playerId;
            this.hand = hand;
            this.instabuild = instabuild;
            this.playerFeetPos = playerFeetPos.immutable();
            this.facing = facing;
            this.right = right;
        }

        boolean isSamePlayerAndLevel(UUID id, ServerLevel sl) {
            return playerId.equals(id) && level == sl;
        }

        /**
         * @return true when the full tunnel is done (including item consumption)
         */
        boolean tickBatch() {
            int mutations = 0;
            while (l <= 65 && mutations < MAX_BLOCK_MUTATIONS_PER_TICK) {
                mutablePos.set(playerFeetPos)
                        .move(facing, l)
                        .move(right, w)
                        .move(Direction.UP, h);

                BlockState blockState = level.getBlockState(mutablePos);

                if (!blockState.isAir()) {
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
                        mutations++;
                    } else {
                        boolean isOre = MinersDreamItem.isOreBlock(blockState);
                        if (!isOre) {
                            level.destroyBlock(mutablePos, false);
                            mutations++;
                        }
                    }
                }

                h++;
                if (h > 5) {
                    h = 1;
                    w++;
                    if (w > 5) {
                        w = -5;
                        l++;
                    }
                }
            }

            if (l > 65) {
                consumeItemIfNeeded();
                return true;
            }
            return false;
        }

        private void consumeItemIfNeeded() {
            if (instabuild) {
                return;
            }
            Player player = level.getServer().getPlayerList().getPlayer(playerId);
            if (player == null) {
                return;
            }
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(ModItems.MINERS_DREAM.get())) {
                stack.shrink(1);
            }
        }
    }
}
