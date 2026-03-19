package net.sq.lockpicking.custom_data;

import net.sq.lockpicking.UI.LockType;

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