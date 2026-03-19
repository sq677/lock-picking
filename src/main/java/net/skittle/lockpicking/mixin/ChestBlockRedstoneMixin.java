package net.skittle.lockpicking.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.skittle.lockpicking.ChestAlarm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class ChestBlockRedstoneMixin {

    @Inject(method = "emitsRedstonePower", at = @At("HEAD"), cancellable = true)
    private void lockpicking$emitsRedstonePower(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof ChestBlock) {
            if (ChestAlarm.isAlarming()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getWeakRedstonePower", at = @At("HEAD"), cancellable = true)
    private void lockpicking$getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (state.getBlock() instanceof ChestBlock) {
            int power = ChestAlarm.getPowerLevel(pos);
            if (power > 0) {
                cir.setReturnValue(power);
            }
        }
    }

    @Inject(method = "getStrongRedstonePower", at = @At("HEAD"), cancellable = true)
    private void lockpicking$getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (state.getBlock() instanceof ChestBlock) {
            int power = ChestAlarm.getPowerLevel(pos);
            if (power > 0) {
                cir.setReturnValue(power);
            }
        }
    }
}
