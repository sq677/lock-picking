package net.sq.lockpicking.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.sq.lockpicking.custom_data.CustomChestData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestBlockMixin {

    @Inject(
            method = "getPlacementState",
            at = @At("RETURN"),
            cancellable = true
    )
    private void preventPlacementNearLockedChest(
            ItemPlacementContext ctx,
            CallbackInfoReturnable<BlockState> cir
    ) {
        BlockState result = cir.getReturnValue();
        if (result == null) return;

        ChestType chestType = result.get(ChestBlock.CHEST_TYPE);
        if (chestType != ChestType.SINGLE) {
            World world = ctx.getWorld();
            BlockPos pos = ctx.getBlockPos();
            Direction facing = result.get(ChestBlock.FACING);

            Direction neighborDir = chestType == ChestType.LEFT
                    ? facing.rotateYClockwise()
                    : facing.rotateYCounterclockwise();
            BlockPos neighborPos = pos.offset(neighborDir);

            BlockEntity neighborBE = world.getBlockEntity(neighborPos);
            if (neighborBE instanceof CustomChestData neighborData) {
                if (neighborData.isLocked()) {
                    cir.setReturnValue(result.with(ChestBlock.CHEST_TYPE, ChestType.SINGLE));
                }
            }
        }
    }

    @Inject(
            method = "getStateForNeighborUpdate",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventMixedChest(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos,
            CallbackInfoReturnable<BlockState> cir
    ) {
        if (world.isClient()) return;

        if (direction.getAxis() == Direction.Axis.Y) return;

        if (!(neighborState.getBlock() instanceof ChestBlock))
            return;

        ChestType currentType = state.get(ChestBlock.CHEST_TYPE);
        if (currentType != ChestType.SINGLE) return;

        ChestType neighborType = neighborState.get(ChestBlock.CHEST_TYPE);
        if (neighborType != ChestType.SINGLE) return;

        BlockEntity selfBE = world.getBlockEntity(pos);
        BlockEntity otherBE = world.getBlockEntity(neighborPos);

        if (!(selfBE instanceof CustomChestData self) ||
                !(otherBE instanceof CustomChestData other))
            return;

        if (self.isLocked() != other.isLocked()) {
            cir.setReturnValue(state);
        }
    }
}
