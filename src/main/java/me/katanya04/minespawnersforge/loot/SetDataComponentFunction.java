package me.katanya04.minespawnersforge.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
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

public class SetDataComponentFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec((p_336302_) ->
            commonFields(p_336302_).and(TagParser.LENIENT_CODEC.fieldOf("tag").forGetter((p_328670_) -> p_328670_.tag))
            .and(DataComponentType.CODEC.fieldOf("dataComponentType").forGetter((p_328670_) -> p_328670_.dataComponentType))
            .apply(p_336302_, SetDataComponentFunction::new)
    );
    private final CompoundTag tag;
    private final DataComponentType<?> dataComponentType;

    private SetDataComponentFunction(List<LootItemCondition> p_334383_, CompoundTag p_334528_, DataComponentType<?> dataComponentType) {
        super(p_334383_);
        this.tag = p_334528_;
        this.dataComponentType = dataComponentType;
    }


    public @NotNull LootItemFunctionType<SetDataComponentFunction> getType() {
        return ModLootModifiers.SET_DATA_COMPONENT;
    }

    public @NotNull ItemStack run(@NotNull ItemStack p_328195_, @NotNull LootContext p_331034_) {
        CustomData.update(DataComponents.CUSTOM_DATA, p_328195_, (p_335000_) -> {
            p_335000_.merge(this.tag);
        });
        return p_328195_;
    }

    public static Builder<?> setDataComponent(CompoundTag p_328660_, DataComponentType<?> dataComponentType) {
        return simpleBuilder((p_332883_) -> new SetDataComponentFunction(p_332883_, p_328660_, dataComponentType));
    }
}
