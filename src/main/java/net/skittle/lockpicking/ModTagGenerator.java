package net.skittle.lockpicking;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.skittle.lockpicking.ModItems;
import java.util.concurrent.CompletableFuture;

public class ModTagGenerator extends FabricTagProvider.ItemTagProvider {
    public static final TagKey<Item> LOCKPICKS = TagKey.of(RegistryKeys.ITEM, Identifier.of("lockpicking", "lockpicks"));

    public ModTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(LOCKPICKS)
                .add(ModItems.COPPER_LOCK_PICK)
                .add(ModItems.IRON_LOCK_PICK)
                .add(ModItems.GOLDEN_LOCK_PICK)
                .add(ModItems.DIAMOND_LOCK_PICK)
                .add(ModItems.NETHERITE_LOCK_PICK);

        getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED)
                .add(ModItems.GOLDEN_LOCK_PICK)
                .add(ModItems.GOLDEN_LOCK);
    }
}