package net.sq.lockpicking.entity.locks.diamond_lock;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sq.lockpicking.LockpickingSounds;
import net.sq.lockpicking.ModItems;
import net.sq.lockpicking.entity.locks.LockEntity;
import net.sq.lockpicking.entity.locks.LockEntityManager;
import net.minecraft.entity.ItemEntity;

public class DiamondLockEntity extends LockEntity {
    private static final TrackedData<Boolean> IS_OPENING = DataTracker.registerData(DiamondLockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_OPEN = DataTracker.registerData(DiamondLockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private BlockPos chestPos;
    public final AnimationState openAnimationState = new AnimationState();
    private int openAnimationTicks = 0;
    private static final int OPEN_ANIMATION_LENGTH = 12;

    public DiamondLockEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(IS_OPENING, false);
        builder.add(IS_OPEN, false);
    }

    public void setChestPos(BlockPos pos) {
        this.chestPos = pos;
    }

    public BlockPos getChestPos() {
        return this.chestPos;
    }

    public boolean isOpening() {
        return this.dataTracker.get(IS_OPENING);
    }

    public void setOpening(boolean opening) {
        this.dataTracker.set(IS_OPENING, opening);
    }

    public boolean isOpen() {
        return this.dataTracker.get(IS_OPEN);
    }

    public void setOpen(boolean open) {
        this.dataTracker.set(IS_OPEN, open);
    }

    @Override
    public void startOpening() {
        if (!this.getWorld().isClient && !this.isOpening() && !this.isOpen()) {
            this.setOpening(true);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("ChestX")) {
            this.chestPos = new BlockPos(
                    nbt.getInt("ChestX"),
                    nbt.getInt("ChestY"),
                    nbt.getInt("ChestZ")
            );
        }
        if (nbt.contains("IsOpen")) {
            this.setOpen(nbt.getBoolean("IsOpen"));
        }
        if (nbt.contains("IsOpening")) {
            this.setOpening(nbt.getBoolean("IsOpening"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.chestPos != null) {
            nbt.putInt("ChestX", chestPos.getX());
            nbt.putInt("ChestY", chestPos.getY());
            nbt.putInt("ChestZ", chestPos.getZ());
        }
        nbt.putBoolean("IsOpen", this.isOpen());
        nbt.putBoolean("IsOpening", this.isOpening());
    }

    @Override
    public boolean canHit() {
        return this.isOpen();
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && this.isOpen()) {
            if (this.chestPos != null) {
                net.minecraft.block.entity.BlockEntity be = this.getWorld().getBlockEntity(this.chestPos);
                if (be instanceof net.sq.lockpicking.custom_data.CustomChestData customChest) {
                    customChest.setLocked(false);
                    customChest.setLockType(null);
                    customChest.setOwnerUuid(null);
                }
                unlockOtherHalf();
            }

            ItemStack lockStack = new ItemStack(ModItems.DIAMOND_LOCK, 1);
            ItemEntity itemEntity = new ItemEntity(
                    this.getWorld(),
                    this.getX(),
                    this.getY() + 0.5,
                    this.getZ(),
                    lockStack
            );
            this.getWorld().spawnEntity(itemEntity);

            this.getWorld().playSound(null, this.getBlockPos(), LockpickingSounds.LOCK_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);

            if (this.chestPos != null) {
                LockEntityManager.notifyRemoval(this.chestPos);
            }
            this.discard();

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void unlockOtherHalf() {
        if (this.chestPos == null) return;
        net.minecraft.block.BlockState state = this.getWorld().getBlockState(this.chestPos);
        if (!(state.getBlock() instanceof net.minecraft.block.ChestBlock)) return;

        net.minecraft.block.enums.ChestType chestType = state.get(net.minecraft.block.ChestBlock.CHEST_TYPE);
        if (chestType == net.minecraft.block.enums.ChestType.SINGLE) return;

        net.minecraft.util.math.Direction facing = state.get(net.minecraft.block.ChestBlock.FACING);
        net.minecraft.util.math.Direction otherDir = chestType == net.minecraft.block.enums.ChestType.LEFT
                ? facing.rotateYClockwise()
                : facing.rotateYCounterclockwise();
        BlockPos otherPos = this.chestPos.offset(otherDir);

        net.minecraft.block.entity.BlockEntity otherBE = this.getWorld().getBlockEntity(otherPos);
        if (otherBE instanceof net.sq.lockpicking.custom_data.CustomChestData otherChest) {
            otherChest.setLocked(false);
            otherChest.setLockType(null);
            otherChest.setOwnerUuid(null);
        }
    }

    @Override
    public void tick() {
        if (this.getWorld().isClient) {
            if (this.isOpening() && !this.isOpen()) {
                if (!this.openAnimationState.isRunning()) {
                    this.openAnimationState.start(this.age);
                }
                this.openAnimationTicks++;
            }
        }

        if (!this.getWorld().isClient) {
            if (this.chestPos == null) {
                this.discard();
                return;
            }

            if (this.isOpen()) {
                return;
            }

            if (this.isOpening()) {
                this.openAnimationTicks++;
                if (this.openAnimationTicks >= OPEN_ANIMATION_LENGTH) {
                    this.setOpen(true);
                }
            }
        }
    }
}
