package net.skittle.lockpicking.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.skittle.lockpicking.ModItems;
import net.skittle.lockpicking.UI.LockType;
import net.skittle.lockpicking.custom_data.CustomChestData;
import net.skittle.lockpicking.entity.locks.LockEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    protected ServerWorld world;

    @Shadow
    protected ServerPlayerEntity player;

    @Unique
    private boolean canPlayerBreakLockedChest(BlockPos pos) {
        if (this.player.interactionManager.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        BlockEntity blockEntity = this.world.getBlockEntity(pos);
        if (blockEntity instanceof CustomChestData customChest) {
            UUID ownerUuid = customChest.getOwnerUuid();
            if (ownerUuid != null && ownerUuid.equals(this.player.getUuid())) {
                return true;
            }
        }

        BlockState state = this.world.getBlockState(pos);
        if (state.getBlock() instanceof ChestBlock) {
            ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
            if (chestType != ChestType.SINGLE) {
                Direction facing = state.get(ChestBlock.FACING);
                Direction otherDir = chestType == ChestType.LEFT
                        ? facing.rotateYClockwise()
                        : facing.rotateYCounterclockwise();
                BlockPos otherPos = pos.offset(otherDir);

                BlockEntity otherBE = this.world.getBlockEntity(otherPos);
                if (otherBE instanceof CustomChestData otherChest) {
                    UUID otherOwnerUuid = otherChest.getOwnerUuid();
                    if (otherOwnerUuid != null && otherOwnerUuid.equals(this.player.getUuid())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Unique
    private boolean isPartOfLockedDoubleChest(BlockPos pos) {
        BlockEntity blockEntity = this.world.getBlockEntity(pos);

        if (!(blockEntity instanceof ChestBlockEntity) || !(blockEntity instanceof CustomChestData customChest)) {
            return false;
        }

        if (customChest.isLocked()) {
            return true;
        }

        BlockState state = this.world.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock)) {
            return false;
        }

        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return false;
        }

        Direction facing = state.get(ChestBlock.FACING);
        Direction otherDir = chestType == ChestType.LEFT
                ? facing.rotateYClockwise()
                : facing.rotateYCounterclockwise();
        BlockPos otherPos = pos.offset(otherDir);

        BlockEntity otherBE = this.world.getBlockEntity(otherPos);
        if (otherBE instanceof CustomChestData otherChest) {
            return otherChest.isLocked();
        }

        return false;
    }

    @Unique
    private BlockPos getOtherChestHalf(BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock)) {
            return null;
        }

        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return null;
        }

        Direction facing = state.get(ChestBlock.FACING);
        Direction otherDir = chestType == ChestType.LEFT
                ? facing.rotateYClockwise()
                : facing.rotateYCounterclockwise();
        return pos.offset(otherDir);
    }

    @Unique
    private void dropLockItem(BlockPos pos, LockType lockType) {
        if (lockType == null || lockType == LockType.BastionLock) return;

        Item lockItem = switch (lockType) {
            case CopperLock -> ModItems.COPPER_LOCK;
            case IronLock -> ModItems.IRON_LOCK;
            case GoldenLock -> ModItems.GOLDEN_LOCK;
            case DiamondLock -> ModItems.DIAMOND_LOCK;
            case NetheriteLock -> ModItems.NETHERITE_LOCK;
            case BastionLock -> ModItems.BASTION_LOCK;
        };

        ItemStack lockStack = new ItemStack(lockItem, 1);
        ItemEntity itemEntity = new ItemEntity(
                this.world,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                lockStack
        );
        this.world.spawnEntity(itemEntity);
    }

    @Unique
    private void handleLockedChestBreak(BlockPos pos) {
        BlockEntity blockEntity = this.world.getBlockEntity(pos);
        if (!(blockEntity instanceof CustomChestData customChest)) {
            return;
        }

        if (!customChest.isLocked()) {
            return;
        }

        boolean isCreative = this.player.interactionManager.getGameMode() == GameMode.CREATIVE;

        if (!isCreative) {
            dropLockItem(pos, customChest.getLockType());
        }

        LockEntityManager.removeLock(this.world, pos);

        BlockPos otherPos = getOtherChestHalf(pos);
        if (otherPos != null) {
            BlockEntity otherBE = this.world.getBlockEntity(otherPos);
            if (otherBE instanceof CustomChestData otherChest) {
                otherChest.setLocked(false);
                otherChest.setLockType(null);
                otherChest.setOwnerUuid(null);
            }
        }
    }

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void preventLockedChestBreaking(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (isPartOfLockedDoubleChest(pos)) {
            if (!canPlayerBreakLockedChest(pos)) {
                cir.setReturnValue(false);
            } else {
                handleLockedChestBreak(pos);
            }
        }
    }

    @Inject(method = "continueMining", at = @At("HEAD"), cancellable = true)
    private void preventLockedChestMining(BlockState state, BlockPos pos, int failedStartMiningTime, CallbackInfoReturnable<Float> cir) {
        if (isPartOfLockedDoubleChest(pos) && !canPlayerBreakLockedChest(pos)) {
            cir.setReturnValue(0.0f);
        }
    }
}
