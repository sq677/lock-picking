package net.sq.lockpicking;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {
    private static final Identifier PIGLIN_BRUTE_ID = Identifier.of("minecraft","entities/piglin_brute");

    public static void modifyLootTables()
    {
        LootTableEvents.MODIFY.register(((key, tableBuilder, source, registries) ->{
            if(PIGLIN_BRUTE_ID.equals(key.getValue()))
            {
                LootPool.Builder poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.75f))
                        .with(ItemEntry.builder(ModItems.BASTION_LOCK_KEY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f,1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
        }));
    }
}
