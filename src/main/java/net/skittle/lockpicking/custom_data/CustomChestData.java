package net.skittle.lockpicking.custom_data;

import net.skittle.lockpicking.UI.LockType;

import java.util.UUID;

public interface CustomChestData {
    boolean isLocked();
    void setLocked(boolean locked);
    LockType getLockType();
    void setLockType(LockType type);
    long getLockSeed();
    UUID getOwnerUuid();
    void setOwnerUuid(UUID uuid);
}