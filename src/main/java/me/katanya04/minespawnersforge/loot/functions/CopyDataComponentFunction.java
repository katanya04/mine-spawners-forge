package me.katanya04.minespawnersforge.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawnersforge.loot.ModLootModifiers;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A LootTable function that copies NBT from the source to a DataComponent of the target
 */
public class CopyDataComponentFunction extends LootItemConditionalFunction {
    public static final MapCodec<CopyDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec((p_334162_) ->
            commonFields(p_334162_).and(p_334162_.group(NbtProviders.CODEC.fieldOf("source").forGetter((p_330558_) -> p_330558_.source),
            CopyOperation.CODEC.listOf().fieldOf("ops").forGetter((p_327675_) -> p_327675_.operations))).apply(p_334162_, CopyDataComponentFunction::new));
    private final NbtProvider source;
    private final List<CopyOperation> operations;

    CopyDataComponentFunction(List<LootItemCondition> p_330573_, NbtProvider p_334617_, List<CopyOperation> p_334520_) {
        super(p_330573_);
        this.source = p_334617_;
        this.operations = List.copyOf(p_334520_);
    }

    public @NotNull LootItemFunctionType<CopyDataComponentFunction> getType() {
        return ModLootModifiers.COPY_DATA_COMPONENT.get();
    }

    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return this.source.getReferencedContextParams();
    }

    public @NotNull ItemStack run(@NotNull ItemStack item, @NotNull LootContext lootContext) {
        Tag sourceTag = this.source.get(lootContext);
        if (sourceTag == null)
            return item;
        HashMap<DataComponentType<CustomData>, CompoundTag> tags = new HashMap<>();
        this.operations.forEach((op) -> {
            if (!tags.containsKey(op.dataComponentType))
                tags.put(op.dataComponentType, item.getOrDefault(op.dataComponentType, CustomData.EMPTY).copyTag());
            op.apply(tags, sourceTag);
        });
        tags.forEach((type, compoundTag) -> CustomData.set(type, item, compoundTag));

        return item;
    }

    public static Builder copyData(NbtProvider p_335021_) {
        return new Builder(p_335021_);
    }

    public static Builder copyData(LootContext.EntityTarget p_329362_) {
        return new Builder(ContextNbtProvider.forContextEntity(p_329362_));
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final NbtProvider source;
        private final List<CopyOperation> ops = Lists.newArrayList();

        Builder(NbtProvider p_328406_) {
            this.source = p_328406_;
        }

        public Builder copy(String sourcePath, String targetPath, MergeStrategy p_332655_,
                                                      DataComponentType<CustomData> dataComponentType) {
            try {
                this.ops.add(new CopyOperation(NbtPathArgument.NbtPath.of(sourcePath),
                        NbtPathArgument.NbtPath.of(targetPath), p_332655_, dataComponentType));
                return this;
            } catch (CommandSyntaxException var5) {
                throw new IllegalArgumentException(var5);
            }
        }

        public Builder copy(String p_333187_, String p_327847_, DataComponentType<CustomData> dataComponentType) {
            return this.copy(p_333187_, p_327847_, MergeStrategy.REPLACE, dataComponentType);
        }

        protected @NotNull Builder getThis() {
            return this;
        }

        public @NotNull LootItemFunction build() {
            return new CopyDataComponentFunction(this.getConditions(), this.source, this.ops);
        }
    }

    record CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, MergeStrategy op, DataComponentType<CustomData> dataComponentType) {
        public static final Codec<CopyOperation> CODEC = RecordCodecBuilder.create((p_333172_) ->
                p_333172_.group(NbtPathArgument.NbtPath.CODEC.fieldOf("source").forGetter(CopyOperation::sourcePath),
                NbtPathArgument.NbtPath.CODEC.fieldOf("target").forGetter(CopyOperation::targetPath),
                MergeStrategy.CODEC.fieldOf("op").forGetter(CopyOperation::op),
                DataComponentType.CODEC.fieldOf("dataComponentType").forGetter(CopyOperation::dataComponentType))
                .apply(p_333172_, ((nbtPath, nbtPath2, mergeStrategy, dataComponentType1) ->
                        new CopyOperation(nbtPath, nbtPath2, mergeStrategy, (DataComponentType<CustomData>) dataComponentType1)))
        );

        public void apply(HashMap<DataComponentType<CustomData>, CompoundTag> tags, Tag sourceTag) {
            try {
                List<Tag> sourceNBT = this.sourcePath.get(sourceTag);
                if (!sourceNBT.isEmpty()) {
                    this.op.merge(tags, this.dataComponentType, this.targetPath, sourceNBT);
                }
            } catch (CommandSyntaxException ignored) {}
        }

        public NbtPathArgument.NbtPath sourcePath() {
            return this.sourcePath;
        }

        public NbtPathArgument.NbtPath targetPath() {
            return this.targetPath;
        }

        public MergeStrategy op() {
            return this.op;
        }

        public DataComponentType<CustomData> dataComponentType() {
            return this.dataComponentType;
        }
    }

    public enum MergeStrategy implements StringRepresentable {
        REPLACE("replace") {
            public void merge(HashMap<DataComponentType<CustomData>, CompoundTag> tags,
                              DataComponentType<CustomData> dataComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> p_330977_) throws CommandSyntaxException {
                Tag newValue = Iterables.getLast(p_330977_).copy();
                tags.put(dataComponentType, (CompoundTag) newValue);
                targetPath.set(tags.get(dataComponentType), newValue);
            }
        },
        APPEND("append") {
            public void merge(HashMap<DataComponentType<CustomData>, CompoundTag> tags,
                              DataComponentType<CustomData> dataComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> p_331184_) throws CommandSyntaxException {
                List<Tag> $$3 = targetPath.getOrCreate(tags.get(dataComponentType), ListTag::new);
                $$3.forEach((p_328852_) -> {
                    if (p_328852_ instanceof ListTag) {
                        p_331184_.forEach((p_333613_) -> ((ListTag)p_328852_).add(p_333613_.copy()));
                    }
                });
            }
        },
        MERGE("merge") {
            public void merge(HashMap<DataComponentType<CustomData>, CompoundTag> tags,
                              DataComponentType<CustomData> dataComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> p_336007_) throws CommandSyntaxException {
                List<Tag> $$3 = targetPath.getOrCreate(tags.get(dataComponentType), CompoundTag::new);
                $$3.forEach((p_328276_) -> {
                    if (p_328276_ instanceof CompoundTag) {
                        p_336007_.forEach((p_330167_) -> {
                            if (p_330167_ instanceof CompoundTag) {
                                ((CompoundTag)p_328276_).merge((CompoundTag)p_330167_);
                            }

                        });
                    }

                });
            }
        };

        public static final Codec<MergeStrategy> CODEC = StringRepresentable.fromEnum(MergeStrategy::values);
        private final String name;

        public abstract void merge(HashMap<DataComponentType<CustomData>, CompoundTag> tags,
                                   DataComponentType<CustomData> dataComponentType, NbtPathArgument.NbtPath var2, List<Tag> var3)
                throws CommandSyntaxException;

        MergeStrategy(final String p_328833_) {
            this.name = p_328833_;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}