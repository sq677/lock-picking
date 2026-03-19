package net.skittle.lockpicking;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementGenerator extends FabricAdvancementProvider {
    public ModAdvancementGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {

        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                        ModItems.COPPER_LOCK_PICK,
                        Text.literal("Lock picking"),
                        Text.literal("Craft or obtain your first lock pick"),
                        Identifier.of("minecraft", "textures/block/chiseled_stone_bricks.png"), // Background texture
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("obtained_lockpick", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().tag(ModTagGenerator.LOCKPICKS).build()
                ))
                .build(consumer, "lockpicking:main/root");

        var sharpnessEnchantment = registryLookup.getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                .getOrThrow(net.minecraft.enchantment.Enchantments.SHARPNESS);

        AdvancementEntry hiddenSharpness = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        ModItems.COPPER_LOCK_PICK,
                        Text.literal("The Sharpest Tool in the Shed"),
                        Text.literal("Enchant a lockpick with Sharpness V."),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        true
                )
                .criterion("has_sharp_pick", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create()
                                .tag(ModTagGenerator.LOCKPICKS)
                                .subPredicate(net.minecraft.predicate.item.ItemSubPredicateTypes.ENCHANTMENTS,
                                        net.minecraft.predicate.item.EnchantmentsPredicate.enchantments(java.util.List.of(
                                                new EnchantmentPredicate(sharpnessEnchantment, NumberRange.IntRange.exactly(5))
                                        )))
                                .build()
                ))
                .build(consumer, "lockpicking:hidden/sharp_lockpick");

        AdvancementEntry netheriteLockpick = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        ModItems.NETHERITE_LOCK_PICK,
                        Text.literal("Master Locksmith"),
                        Text.literal("Upgrade a diamond lockpick to netherite using a smithing table"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .criterion("has_netherite_lockpick", InventoryChangedCriterion.Conditions.items(
                        ModItems.NETHERITE_LOCK_PICK
                ))
                .build(consumer, "lockpicking:main/netherite_lockpick");

        AdvancementEntry riskyUnlock = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        ModItems.IRON_LOCK_PICK,
                        Text.literal("Sleight of hand, nothing in my pockets!"),
                        Text.literal("Unlock a chest while a guard is nearby"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false
                )
                .criterion("risky_unlock", new net.minecraft.advancement.AdvancementCriterion<>(Lockpicking.RISKY_UNLOCK, RiskyUnlockCriterion.Conditions.create()))
                .build(consumer, "lockpicking:main/risky_unlock");
    }
}