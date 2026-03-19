package net.sq.lockpicking.mixin;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sq.lockpicking.LockpickingSounds;
import net.sq.lockpicking.ModItems;
import net.sq.lockpicking.UI.LockType;
import net.sq.lockpicking.custom_data.CustomChestData;
import net.sq.lockpicking.entity.locks.LockEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestInteractionMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void preventLockedChestOpening(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient && net.sq.lockpicking.LockpickingClient.isRecentlyUnlocked(pos)) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }
        if (!world.isClient && net.sq.lockpicking.Lockpicking.isRecentlyUnlockedServer(pos)) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CustomChestData customData) {
            if (world.isClient && customData.isLocked() && net.sq.lockpicking.UnlockedChests.isUnlocked(pos)) {
                net.sq.lockpicking.UnlockedChests.remove(pos);
            }

            if (world.isClient && !customData.isLocked() && net.sq.lockpicking.UnlockedChests.isUnlocked(pos)) {
                return;
            }

            if (customData.isLocked()) {
                java.util.UUID ownerUuid = customData.getOwnerUuid();
                if (ownerUuid != null && player.getUuid().equals(ownerUuid)) {
                    if (player.isSneaking()) {
                        if (!world.isClient) {
                            LockType removedLockType = customData.getLockType();

                            customData.setLocked(false);
                            customData.setLockType(null);
                            customData.setOwnerUuid(null);
                            LockEntityManager.removeLock((ServerWorld) world, pos);

                            ItemStack lockStack = getLockItemStack(removedLockType);
                            if (lockStack != null) {
                                int selectedSlot = player.getInventory().selectedSlot;
                                ItemStack currentStack = player.getInventory().getStack(selectedSlot);

                                if (currentStack.isEmpty()) {
                                    player.getInventory().setStack(selectedSlot, lockStack);
                                } else if (currentStack.getItem() == lockStack.getItem() && currentStack.getCount() < currentStack.getMaxCount()) {
                                    currentStack.increment(1);
                                } else {
                                    if (!player.getInventory().insertStack(lockStack)) {
                                        player.dropItem(lockStack, false);
                                    }
                                }
                            }

                            world.playSound(null, pos, LockpickingSounds.LOCK_ON, SoundCategory.BLOCKS, 0.7f, 0.8f);
                        }
                        cir.setReturnValue(ActionResult.SUCCESS);
                        return;
                    } else {
                        return;
                    }
                }

                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    private ItemStack getLockItemStack(LockType lockType) {
        if (lockType == null) return null;

        Item lockItem = switch (lockType) {
            case CopperLock -> net.sq.lockpicking.ModItems.COPPER_LOCK;
            case IronLock -> net.sq.lockpicking.ModItems.IRON_LOCK;
            case GoldenLock -> net.sq.lockpicking.ModItems.GOLDEN_LOCK;
            case DiamondLock -> net.sq.lockpicking.ModItems.DIAMOND_LOCK;
            case NetheriteLock -> net.sq.lockpicking.ModItems.NETHERITE_LOCK;
            case BastionLock -> ModItems.BASTION_LOCK;
        };

        return new ItemStack(lockItem, 1);
    }
}