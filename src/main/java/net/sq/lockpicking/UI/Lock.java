package net.sq.lockpicking.UI;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.sq.lockpicking.LockpickingTextures;
import java.util.Random;
import net.minecraft.util.math.BlockPos; // Import BlockPos


public class Lock
{
    private Lock Instance;
    public LockType Type;
    private Identifier Texture;
    public LockPoint LockPoint;
    public Lockpick LockPick;
    public final float areaWidth;

    public Lock(LockType type, long seed)
    {
        Instance = this;
        Type = type;
        switch (Type)
        {
            case CopperLock -> Texture = LockpickingTextures.COPPERLOCK_TEX;
            case IronLock -> Texture = LockpickingTextures.IRONLOCK_TEX;
            case GoldenLock -> Texture = LockpickingTextures.GOLDENLOCK_TEX;
            case DiamondLock -> Texture = LockpickingTextures.DIAMONDLOCK_TEX;
            case NetheriteLock -> Texture = LockpickingTextures.NETHERITELOCK_TEX;
        }
        this.areaWidth = type.getAreaWidth();

        LockPoint = CreateLockPoint(seed);

    }

    public void render(DrawContext ctx,int x,int y)
    {
        ctx.drawTexture(
                Texture,
                x, y,
                0, 0,
                128, 128,
                128, 128
        );
        //LockPoint.render(ctx,x,y); debug
    }


    public LockPoint CreateLockPoint(long seed)
    {
        Random rng = new Random(seed);

        float randomRadius = 28f + (rng.nextFloat() * 22f);
        double angle = rng.nextDouble() * Math.PI * 2.0;

        float x = (float)(Math.cos(angle) * randomRadius);
        float y = (float)(Math.sin(angle) * randomRadius);

        return new LockPoint((int)(64 + x), (int)(64 + y));
    }
}
