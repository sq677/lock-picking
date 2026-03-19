package net.skittle.lockpicking;

import net.minecraft.util.math.BlockPos;
import java.util.HashSet;
import java.util.Set;

public class UnlockedChests {
    private static final Set<BlockPos> unlocked = new HashSet<>();

    public static void unlock(BlockPos pos) {
        unlocked.add(pos.toImmutable());
    }

    public static boolean isUnlocked(BlockPos pos) {
        return unlocked.contains(pos);
    }

    public static void remove(BlockPos pos) {
        unlocked.remove(pos);
    }
}