package me.katanya04.minespawnersforge.tags;

import me.katanya04.minespawnersforge.Mine_spawners_forge;
import me.katanya04.minespawnersforge.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A dynamic tag, it's not registered anywhere, it's just a map tag-supplier of elements
 */
public class DynamicTags {
    private static final Map<TagKey<Item>, Supplier<Set<Item>>> DYNAMIC_TAGS = new HashMap<>();
    public static final TagKey<Item> BLACKLISTED = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Mine_spawners_forge.MOD_ID, "blacklisted"));
    static {
        DYNAMIC_TAGS.put(BLACKLISTED, () -> Config.BLACKLISTED_PICKAXES.get().stream().map(p -> {
            Optional<Holder.Reference<Item>> pickaxe = BuiltInRegistries.ITEM.get(ResourceLocation.parse(p));
            return pickaxe.map(Holder::get).orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public static boolean isInTag(ItemStack stack, TagKey<Item> tag) {
        return stack.is(tag) || DYNAMIC_TAGS.getOrDefault(tag, Collections::emptySet).get().contains(stack.getItem());
    }
}
