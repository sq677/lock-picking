package net.sq.lockpicking.UI;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.sq.lockpicking.*;
import net.sq.lockpicking.custom_data.CustomChestData;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;

import java.util.Optional;

public class LockpickingScreen extends Screen {
    private final Screen parent;
    private final BlockPos chestPos;

    private Lock lock;
    private Lockpick lockpick;
    private LockpickingArea lockpickingArea;
    private double lastAngle = Double.NaN;
    private double accumulatedAngle = 0.0;
    private boolean lockPicked = false;
    private float lockRotation = 0f;

    private boolean wasSpotLocked = false;
    private boolean inPickZone;

    private SoundInstance lockTurnSound;

    public LockpickingScreen(Screen parent, BlockPos chestPos, LockType type, long seed) {
        super(Text.literal("Lockpicking"));
        this.parent = parent;
        this.chestPos = chestPos;

        this.lock = new Lock(type, seed);
        this.lockpick = new Lockpick();
    }

    @Override
    protected void init() {
        super.init();
        playPickingStartSound();
    }

    private void playPickingStartSound() {
        if (this.client != null) {
            this.client.getSoundManager().play(
                PositionedSoundInstance.master(LockpickingSounds.PICKING_LOCK, 1.0f)
            );
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!isHoldingLockpick()) {
            stopLockSound();
            this.client.setScreen(null);
            return;
        }

        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        updateLockpickDistance(centerX, centerY, mouseX, mouseY);
        renderLock(context, centerX, centerY);
        initializeLockpickingAreaIfNeeded(centerX, centerY);
        //renderLockpickingArea(context, centerX, centerY); debug

        if (lockPicked) {
            onLockPicked();
            return;
        }

        inPickZone = lockpickingArea.contains(mouseX, mouseY);
        lockpick.render(context, mouseX, mouseY, inPickZone);

        handleLockpickFailure();
        handleLockRotation(centerX, centerY, mouseX, mouseY);
    }

    private boolean isHoldingLockpick() {
        if (this.client == null || this.client.player == null) {
            return false;
        }
        net.minecraft.item.ItemStack stack = this.client.player.getMainHandStack();
        return stack.isOf(ModItems.COPPER_LOCK_PICK) ||
               stack.isOf(ModItems.IRON_LOCK_PICK) ||
               stack.isOf(ModItems.GOLDEN_LOCK_PICK) ||
               stack.isOf(ModItems.DIAMOND_LOCK_PICK) ||
               stack.isOf(ModItems.NETHERITE_LOCK_PICK);
    }

    private void updateLockpickDistance(int centerX, int centerY, int mouseX, int mouseY) {
        int lockPointScreenX = centerX + lock.LockPoint.X - 64;
        int lockPointScreenY = centerY + lock.LockPoint.Y - 64;

        lockpick.distanceToLockpoint = calculateDistance(
                lockPointScreenX,
                lockPointScreenY,
                mouseX,
                mouseY
        );
    }

    private void renderLock(DrawContext context, int centerX, int centerY) {
        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().multiply(fromXYZRadians(0, 0, lockRotation));
        context.getMatrices().translate(-64, -64, 0);
        lock.render(context, 0, 0);
        context.getMatrices().pop();
    }

    private void initializeLockpickingAreaIfNeeded(int centerX, int centerY) {
        if (lockpickingArea == null) {
            double distToPoint = Math.sqrt(
                Math.pow(lock.LockPoint.X - 64, 2) +
                Math.pow(lock.LockPoint.Y - 64, 2)
            );

            float baseWidth = lock.Type.getAreaWidth();
            int precisionLevel = getPrecisionEnchantmentLevel();
            float bonusMultiplier = 1.0f + (precisionLevel * 0.1f);
            float finalWidth = baseWidth * bonusMultiplier;

            Lockpicking.LOGGER.info("Lockpicking area - Base width: " + baseWidth + ", Precision level: " + precisionLevel + ", Final width: " + finalWidth);

            lockpickingArea = new LockpickingArea(
                    centerX,
                    centerY,
                    distToPoint,
                    finalWidth
            );
        }
    }

    private int getPrecisionEnchantmentLevel() {
        if (this.client == null || this.client.player == null || this.client.world == null) {
            return 0;
        }

        ItemStack heldItem = this.client.player.getMainHandStack();
        if (heldItem.isEmpty()) {
            return 0;
        }

        RegistryKey<Enchantment> precisionKey = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("lockpicking", "precision"));

