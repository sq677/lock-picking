package net.sq.lockpicking.entity.locks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.sq.lockpicking.custom_data.CustomChestData;
import net.sq.lockpicking.entity.locks.LockEntityManager;

public abstract class LockEntity extends Entity {
    private BlockPos chestPos;

    public LockEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    public void setChestPos(BlockPos pos) {
        this.chestPos = pos;
    }

    public BlockPos getChestPos() {
        return this.chestPos;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("ChestX")) {
            this.chestPos = new BlockPos(nbt.getInt("ChestX"), nbt.getInt("ChestY"), nbt.getInt("ChestZ"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.chestPos != null) {
            nbt.putInt("ChestX", chestPos.getX());
            nbt.putInt("ChestY", chestPos.getY());
            nbt.putInt("ChestZ", chestPos.getZ());
        }
    }

    @Override
    public boolean canHit() { return false; }

    @Override
    public boolean isCollidable() { return false; }
    public boolean isDoubleChest(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof ChestBlock chest))
            return false;

        Inventory inv = ChestBlock.getInventory(
                chest,
                state,
                world,
                pos,
                true
        );

        return inv != null && inv.size() > 27;
    }
    public static BlockPos getMasterChestPos(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof ChestBlock))
            return pos;

        ChestType type = state.get(ChestBlock.CHEST_TYPE);

        if (type == ChestType.SINGLE)
            return pos;

        if (type == ChestType.LEFT) {
            return pos;
        }

        Direction facing = state.get(ChestBlock.FACING);
        Direction toLeft = facing.rotateYCounterclockwise();
        return pos.offset(toLeft);
    }
    public static boolean canPlaceLock(ServerWorld world, BlockPos pos) {
        BlockPos master = getMasterChestPos(world, pos);
        return !LockEntityManager.hasLockEntity(world, master);
    }


    public void startOpening() {

    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient) {
            if (discardIfChestRemoved()) return;

            BlockState state = this.getWorld().getBlockState(this.getChestPos());
            boolean isStillChest = state.getBlock() instanceof ChestBlock;
            net.minecraft.block.entity.BlockEntity be = this.getWorld().getBlockEntity(this.getChestPos());

            boolean locked = false;
            if (isStillChest && be instanceof CustomChestData data) {
                locked = data.isLocked();
            }

            if (!locked) {
                LockEntityManager.notifyRemoval(this.getChestPos());
                this.discard();
            } else if (isStillChest) {
                updatePosition(state);
            }
        }
    }

    protected boolean discardIfChestRemoved() {
        BlockPos chestPos = this.getChestPos();
        if (chestPos == null) {
            this.discard();
            return true;
        }

        BlockState state = this.getWorld().getBlockState(chestPos);
        if (!(state.getBlock() instanceof ChestBlock) || this.getWorld().getBlockEntity(chestPos) == null) {
            LockEntityManager.notifyRemoval(chestPos);
            this.discard();
            return true;
        }

        return false;
    }

    protected void updatePositionFromChest() {
        BlockPos chestPos = this.getChestPos();
        if (chestPos == null) {
            return;
        }

        BlockState state = this.getWorld().getBlockState(chestPos);
        if (state.getBlock() instanceof ChestBlock) {
            updatePosition(state);
        }
    }

    private void updatePosition(BlockState state) {
        Direction facing = state.get(ChestBlock.FACING);
        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        BlockPos pos = this.getChestPos();

        double targetX = pos.getX() + 0.5 + facing.getOffsetX() * 0.44;
        double targetY = pos.getY() + 0.3;
        double targetZ = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.44;

        if (chestType != ChestType.SINGLE) {
            Direction offsetDir = chestType == ChestType.LEFT
                    ? facing.rotateYClockwise()
                    : facing.rotateYCounterclockwise();
            targetX += offsetDir.getOffsetX() * 0.5;
            targetZ += offsetDir.getOffsetZ() * 0.5;
        }

        double dx = Math.abs(this.getX() - targetX);
        double dz = Math.abs(this.getZ() - targetZ);
        if (dx > 0.1 || dz > 0.1) {
            this.refreshPositionAndAngles(targetX, targetY, targetZ, facing.asRotation(), 0f);
        }
    }
}
