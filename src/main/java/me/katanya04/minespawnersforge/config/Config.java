package me.katanya04.minespawnersforge.config;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Config parameters of the mod
 */
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ConfigNumericField<Double> DROP_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_PICKAXES;

    static {
        BUILDER.push("Mine Spawners Config");
        DROP_CHANCE = new ConfigNumericField<>(BUILDER
                .comment("Chance of dropping the spawner when mined. From 0 (never) to 100 (always)")
                .defineInRange("dropChance", 1.0, 0.0, 1.0));
        BLACKLISTED_PICKAXES = BUILDER.comment("Not allowed pickaxes")
                .defineListAllowEmpty("blacklistedPickaxes", List.of(), p -> isPickaxe((String) p));
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    static Set<Item> getAllPickaxes() {
        return ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> isPickaxe(item.getDefaultInstance())).collect(Collectors.toSet());
    }

    private static boolean isPickaxe(String name) {
        Optional<Holder.Reference<Item>> pickaxe = BuiltInRegistries.ITEM.get(ResourceLocation.parse(name));
        return pickaxe.filter(itemReference -> isPickaxe(itemReference.get().getDefaultInstance())).isPresent();
    }

    private static boolean isPickaxe(ItemStack stack) {
        return  stack.is(ItemTags.PICKAXES) ||
                stack.isCorrectToolForDrops(Blocks.SPAWNER.defaultBlockState()) ||
                (stack.get(DataComponents.TOOL) != null && stack.get(DataComponents.TOOL).rules().stream()
                        .anyMatch(r -> (r.blocks() instanceof HolderSet.Named<Block> blocks) &&
                                blocks.key().location().equals(BlockTags.MINEABLE_WITH_PICKAXE.location()))
                );
    }
}
