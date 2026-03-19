package net.sq.lockpicking;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChestAlarm {
    private static final Map<BlockPos, AlarmData> alarmingChests = new HashMap<>();
    private static final int ALARM_DURATION = 1;
    private static final int POWER_LEVEL = 5;

    public static void trigger(World world, BlockPos pos) {
        if (world.isClient) return;

        alarmingChests.put(pos.toImmutable(), new AlarmData(POWER_LEVEL, ALARM_DURATION));

        BlockState state = world.getBlockState(pos);
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    public static int getPowerLevel(BlockPos pos) {
        AlarmData data = alarmingChests.get(pos);
        return data != null ? data.powerLevel : 0;
    }


    public static boolean isAlarming() {
        return !alarmingChests.isEmpty();
    }

    public static void tick(ServerWorld world) {
        if (alarmingChests.isEmpty()) return;

        Iterator<Map.Entry<BlockPos, AlarmData>> iterator = alarmingChests.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, AlarmData> entry = iterator.next();
            BlockPos pos = entry.getKey();
            AlarmData data = entry.getValue();

            data.ticksRemaining--;

            if (data.ticksRemaining <= 0) {
                iterator.remove();
                if (world.isChunkLoaded(pos)) {
                    BlockState state = world.getBlockState(pos);
                    world.updateNeighborsAlways(pos, state.getBlock());
                }
            }
        }
    }

    public static void clear() {
        alarmingChests.clear();
    }

    private static class AlarmData {
        int powerLevel;
        int ticksRemaining;

        AlarmData(int powerLevel, int ticksRemaining) {
            this.powerLevel = powerLevel;
            this.ticksRemaining = ticksRemaining;
        }
    }
}
