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

    private SetDataComponentFunction(List<LootItemCondition> p_334383_, CompoundTag p_334528_, DataComponentType<CustomData> dataComponentType) {
        super(p_334383_);
        this.tag = p_334528_;
        this.dataComponentType = dataComponentType;
    }


    public @NotNull LootItemFunctionType<SetDataComponentFunction> getType() {
        return ModLootModifiers.SET_DATA_COMPONENT.get();
    }

    public @NotNull ItemStack run(@NotNull ItemStack item, @NotNull LootContext p_331034_) {
        CustomData.update(this.dataComponentType, item, (itemDataComponent) -> itemDataComponent.merge(this.tag));
        return item;
    }

    public static Builder<?> setDataComponent(CompoundTag p_328660_, DataComponentType<CustomData> dataComponentType) {
        return simpleBuilder((p_332883_) -> new SetDataComponentFunction(p_332883_, p_328660_, dataComponentType));
    }
}
