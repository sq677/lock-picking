package net.sq.lockpicking.UI;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.sq.lockpicking.LockpickingTextures;
import org.lwjgl.glfw.GLFW;


import java.awt.*;

public class Lockpick {
    private LockpickType Type;
    private Identifier Texture;
    public double distanceToLockpoint;
    private boolean spotLocked = false;

    public Lockpick()
    {
        Type = LockpickType.Far;
    }

    public void render(DrawContext context, int mouseX, int mouseY, boolean inPickArea)
    {
        if (!inPickArea) {
            spotLocked = false;
        }

        boolean rightMouseDown =
                GLFW.glfwGetMouseButton(
                        MinecraftClient.getInstance().getWindow().getHandle(),
                        GLFW.GLFW_MOUSE_BUTTON_RIGHT
                ) == GLFW.GLFW_PRESS;
        if (!rightMouseDown || !inPickArea) {
            spotLocked = false;
        }
        if (spotLocked) {
            Type = LockpickType.OnSpot;
            Texture = LockpickingTextures.LOCKPICK_ON_SPOT;
        }
        else {
            if (distanceToLockpoint < 5) {
                Type = LockpickType.OnSpot;
                Texture = LockpickingTextures.LOCKPICK_ON_SPOT;

                if (rightMouseDown) {
                    spotLocked = true;
                }

            } else if (distanceToLockpoint < 25) {
                Type = LockpickType.Near;
                Texture = LockpickingTextures.LOCKPICK_NEAR;
            } else {
                Type = LockpickType.Far;
                Texture = LockpickingTextures.LOCKPICK_FAR;
            }
        }

        int size = 64;
        int half = size / 2;

        context.drawTexture(
                Texture,
                mouseX - half,
                mouseY - half,
                0, 0,
                size, size,
                size, size
        );
    }

    public void resetSpotLock() {
        spotLocked = false;
    }
    public boolean isSpotLocked() {
        return spotLocked;
    }
}
