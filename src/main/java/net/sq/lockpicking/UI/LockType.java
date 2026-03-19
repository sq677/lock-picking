package net.sq.lockpicking.UI;

public enum LockType
{
    CopperLock,
    IronLock,
    GoldenLock,
    DiamondLock,
    NetheriteLock,
    BastionLock;

    public float getAreaWidth() {
        return switch (this) {
            case CopperLock -> 18f;
            case GoldenLock -> 12f;
            case IronLock -> 9f;
            case DiamondLock -> 7f;
            case NetheriteLock -> 5f;
            case BastionLock -> 0f;
        };
    }
}

