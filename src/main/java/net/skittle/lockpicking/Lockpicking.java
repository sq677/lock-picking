package net.skittle.lockpicking;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.chunk.WorldChunk;
import net.skittle.lockpicking.UI.LockType;
import net.skittle.lockpicking.entity.ModEntities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.skittle.lockpicking.custom_data.CustomChestData;
import net.skittle.lockpicking.entity.locks.LockEntityManager;


public class Lockpicking implements ModInitializer {
	public static final String MOD_ID = "lockpicking";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private int tickCounter = 0;
	public static final net.minecraft.util.Identifier UNLOCK_PACKET_ID = net.minecraft.util.Identifier.of(MOD_ID, "unlock_chest");
	public static final RiskyUnlockCriterion RISKY_UNLOCK = net.minecraft.advancement.criterion.Criteria.register("lockpicking:risky_unlock", new RiskyUnlockCriterion());

	private static final java.util.Map<BlockPos, Long> recentlyUnlockedServer = new java.util.HashMap<>();
	private static final long UNLOCK_COOLDOWN_MS = 1000;

	public static void markRecentlyUnlockedServer(BlockPos pos) {
		recentlyUnlockedServer.put(pos.toImmutable(), System.currentTimeMillis());
	}

	public static boolean isRecentlyUnlockedServer(BlockPos pos) {
		Long time = recentlyUnlockedServer.get(pos);
		if (time == null) return false;
		if (System.currentTimeMillis() - time > UNLOCK_COOLDOWN_MS) {
			recentlyUnlockedServer.remove(pos);
			return false;
		}
		return true;
	}

