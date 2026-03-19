package net.skittle.lockpicking.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.skittle.lockpicking.LockpickingSounds;
import net.skittle.lockpicking.ModItems;
import net.skittle.lockpicking.UI.LockType;
import net.skittle.lockpicking.custom_data.CustomChestData;
import net.skittle.lockpicking.entity.locks.LockEntity;
import net.skittle.lockpicking.entity.locks.LockEntityManager;

public class LockItem extends Item {
    private final LockType lockType;

    public LockItem(Settings settings, LockType lockType) {
        super(settings);
        this.lockType = lockType;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();

        if (!(state.getBlock() instanceof ChestBlock)) {
            return ActionResult.PASS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof ChestBlockEntity) || !(blockEntity instanceof CustomChestData customChest)) {
            return ActionResult.PASS;
        }

        if (player == null) {
            return ActionResult.PASS;
        }

        if (player.isSneaking() && customChest.isLocked()) {
            java.util.UUID ownerUuid = customChest.getOwnerUuid();

            if (ownerUuid != null && player.getUuid().equals(ownerUuid)) {
                if (!world.isClient) {
                    LockType removedLockType = customChest.getLockType();

                    customChest.setLocked(false);
                    customChest.setLockType(null);
                    customChest.setOwnerUuid(null);

                    unlockOtherHalf(world, pos, state);

                    ItemStack lockStack = getLockItemStack(removedLockType);
                    if (lockStack != null) {
                        int selectedSlot = player.getInventory().selectedSlot;
                        ItemStack currentStack = player.getInventory().getStack(selectedSlot);

                        if (currentStack.isEmpty()) {
                            player.getInventory().setStack(selectedSlot, lockStack);
                        } else if (currentStack.getItem() == lockStack.getItem()
                                && currentStack.getCount() < currentStack.getMaxCount()) {
                            currentStack.increment(1);
                        } else {
                            if (!player.getInventory().insertStack(lockStack)) {
                                player.dropItem(lockStack, false);
                            }
                        }
                    }

                    world.playSound(
                            null, pos,
                            LockpickingSounds.LOCK_ON,
                            SoundCategory.BLOCKS,
                            0.7f, 0.8f
                    );
                }

                return ActionResult.SUCCESS;
            } else {
                if (!world.isClient) {
                    player.sendMessage(net.minecraft.text.Text.literal("You don't own this lock"), true);
                }
                return ActionResult.FAIL;
            }
        }

        if (!customChest.isLocked()) {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            }

            ServerWorld serverWorld = (ServerWorld) world;

            if (!LockEntity.canPlaceLock(serverWorld, pos)) {
                player.sendMessage(Text.literal("This chest already has a lock"), true);
                return ActionResult.FAIL;
            }

            customChest.setLockType(lockType);
            customChest.setOwnerUuid(player.getUuid());
            customChest.setLocked(true);

            lockOtherHalf(world, pos, state, lockType, player.getUuid());

            world.playSound(
                    null, pos,
                    LockpickingSounds.LOCK_ON,
                    SoundCategory.BLOCKS,
                    0.7f, 1.0f
            );

            if (!player.isCreative()) {
                context.getStack().decrement(1);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }


    private ItemStack getLockItemStack(LockType lockType) {
        if (lockType == null) return null;

        Item lockItem = switch (lockType) {
            case CopperLock -> ModItems.COPPER_LOCK;
            case IronLock -> ModItems.IRON_LOCK;
            case GoldenLock -> ModItems.GOLDEN_LOCK;
            case DiamondLock -> ModItems.DIAMOND_LOCK;
            case NetheriteLock -> ModItems.NETHERITE_LOCK;
            case BastionLock -> ModItems.BASTION_LOCK;
        };

        return new ItemStack(lockItem, 1);
    }

    private BlockPos getOtherChestHalf(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof ChestBlock)) return null;

        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        if (chestType == ChestType.SINGLE) return null;

        Direction facing = state.get(ChestBlock.FACING);
        Direction otherDir = chestType == ChestType.LEFT
                ? facing.rotateYClockwise()
                : facing.rotateYCounterclockwise();

        return pos.offset(otherDir);
    }

    private void lockOtherHalf(World world, BlockPos pos, BlockState state, LockType lockType, java.util.UUID ownerUuid) {
        BlockPos otherPos = getOtherChestHalf(world, pos, state);
        if (otherPos == null) return;

        BlockEntity otherBE = world.getBlockEntity(otherPos);
        if (otherBE instanceof CustomChestData otherChest) {
            otherChest.setLockType(lockType);
            otherChest.setOwnerUuid(ownerUuid);
            otherChest.setLocked(true);
        }
    }

    private void unlockOtherHalf(World world, BlockPos pos, BlockState state) {
        BlockPos otherPos = getOtherChestHalf(world, pos, state);
        if (otherPos == null) return;

        BlockEntity otherBE = world.getBlockEntity(otherPos);
        if (otherBE instanceof CustomChestData otherChest) {
            otherChest.setLocked(false);
            otherChest.setLockType(null);
            otherChest.setOwnerUuid(null);
        }
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        return false;
    }
}