        try {
            var registry = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
            Optional<RegistryEntry.Reference<Enchantment>> enchantmentEntry = registry.getEntry(precisionKey);

            if (enchantmentEntry.isPresent()) {
                int level = EnchantmentHelper.getLevel(enchantmentEntry.get(), heldItem);
                Lockpicking.LOGGER.info("Precision enchantment found! Level: " + level);
                return level;
            } else {
                Lockpicking.LOGGER.info("Precision enchantment not found in registry");
            }
        } catch (Exception e) {
            Lockpicking.LOGGER.error("Error getting precision enchantment: " + e.getMessage());
        }

        return 0;
    }

    private void renderLockpickingArea(DrawContext context, int centerX, int centerY) {
        drawCircle(context, centerX, centerY, Math.sqrt(lockpickingArea.innerRadiusSq), 0x3300FF00);
        drawCircle(context, centerX, centerY, Math.sqrt(lockpickingArea.outerRadiusSq), 0x3300FF00);
    }

    private void handleLockpickFailure() {
        if (wasSpotLocked && !lockpick.isSpotLocked() && !lockPicked) {
            onFail();
        }
        wasSpotLocked = lockpick.isSpotLocked();
    }

    private void handleLockRotation(int centerX, int centerY, int mouseX, int mouseY) {
        if (!lockpick.isSpotLocked() || !inPickZone) {
            resetLockRotation();
        } else {
            updateLockRotation(centerX, centerY, mouseX, mouseY);
        }
    }

    private void resetLockRotation() {
        stopLockSound();
        accumulatedAngle = 0.0;
        lastAngle = Double.NaN;
        lockRotation = 0f;
    }

    private void updateLockRotation(int centerX, int centerY, int mouseX, int mouseY) {
        startLockSound();

        double angle = getMouseAngle(centerX, centerY, mouseX, mouseY);

        if (!Double.isNaN(lastAngle)) {
            double delta = normalizeAngleDelta(angle - lastAngle);
            accumulatedAngle += delta;
            lockRotation = (float) accumulatedAngle;

            if (Math.abs(accumulatedAngle) >= Math.PI * 1.5) {
                lockPicked = true;
            }
        }
        lastAngle = angle;
    }

    private double normalizeAngleDelta(double delta) {
        if (delta > Math.PI) delta -= Math.PI * 2.0;
        if (delta < -Math.PI) delta += Math.PI * 2.0;
        return delta;
    }

    private void onFail() {
        ClientPlayNetworking.send(new DamageLockpickPayload());

        lockpick.resetSpotLock();
        accumulatedAngle = 0.0;
        lastAngle = Double.NaN;
        lockRotation = 0f;
    }

    private void onLockPicked() {
        if (this.chestPos != null) {
            UnlockedChests.unlock(this.chestPos);
            ClientPlayNetworking.send(new UnlockChestPayload(this.chestPos));
        }

        stopLockSound();
        LockpickingClient.suppressNextLockpicking = true;
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    private void startLockSound() {
        // Only start if not already playing
        if (this.lockTurnSound == null && this.client != null) {
            this.lockTurnSound = new PositionedSoundInstance(
                LockpickingSounds.LOCK_TURN.getId(),
                net.minecraft.sound.SoundCategory.BLOCKS,
                0.7f,
                1.0f,
                net.minecraft.util.math.random.Random.create(),
                true,
                0,
                SoundInstance.AttenuationType.NONE,
                0.0, 0.0, 0.0,
                true
            );
            this.client.getSoundManager().play(this.lockTurnSound);
        }
    }

    private void stopLockSound() {
        if (this.lockTurnSound != null && this.client != null) {
            this.client.getSoundManager().stop(this.lockTurnSound);
            this.lockTurnSound = null;
        }
    }

    @Override
    public void removed() {
        super.removed();
        stopLockSound();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            stopLockSound();
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void drawCircle(DrawContext ctx, int centerX, int centerY, double radius, int color) {
        int segments = 360;
        for (int i = 0; i < segments; i++) {
            double angle = (Math.PI * 2.0) * i / segments;
            int x = (int) (centerX + Math.cos(angle) * radius);
            int y = (int) (centerY + Math.sin(angle) * radius);
            ctx.fill(x, y, x + 1, y + 1, color);
        }
    }

    private double getMouseAngle(int centerX, int centerY, int mouseX, int mouseY) {
        return Math.atan2(mouseY - centerY, mouseX - centerX);
    }

    public static Quaternionf fromXYZRadians(float x, float y, float z) {
        return new Quaternionf().rotationXYZ(x, y, z);
    }
}