	private static void unlockOtherHalf(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof ChestBlock)) return;

		ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
		if (chestType == ChestType.SINGLE) return;

		Direction facing = state.get(ChestBlock.FACING);
		Direction otherDir = chestType == ChestType.LEFT
				? facing.rotateYClockwise()
				: facing.rotateYCounterclockwise();
		BlockPos otherPos = pos.offset(otherDir);

		BlockEntity otherBE = world.getBlockEntity(otherPos);
		if (otherBE instanceof CustomChestData otherChest) {
			otherChest.setLocked(false);
			otherChest.setLockType(null);
			otherChest.setOwnerUuid(null);
		}
	}

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();
		ModLootTableModifiers.modifyLootTables();

		LOGGER.info("[Lockpicking] Guard Villagers mod loaded: {}", FabricLoader.getInstance().isModLoaded("guardvillagers"));

		PayloadTypeRegistry.playC2S().register(UnlockChestPayload.ID, UnlockChestPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DamageLockpickPayload.ID, DamageLockpickPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(StartLockpickingPayload.ID, StartLockpickingPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(DamageLockpickPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				net.minecraft.server.network.ServerPlayerEntity player = context.player();
				net.minecraft.item.ItemStack stack = player.getMainHandStack();

				if (stack.isOf(ModItems.COPPER_LOCK_PICK) || stack.isOf(ModItems.IRON_LOCK_PICK) || stack.isOf(ModItems.GOLDEN_LOCK_PICK) || stack.isOf(ModItems.DIAMOND_LOCK_PICK) || stack.isOf(ModItems.NETHERITE_LOCK_PICK)) {
					stack.setDamage(stack.getDamage() + 1);

					if (stack.getDamage() >= stack.getMaxDamage()) {
						stack.decrement(1);
					}

					player.currentScreenHandler.sendContentUpdates();
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(UnlockChestPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity player = context.player();
				BlockPos chestPos = payload.pos();

				markRecentlyUnlockedServer(chestPos);

				net.minecraft.block.entity.BlockEntity be = player.getWorld().getBlockEntity(chestPos);
				if (be instanceof CustomChestData customChest) {
					LockType lockType = customChest.getLockType();
					if (customChest.isLocked() && lockType != null) {
						// BastionLock requires key consumption
						if (lockType == LockType.BastionLock) {
							net.minecraft.item.ItemStack mainHand = player.getMainHandStack();
							net.minecraft.item.ItemStack offHand = player.getOffHandStack();

							if (mainHand.isOf(ModItems.BASTION_LOCK_KEY)) {
								mainHand.decrement(1);
							} else if (offHand.isOf(ModItems.BASTION_LOCK_KEY)) {
								offHand.decrement(1);
							}
							player.currentScreenHandler.sendContentUpdates();
						}

						Vec3d chestVec = Vec3d.ofCenter(chestPos);
						Box searchBox = new Box(
							chestVec.x - 1, chestVec.y - 1, chestVec.z - 1,
							chestVec.x + 1, chestVec.y + 1, chestVec.z + 1
						);

						var lockEntities = player.getWorld().getEntitiesByClass(
							net.skittle.lockpicking.entity.locks.LockEntity.class,
							searchBox,
							entity -> {
								BlockPos entityChestPos = entity.getChestPos();
								return entityChestPos != null && entityChestPos.equals(chestPos);
							}
						);

						for (var lockEntity : lockEntities) {
							lockEntity.startOpening();
						}
					}

					if (FabricLoader.getInstance().isModLoaded("guardvillagers")) {
						Vec3d chestVec = Vec3d.ofCenter(chestPos);
						double minRange = 9.0;
						double maxRange = 15.0;
						Box searchBox = new Box(
							chestVec.x - maxRange, chestVec.y - maxRange, chestVec.z - maxRange,
							chestVec.x + maxRange, chestVec.y + maxRange, chestVec.z + maxRange
						);

						try {
							Class<?> guardClass = Class.forName("dev.sterner.guardvillagers.common.entity.GuardEntity");
							var guards = player.getWorld().getEntitiesByClass(LivingEntity.class, searchBox, entity -> {
								return guardClass.isInstance(entity);
							});

							boolean hasGuardInRange = guards.stream().anyMatch(guard -> {
								double distance = guard.getPos().distanceTo(chestVec);
								return distance >= minRange && distance <= maxRange;
							});

							if (hasGuardInRange) {
								RISKY_UNLOCK.trigger(player);
							}
						} catch (ClassNotFoundException e) {
							LOGGER.info("Guard Villagers not available");
						}
					}

					player.getWorld().emitGameEvent(player, GameEvent.BLOCK_CHANGE, chestPos);

					ChestAlarm.trigger(player.getWorld(), chestPos);
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(StartLockpickingPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity player = context.player();
				BlockPos chestPos = payload.pos();
				LOGGER.info("[Lockpicking] StartLockpickingPayload received at {}", chestPos);

				Vec3d chestVec = Vec3d.ofCenter(chestPos);
				double range = 8.0;
				Box searchBox = new Box(
					chestVec.x - range, chestVec.y - range, chestVec.z - range,
					chestVec.x + range, chestVec.y + range, chestVec.z + range
				);

				var piglins = player.getWorld().getEntitiesByClass(
					net.minecraft.entity.mob.PiglinEntity.class,
					searchBox,
					piglin -> true
				);

				LOGGER.info("[Lockpicking] Found {} piglins nearby", piglins.size());

				piglins.forEach(piglin -> {

					piglin.setTarget(player);
					if (piglin instanceof net.minecraft.entity.mob.Angerable angerable) {
						angerable.setAngryAt(player.getUuid());
						angerable.setAngerTime(400);
					}
				});

				boolean guardVillagersLoaded = FabricLoader.getInstance().isModLoaded("guardvillagers");
				LOGGER.info("[Lockpicking] Checking Guard Villagers: {}", guardVillagersLoaded);
				if (guardVillagersLoaded) {
					try {
						Class<?> guardClass = Class.forName("dev.sterner.guardvillagers.common.entity.GuardEntity");

						var guards = player.getWorld().getEntitiesByClass(
							net.minecraft.entity.mob.MobEntity.class,
							searchBox,
							entity -> guardClass.isInstance(entity)
						);

						LOGGER.info("[Lockpicking] Found {} guards nearby", guards.size());

						guards.forEach(guard -> {
							guard.setTarget(player);

							if (guard instanceof net.minecraft.entity.mob.Angerable angerable) {
								angerable.setAngryAt(player.getUuid());
								angerable.setAngerTime(400);
							}
						});
					} catch (ClassNotFoundException e) {
						LOGGER.warn("[Lockpicking] Guard Villagers class not found: {}", e.getMessage());
					}
				}
			});
		});

		ServerWorldEvents.UNLOAD.register((server, world) -> {
			LockEntityManager.clearProcessedPositions();
			ChestAlarm.clear();
		});

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			LockEntityManager.processQueue();
			ChestAlarm.tick(world);

			tickCounter++;
			if (tickCounter < 40) {
				return;
			}
			tickCounter = 0;

			for (ServerPlayerEntity player : world.getPlayers()) {
				ChunkPos playerChunk = player.getChunkPos();

				for (int cx = -1; cx <= 1; cx++) {
					for (int cz = -1; cz <= 1; cz++) {
						ChunkPos checkChunk = new ChunkPos(playerChunk.x + cx, playerChunk.z + cz);

						if (world.isChunkLoaded(checkChunk.x, checkChunk.z)) {
							WorldChunk chunk = world.getChunk(checkChunk.x, checkChunk.z);

							for (BlockPos pos : chunk.getBlockEntityPositions()) {
								BlockEntity blockEntity = chunk.getBlockEntity(pos);

								if (blockEntity instanceof ChestBlockEntity && blockEntity instanceof CustomChestData customChest) {
									if (customChest.isLocked()) {
										LockEntityManager.queueSpawn(world, pos, customChest.getLockType());
									}
								}
							}
						}
					}
				}
			}
		});
	}
}
