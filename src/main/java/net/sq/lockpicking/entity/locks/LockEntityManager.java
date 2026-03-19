package net.sq.lockpicking.entity.locks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.sq.lockpicking.UI.LockType;
import net.sq.lockpicking.entity.ModEntities;
import net.sq.lockpicking.entity.locks.bastion_lock.BastionLockEntity;
import net.sq.lockpicking.entity.locks.copper_lock.CopperLockEntity;
import net.sq.lockpicking.entity.locks.diamond_lock.DiamondLockEntity;
import net.sq.lockpicking.entity.locks.golden_lock.GoldenLockEntity;
import net.sq.lockpicking.entity.locks.iron_lock.IronLockEntity;
import net.sq.lockpicking.entity.locks.netherite_lock.NetheriteLockEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LockEntityManager {

    private static final ConcurrentLinkedQueue<LockSpawnRequest> spawnQueue = new ConcurrentLinkedQueue<>();
    private static final Set<BlockPos> processedPositions = new HashSet<>();
    public record LockSpawnRequest(ServerWorld world, BlockPos pos, LockType type) {}

    public static void notifyRemoval(BlockPos pos) {
        if (pos != null) {
            processedPositions.remove(pos.toImmutable());
        }
    }

    public static void queueSpawn(ServerWorld world, BlockPos pos, LockType type) {
        BlockPos masterPos = LockEntity.getMasterChestPos(world, pos).toImmutable();
        if (!processedPositions.contains(masterPos)) {
            spawnQueue.add(new LockSpawnRequest(world, masterPos, type));
        }
    }

    public static void processQueue() {
        int processed = 0;

        while (!spawnQueue.isEmpty() && processed < 10) {

            LockSpawnRequest request = spawnQueue.poll();
            if (request != null) {
                spawnLock(request.world(), request.pos(), request.type());
                processed++;
            }
        }
    }

    public static void spawnLock(ServerWorld world, BlockPos chestPos, LockType type) {
        BlockPos immutablePos = chestPos.toImmutable();

        if (hasLockEntity(world, immutablePos)) {
            processedPositions.add(immutablePos);
            return;
        }

        BlockState state = world.getBlockState(immutablePos);
        Direction facing = state.getBlock() instanceof ChestBlock ? state.get(ChestBlock.FACING) : Direction.NORTH;

        double x = immutablePos.getX() + 0.5 + facing.getOffsetX() * 0.44;
        double y = immutablePos.getY() + 0.3;
        double z = immutablePos.getZ() + 0.5 + facing.getOffsetZ() * 0.44;

        if (state.getBlock() instanceof ChestBlock) {
            ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
            if (chestType != ChestType.SINGLE) {
                Direction offsetDir = chestType == ChestType.LEFT
                        ? facing.rotateYClockwise()
                        : facing.rotateYCounterclockwise();
                x += offsetDir.getOffsetX() * 0.5;
                z += offsetDir.getOffsetZ() * 0.5;
            }
        }

        LockEntity lockEntity = switch (type) {
            case CopperLock -> new CopperLockEntity(ModEntities.COPPER_LOCK, world);
            case IronLock -> new IronLockEntity(ModEntities.IRON_LOCK, world);
            case GoldenLock -> new GoldenLockEntity(ModEntities.GOLDEN_LOCK, world);
            case DiamondLock -> new DiamondLockEntity(ModEntities.DIAMOND_LOCK, world);
            case NetheriteLock -> new NetheriteLockEntity(ModEntities.NETHERITE_LOCK, world);
            case BastionLock -> new BastionLockEntity(ModEntities.BASTION_LOCK, world);
        };

        lockEntity.refreshPositionAndAngles(x, y, z, facing.asRotation(), 0f);
        lockEntity.setChestPos(immutablePos);
        world.spawnEntity(lockEntity);

        processedPositions.add(immutablePos);
    }

    public static void removeLock(ServerWorld world, BlockPos chestPos) {
        BlockPos masterPos = LockEntity.getMasterChestPos(world, chestPos).toImmutable();

        List<LockEntity> locks = world.getEntitiesByClass(
                LockEntity.class,
                new Box(masterPos).expand(2.0),
                lock -> masterPos.equals(lock.getChestPos())
        );

        for (LockEntity lock : locks) {
            lock.discard();
        }

        processedPositions.remove(masterPos);
    }

    public static boolean hasLockEntity(ServerWorld world, BlockPos chestPos) {
        BlockPos masterPos = LockEntity.getMasterChestPos(world, chestPos).toImmutable();
        List<LockEntity> locks = world.getEntitiesByClass(
                LockEntity.class,
                new Box(masterPos).expand(2.0),
                lock -> masterPos.equals(lock.getChestPos())
        );
        return !locks.isEmpty();
    }

    public static void clearProcessedPositions() {
        processedPositions.clear();
    }
}