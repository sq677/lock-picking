package net.sq.lockpicking.entity.locks.bastion_lock;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sq.lockpicking.Lockpicking;
import net.sq.lockpicking.entity.locks.LockEntity;
import net.sq.lockpicking.entity.locks.LockEntityManager;

public class BastionLockEntity extends LockEntity {

    private static final TrackedData<Boolean> IS_OPENING = DataTracker.registerData(BastionLockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private BlockPos chestPos;

    public final AnimationState openAnimationState = new AnimationState();
    private int openAnimationTicks = 0;
    private static final int OPEN_ANIMATION_LENGTH = 10;

    private int discardDelay = -1;
    private static final int DISCARD_DELAY_TICKS = 15;

    public BastionLockEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(IS_OPENING, false);
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

    public boolean isOpenAnimationFinished() {
        return this.isOpening() && this.openAnimationTicks >= OPEN_ANIMATION_LENGTH;
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
    public boolean canHit() {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public void tick() {

        if (this.getWorld().isClient) {
            if (this.isOpening()) {
                Lockpicking.LOGGER.info("[CLIENT] BastionLock isOpening=true, animTicks=" + this.openAnimationTicks);
                if (!this.openAnimationState.isRunning()) {
                    Lockpicking.LOGGER.info("[CLIENT] Starting animation at age=" + this.age);
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

            if (this.discardDelay >= 0) {
                Lockpicking.LOGGER.info("[SERVER] Discard delay countdown: " + this.discardDelay);
                this.discardDelay--;
                if (this.discardDelay < 0) {
                    Lockpicking.LOGGER.info("[SERVER] Discarding entity");
                    LockEntityManager.notifyRemoval(this.chestPos);
                    this.discard();
                }
                return;
            }

            net.minecraft.block.entity.BlockEntity be = this.getWorld().getBlockEntity(this.chestPos);

            boolean isLocked = false;
            if (be instanceof net.sq.lockpicking.custom_data.CustomChestData customData) {
                isLocked = customData.isLocked();
            }

            if (!isLocked) {
                Lockpicking.LOGGER.info("[SERVER] Lock unlocked! Setting isOpening=true, starting delay=" + DISCARD_DELAY_TICKS);
                this.setOpening(true);
                this.discardDelay = DISCARD_DELAY_TICKS;
            }
        }
    }
}
