package me.katanya04.minespawnersforge.loot.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;

import me.katanya04.minespawnersforge.loot.ModLootModifiers;
import me.katanya04.minespawnersforge.tags.DynamicTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

/**
 * A Loot Item Condition that checks if the tool used matches the given predicate and has a specified {@link DynamicTags}
 * @param predicate the predicate to check
 * @param dynamicTag the dynamic tag to check
 */
public record MatchToolWithDynamicTag(Optional<ItemPredicate> predicate, TagKey<Item> dynamicTag) implements LootItemCondition {
    public static final MapCodec<MatchToolWithDynamicTag> CODEC = RecordCodecBuilder.mapCodec(
            p_327654_ -> p_327654_.group(
                    ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchToolWithDynamicTag::predicate),
                    TagKey.codec(Registries.ITEM).fieldOf("dynamicTag").forGetter(MatchToolWithDynamicTag::dynamicTag)
            ).apply(p_327654_, MatchToolWithDynamicTag::new)
    );

    @Override
    public @NotNull LootItemConditionType getType() {
        return ModLootModifiers.MATCH_TOOL_WITH_DYNAMIC_TAG.get();
    }

    @Override
    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext p_82000_) {
        ItemStack itemstack = p_82000_.getOptionalParameter(LootContextParams.TOOL);
        return itemstack != null && (this.predicate.isEmpty() || this.predicate.get().test(itemstack)) &&
                DynamicTags.isInTag(itemstack, this.dynamicTag);
    }

    public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder p_81998_, TagKey<Item> dynamicTag) {
        return () -> new MatchToolWithDynamicTag(Optional.of(p_81998_.build()), dynamicTag);
    }
}