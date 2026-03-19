package net.sq.lockpicking.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sq.lockpicking.UI.LockType;
import net.sq.lockpicking.custom_data.CustomChestData;
import net.sq.lockpicking.entity.locks.LockEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends BlockEntity implements CustomChestData {

    @Unique
    private boolean isLocked = false;
    @Unique
    private LockType lockType = LockType.IronLock;
    @Unique
    private long lockSeed = 0;
    @Unique
    private java.util.UUID ownerUuid = null;
    public ChestBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void writeCustomNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
        nbt.putBoolean("lockpicking:is_locked", isLocked);
        if (lockType != null) {
            nbt.putString("lockpicking:lock_type", lockType.name());
        }
        nbt.putLong("lockpicking:lock_seed", lockSeed);
        if (ownerUuid != null) {
            nbt.putUuid("lockpicking:owner_uuid", ownerUuid);
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readCustomNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
        RegistryKey<LootTable> lootTable = lockpicking$getLootTable();
        net.sq.lockpicking.Lockpicking.LOGGER.info("Chest at " + this.pos + " - lootTable: " + (lootTable != null ? lootTable.getValue().getPath() : "NULL") + " - hasLockData: " + nbt.contains("lockpicking:is_locked"));

        if (nbt.contains("lockpicking:is_locked")) {
            this.isLocked = nbt.getBoolean("lockpicking:is_locked");
        } else {
            if (lootTable != null) {
                String path = lootTable.getValue().getPath();
                if (path.contains("village") || path.contains("nether_bridge") || path.contains("bastion")
                        || path.contains("trial_chambers") || path.contains("ancient_city")) {
                    this.isLocked = true;
                }
            } else {
                if (lockpicking$isInNetherFortress()) {
                    this.isLocked = true;
                    this.lockType = LockType.GoldenLock;
                    net.sq.lockpicking.Lockpicking.LOGGER.info("Detected Nether Fortress chest at " + this.pos + " by surroundings");
                }
            }
        }

        if (nbt.contains("lockpicking:lock_type")) {
            try {
                this.lockType = LockType.valueOf(nbt.getString("lockpicking:lock_type"));
            } catch (IllegalArgumentException e) {
                this.lockType = LockType.IronLock;
            }
        } else if (this.isLocked) {
            if (lootTable != null) {
                String path = lootTable.getValue().getPath();
                if (path.contains("weaponsmith") || path.contains("toolsmith") || path.contains("armorer")) {
                    this.lockType = LockType.IronLock;
                } else if (path.contains("village")) {
                    this.lockType = LockType.CopperLock;
                }
                else if (path.contains("nether_bridge")) {
                    this.lockType = LockType.GoldenLock;
                }
                else if (path.contains("bastion_treasure")) {
                    this.lockType = LockType.BastionLock;
                    net.sq.lockpicking.Lockpicking.LOGGER.info("Set NETHERITE lock for: " + path);

                } else if (path.contains("bastion")) {
                    this.lockType = LockType.GoldenLock;
                    net.sq.lockpicking.Lockpicking.LOGGER.info("Set GOLDEN lock for: " + path);
                }
                else if (path.contains("trial_chambers")) {
                    this.lockType = LockType.CopperLock;
                }
                else if (path.contains("ancient_city")) {
                    this.lockType = LockType.DiamondLock;
                }
                else {
                    this.lockType = LockType.CopperLock;
                }
                net.sq.lockpicking.Lockpicking.LOGGER.info("Final lockType for " + this.pos + ": " + this.lockType);
            }
        }

        if (nbt.contains("lockpicking:lock_seed")) {
            this.lockSeed = nbt.getLong("lockpicking:lock_seed");
        }

        if (nbt.containsUuid("lockpicking:owner_uuid")) {
            this.ownerUuid = nbt.getUuid("lockpicking:owner_uuid");
        }

        if (this.isLocked && this.lockSeed == 0) {
            this.lockSeed = (this.world != null) ? this.world.random.nextLong() : new java.util.Random().nextLong();
        }

        if (this.world instanceof ServerWorld serverWorld) {
            if (this.isLocked) {
                LockEntityManager.queueSpawn(serverWorld, this.pos, this.getLockType());
            }

            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }
    @Override
    public long getLockSeed() {
        return this.lockSeed;
    }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((ChestBlockEntity) (Object) this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        nbt.putBoolean("lockpicking:is_locked", this.isLocked);
        if (this.lockType != null) {
            nbt.putString("lockpicking:lock_type", this.lockType.name());
        }
        nbt.putLong("lockpicking:lock_seed", this.lockSeed);
        if (this.ownerUuid != null) {
            nbt.putUuid("lockpicking:owner_uuid", this.ownerUuid);
        }
        return nbt;
    }

    @Unique
    private RegistryKey<LootTable> lockpicking$getLootTable() {
        LootableContainerBlockEntity self = (LootableContainerBlockEntity) (Object) this;
        return self.getLootTable();
    }

    @Unique
    private boolean lockpicking$isInNetherFortress() {
        if (this.world == null) return false;
        if (!this.world.getRegistryKey().equals(World.NETHER)) return false;

        int netherBrickCount = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos checkPos = this.pos.add(dx, dy, dz);
                    BlockState state = this.world.getBlockState(checkPos);
                    if (state.isOf(Blocks.NETHER_BRICKS) || state.isOf(Blocks.NETHER_BRICK_FENCE) ||
                        state.isOf(Blocks.NETHER_BRICK_STAIRS) || state.isOf(Blocks.NETHER_BRICK_SLAB)) {
                        netherBrickCount++;
                    }
                }
            }
        }
        return netherBrickCount >= 5;
    }

    @Override
    public boolean isLocked() {
        return this.isLocked;
    }

    @Override
    public LockType getLockType() {
        return this.lockType;
    }

    @Override
    public void setLockType(LockType type) {
        this.lockType = type;
        this.markDirty();
    }
    @Override
    public void setLocked(boolean locked) {
        this.isLocked = locked;
        if (locked) {
            this.lockSeed = (this.world != null) ? this.world.random.nextLong() : new java.util.Random().nextLong();
        } else {
            this.lockSeed = 0;
            this.ownerUuid = null;
        }
        if (locked && this.lockSeed == 0) {
            this.lockSeed = this.world != null ? this.world.random.nextLong() : new java.util.Random().nextLong();
        }
        this.markDirty();

        if (this.world != null && !this.world.isClient) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);

            if (this.world instanceof ServerWorld serverWorld) {
                if (locked && this.lockType != null) {
                    LockEntityManager.queueSpawn(serverWorld, this.pos, this.getLockType());
                }
            }
        }
    }

    @Override
    public java.util.UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    @Override
    public void setOwnerUuid(java.util.UUID uuid) {
        this.ownerUuid = uuid;
        this.markDirty();
    }
}