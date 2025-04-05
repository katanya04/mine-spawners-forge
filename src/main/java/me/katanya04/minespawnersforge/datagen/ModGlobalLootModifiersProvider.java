package me.katanya04.minespawnersforge.datagen;

import me.katanya04.minespawnersforge.config.Config;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import me.katanya04.minespawnersforge.loot.functions.CopyDataComponentFunction;
import me.katanya04.minespawnersforge.loot.LootTableModifier;
import me.katanya04.minespawnersforge.loot.functions.SetDataComponentFunction;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Data generation class, specifies the spawner loot table
 */
public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Mine_spawners_forge.MOD_ID, registries);
    }

    @Override
    protected void start(HolderLookup.@NotNull Provider registries) {
        var items = registries.lookupOrThrow(Registries.ITEM);
        //var items = registries.lookupOrThrow(BuiltInRegistries.ITEM.key());
        var enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);

        ItemPredicate.Builder pickaxeWithSilktouch = ItemPredicate.Builder.item();
        pickaxeWithSilktouch.withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.ENCHANTMENTS,
                        EnchantmentsPredicate.enchantments(Collections.singletonList(new EnchantmentPredicate(
                                enchantments.getOrThrow(Enchantments.SILK_TOUCH),
                        MinMaxBounds.Ints.atLeast(1))))).build()
        );

        CompoundTag removeDelayAndCoords = new CompoundTag();
        removeDelayAndCoords.put("Delay", ShortTag.valueOf((short) -1));
        removeDelayAndCoords.put("x", IntTag.valueOf(0));
        removeDelayAndCoords.put("y", IntTag.valueOf(0));
        removeDelayAndCoords.put("z", IntTag.valueOf(0));

        add("drop_spawner", new LootTableModifier(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(
                    LootItem.lootTableItem(Items.SPAWNER)
                    .apply(
                            CopyDataComponentFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("{}", "{}", CopyDataComponentFunction.MergeStrategy.REPLACE, DataComponents.BLOCK_ENTITY_DATA))
                    .apply(SetDataComponentFunction.setDataComponent(removeDelayAndCoords, DataComponents.BLOCK_ENTITY_DATA))
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPAWNER))
                    .when(MatchTool.toolMatches(pickaxeWithSilktouch))
                    .when(LootItemRandomChanceCondition.randomChance(Config.DROP_CHANCE))
                ).name("drop_spawner").build())
        );

        add("drop_trial_spawner", new LootTableModifier(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(
                        LootItem.lootTableItem(Items.TRIAL_SPAWNER)
                                .apply(
                                        CopyDataComponentFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                .copy("{}", "{}", CopyDataComponentFunction.MergeStrategy.REPLACE, DataComponents.BLOCK_ENTITY_DATA))
                                .apply(SetDataComponentFunction.setDataComponent(removeDelayAndCoords, DataComponents.BLOCK_ENTITY_DATA))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TRIAL_SPAWNER))
                                .when(MatchTool.toolMatches(pickaxeWithSilktouch))
                                .when(LootItemRandomChanceCondition.randomChance(Config.DROP_CHANCE))
                ).name("drop_spawner").build())
        );
    }
}