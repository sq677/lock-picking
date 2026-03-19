package net.sq.lockpicking;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.sq.lockpicking.Lockpicking;
import net.sq.lockpicking.UI.LockType;
import net.sq.lockpicking.item.LockItem;
import net.sq.lockpicking.item.LockpickItem;

public class ModItems {
    public static final RegistryKey<ItemGroup> LOCKPICKING_GROUP = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(Lockpicking.MOD_ID, "lockpicking")
    );

    public static final ItemGroup LOCKPICKING_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.COPPER_LOCK_PICK))
            .displayName(Text.translatable("itemGroup.lockpicking"))
            .build();

    public static final Item COPPER_LOCK = registerItem("copper_lock_item",
            new LockItem(new Item.Settings().maxCount(16), LockType.CopperLock));
    public static final Item IRON_LOCK = registerItem("iron_lock_item",
            new LockItem(new Item.Settings().maxCount(16), LockType.IronLock));
    public static final Item GOLDEN_LOCK = registerItem("golden_lock_item",
            new LockItem(new Item.Settings().maxCount(16), LockType.GoldenLock));
    public static final Item DIAMOND_LOCK = registerItem("diamond_lock_item",
            new LockItem(new Item.Settings().maxCount(16), LockType.DiamondLock));
    public static final Item NETHERITE_LOCK = registerItem("netherite_lock_item",
            new LockItem(new Item.Settings().maxCount(16), LockType.NetheriteLock));
    public static final Item BASTION_LOCK = registerItem("bastion_lock",
            new LockItem(new Item.Settings().maxCount(16), LockType.BastionLock));
    public static final Item COPPER_LOCK_PICK = registerItem("copper_lock_pick",
            new LockpickItem(new Item.Settings().maxDamage(10).maxCount(1)));
    public static final Item IRON_LOCK_PICK = registerItem("iron_lock_pick",
            new LockpickItem(new Item.Settings().maxDamage(40).maxCount(1)));
    public static final Item GOLDEN_LOCK_PICK = registerItem("golden_lock_pick",
            new LockpickItem(new Item.Settings().maxDamage(25).maxCount(1)));
    public static final Item DIAMOND_LOCK_PICK = registerItem("diamond_lock_pick",
            new LockpickItem(new Item.Settings().maxDamage(100).maxCount(1)));
    public static final Item NETHERITE_LOCK_PICK = registerItem("netherite_lock_pick",
            new LockpickItem(new Item.Settings().maxDamage(300).maxCount(1)));

    public static final Item BASTION_LOCK_KEY = registerItem("bastion_lock_key",
            new LockpickItem(new Item.Settings().maxCount(1)));
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Lockpicking.MOD_ID, name), item);
    }

    public static void register() {
        Lockpicking.LOGGER.info("Registering Mod Items for " + Lockpicking.MOD_ID);

        Registry.register(Registries.ITEM_GROUP, LOCKPICKING_GROUP, LOCKPICKING_ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(LOCKPICKING_GROUP).register(entries -> {
            entries.add(COPPER_LOCK_PICK);
            entries.add(IRON_LOCK_PICK);
            entries.add(GOLDEN_LOCK_PICK);
            entries.add(DIAMOND_LOCK_PICK);
            entries.add(NETHERITE_LOCK_PICK);
            entries.add(BASTION_LOCK_KEY);

            entries.add(COPPER_LOCK);
            entries.add(IRON_LOCK);
            entries.add(GOLDEN_LOCK);
            entries.add(DIAMOND_LOCK);
            entries.add(NETHERITE_LOCK);
        });
    }
}