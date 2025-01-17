package me.katanya04.minespawnersforge.datagen;

import me.katanya04.minespawnersforge.config.Config;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import me.katanya04.minespawnersforge.loot.LootPoolWithConfigChanceModifier;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

/**
 * Data generation class, specifies the spawner loot table
 */
public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, Mine_spawners_forge.MOD_ID);
    }

    @Override
    protected void start() {
        ItemPredicate.Builder pickaxeWithSilktouch = ItemPredicate.Builder.item();
        pickaxeWithSilktouch.of(ItemTags.PICKAXES);
        pickaxeWithSilktouch.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)));

        CompoundTag removeDelayAndCoords = new CompoundTag();
        CompoundTag insideBlockTag = new CompoundTag();
        insideBlockTag.put("Delay", ShortTag.valueOf((short) -1));
        insideBlockTag.put("x", IntTag.valueOf(0));
        insideBlockTag.put("y", IntTag.valueOf(0));
        insideBlockTag.put("z", IntTag.valueOf(0));
        removeDelayAndCoords.put("BlockEntityTag", insideBlockTag);

        add("drop_spawner", new LootPoolWithConfigChanceModifier(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(
                    LootItem.lootTableItem(Items.SPAWNER)
                    .apply(
                            CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("{}", "BlockEntityTag", CopyNbtFunction.MergeStrategy.MERGE)
                    ).apply(SetNbtFunction.setTag(removeDelayAndCoords))
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPAWNER))
                    .when(MatchTool.toolMatches(pickaxeWithSilktouch))
                ).name("drop_spawner").build(),
                Config.DROP_CHANCE)
        );


    }
}
