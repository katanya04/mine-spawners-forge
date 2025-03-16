package me.katanya04.minespawnersforge.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawnersforge.loot.ModLootModifiers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A LootTable function that sets NBT to a DataComponent of the target
 */
public class SetDataComponentFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec((p_336302_) ->
            commonFields(p_336302_).and(TagParser.LENIENT_CODEC.fieldOf("tag").forGetter((p_328670_) -> p_328670_.tag))
            .and(DataComponentType.CODEC.fieldOf("dataComponentType").forGetter((p_328670_) -> p_328670_.dataComponentType))
            .apply(p_336302_, ((lootItemConditions, compoundTag, dataComponentType1) ->
                    new SetDataComponentFunction(lootItemConditions, compoundTag, (DataComponentType<CustomData>) dataComponentType1)))
    );
    private final CompoundTag tag;
    private final DataComponentType<CustomData> dataComponentType;

    private SetDataComponentFunction(List<LootItemCondition> conditions, CompoundTag tag, DataComponentType<CustomData> dataComponentType) {
        super(conditions);
        this.tag = tag;
        this.dataComponentType = dataComponentType;
    }

    @Override
    public @NotNull LootItemFunctionType<SetDataComponentFunction> getType() {
        return ModLootModifiers.SET_DATA_COMPONENT.get();
    }

    @Override
    public @NotNull ItemStack run(@NotNull ItemStack item, @NotNull LootContext ignored) {
        CustomData.update(this.dataComponentType, item, (itemDataComponent) -> itemDataComponent.merge(this.tag));
        return item;
    }

    public static Builder<?> setDataComponent(CompoundTag tag, DataComponentType<CustomData> dataComponentType) {
        return simpleBuilder((conditions) -> new SetDataComponentFunction(conditions, tag, dataComponentType));
    }
}
