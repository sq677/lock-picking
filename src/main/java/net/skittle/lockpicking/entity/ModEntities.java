package net.skittle.lockpicking.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.skittle.lockpicking.entity.locks.bastion_lock.BastionLockEntity;
import net.skittle.lockpicking.entity.locks.copper_lock.CopperLockEntity;
import net.skittle.lockpicking.entity.locks.diamond_lock.DiamondLockEntity;
import net.skittle.lockpicking.entity.locks.golden_lock.GoldenLockEntity;
import net.skittle.lockpicking.entity.locks.iron_lock.IronLockEntity;
import net.skittle.lockpicking.entity.locks.netherite_lock.NetheriteLockEntity;

public class ModEntities {
    public static final EntityType<IronLockEntity> IRON_LOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("lockpicking", "iron_lock"),
            EntityType.Builder.<IronLockEntity>create(IronLockEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).makeFireImmune().build()
    );

    public static final EntityType<CopperLockEntity> COPPER_LOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("lockpicking", "copper_lock"),
            EntityType.Builder.<CopperLockEntity>create(CopperLockEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).makeFireImmune().build()
    );
    public static final EntityType<GoldenLockEntity> GOLDEN_LOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("lockpicking", "golden_lock"),
            EntityType.Builder.<GoldenLockEntity>create(GoldenLockEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).makeFireImmune().build()
    );
    public static final EntityType<DiamondLockEntity> DIAMOND_LOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("lockpicking", "diamond_lock"),
            EntityType.Builder.<DiamondLockEntity>create(DiamondLockEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).makeFireImmune().build()
    );
    public static final EntityType<NetheriteLockEntity> NETHERITE_LOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("lockpicking", "netherite_lock"),
            EntityType.Builder.<NetheriteLockEntity>create(NetheriteLockEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).makeFireImmune().build()
    );
    public static final EntityType<BastionLockEntity> BASTION_LOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("lockpicking", "bastion_lock"),
            EntityType.Builder.<BastionLockEntity>create(BastionLockEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).makeFireImmune().build()
    );

    public static void register() {
        System.out.println("[Lockpicking] Entities registered");
    }
}