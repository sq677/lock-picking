package net.skittle.lockpicking.UI;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.Debug;
import net.skittle.lockpicking.LockpickingTextures;

public class LockPoint {
    public int X;
    public int Y;
    private Identifier Texture;
    public LockPoint(int x,int y)
    {
        X = x;
        Y = y;
        Texture = LockpickingTextures.LOCKPOINT;
    }

    public void render(DrawContext ctx, int lockX, int lockY) {
        ctx.fill(
                lockX + X - 2,
                lockY + Y - 2,
                lockX + X + 2,
                lockY + Y + 2,
                0xFF0000FF
        );
    }
}
