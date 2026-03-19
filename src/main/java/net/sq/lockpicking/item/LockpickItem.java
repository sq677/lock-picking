package net.sq.lockpicking.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class LockpickItem extends Item {
    public static final RegistryKey<Enchantment> PRECISION = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("lockpicking", "precision"));

    public LockpickItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        return enchantment.matchesKey(Enchantments.UNBREAKING) ||
               enchantment.matchesKey(Enchantments.MENDING) ||
               enchantment.matchesKey(PRECISION);
    }

    @Override
    public int getEnchantability() {
        return 15;
    }
}
