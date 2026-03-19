package net.skittle.lockpicking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.skittle.lockpicking.UI.LockType;
import net.skittle.lockpicking.UI.LockpickingScreen;
import net.skittle.lockpicking.custom_data.CustomChestData;
import net.skittle.lockpicking.entity.locks.bastion_lock.BastionLockModel;
import net.skittle.lockpicking.entity.locks.bastion_lock.BastionLockRenderer;
import net.skittle.lockpicking.entity.locks.copper_lock.CopperLockModel;
import net.skittle.lockpicking.entity.locks.copper_lock.CopperLockRenderer;
import net.skittle.lockpicking.entity.locks.diamond_lock.DiamonLockRenderer;
import net.skittle.lockpicking.entity.locks.diamond_lock.DiamondLockModel;
import net.skittle.lockpicking.entity.locks.golden_lock.GoldenLockModel;
import net.skittle.lockpicking.entity.locks.golden_lock.GoldenLockRenderer;
import net.skittle.lockpicking.entity.locks.iron_lock.IronLockModel;
import net.skittle.lockpicking.entity.locks.iron_lock.IronLockRenderer;
import net.skittle.lockpicking.entity.ModEntities;
import net.skittle.lockpicking.entity.locks.netherite_lock.NetheriteLockModel;
import net.skittle.lockpicking.entity.locks.netherite_lock.NetheriteLockRenderer;

public class LockpickingClient implements ClientModInitializer {
    public static boolean suppressNextLockpicking = false;

    private static final java.util.Map<BlockPos, Long> recentlyUnlocked = new java.util.HashMap<>();
    private static final long UNLOCK_COOLDOWN_MS = 1000;

    public static void markRecentlyUnlocked(BlockPos pos) {
        recentlyUnlocked.put(pos.toImmutable(), System.currentTimeMillis());
    }

    public static boolean isRecentlyUnlocked(BlockPos pos) {
        Long time = recentlyUnlocked.get(pos);
        if (time == null) return false;
        if (System.currentTimeMillis() - time > UNLOCK_COOLDOWN_MS) {
            recentlyUnlocked.remove(pos);
            return false;
        }
        return true;
    }

    @Override
    public void onInitializeClient() {
        LockpickingSounds.register();

        EntityModelLayerRegistry.registerModelLayer(IronLockRenderer.LAYER, IronLockModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.IRON_LOCK, IronLockRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CopperLockRenderer.LAYER, CopperLockModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.COPPER_LOCK, CopperLockRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(GoldenLockRenderer.LAYER, GoldenLockModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.GOLDEN_LOCK, GoldenLockRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(DiamonLockRenderer.LAYER, DiamondLockModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.DIAMOND_LOCK, DiamonLockRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(NetheriteLockRenderer.LAYER, NetheriteLockModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.NETHERITE_LOCK, NetheriteLockRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BastionLockRenderer.LAYER, BastionLockModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.BASTION_LOCK, BastionLockRenderer::new);
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();

            if (isRecentlyUnlocked(pos)) {
                return ActionResult.SUCCESS;
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof CustomChestData customChest && customChest.isLocked()) {
                LockType type = customChest.getLockType();

                if (UnlockedChests.isUnlocked(pos)) {
                    UnlockedChests.remove(pos);
                }

                if (suppressNextLockpicking) {
                    suppressNextLockpicking = false;
                    return ActionResult.PASS;
                }

                java.util.UUID ownerUuid = customChest.getOwnerUuid();
                if (ownerUuid != null && player.getUuid().equals(ownerUuid) && player.isSneaking()) {
                    return ActionResult.PASS;
                }

                if (player.getStackInHand(hand).isOf(ModItems.COPPER_LOCK_PICK) ||
                        player.getStackInHand(hand).isOf(ModItems.IRON_LOCK_PICK) ||
                        player.getStackInHand(hand).isOf(ModItems.GOLDEN_LOCK_PICK) ||
                        player.getStackInHand(hand).isOf(ModItems.DIAMOND_LOCK_PICK) ||
                        player.getStackInHand(hand).isOf(ModItems.NETHERITE_LOCK_PICK)
                ) {
                    if(type == LockType.BastionLock)
                    {
                        player.sendMessage(Text.literal("This lock requires special key."), true);
                        return ActionResult.PASS;
                    }
                    long seed = customChest.getLockSeed();

                    ClientPlayNetworking.send(new StartLockpickingPayload(pos.toImmutable()));

                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> {
                        client.setScreen(new LockpickingScreen(client.currentScreen, pos.toImmutable(), type, seed));
                    });
                    return ActionResult.SUCCESS;
                }
                else if(player.getStackInHand(hand).isOf(ModItems.BASTION_LOCK_KEY))
                {
                    if(type == LockType.BastionLock)
                    {
                        markRecentlyUnlocked(pos);
                        ClientPlayNetworking.send(new UnlockChestPayload(pos));
                        return ActionResult.SUCCESS;
                    }
                    else
                    {
                        return ActionResult.PASS;
                    }
                }
                else
                {
                    if(type == LockType.BastionLock)
                    {
                        return ActionResult.PASS;
                    }
                    player.sendMessage(Text.literal("This chest is locked. Try using a lockpicker"), true);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}