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
    public static final MapCodec<CopyDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            commonFields(instance).and(
                    instance.group(NbtProviders.CODEC.fieldOf("source").forGetter((function) -> function.source),
                    CopyOperation.CODEC.listOf().fieldOf("ops").forGetter((function) -> function.operations))
            ).apply(instance, CopyDataComponentFunction::new));
    private final NbtProvider source;
    private final List<CopyOperation> operations;

    CopyDataComponentFunction(List<LootItemCondition> conditions, NbtProvider source, List<CopyOperation> operations) {
        super(conditions);
        this.source = source;
        this.operations = List.copyOf(operations);
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

    public static Builder copyData(NbtProvider source) {
        return new Builder(source);
    }

    public static Builder copyData(LootContext.EntityTarget target) {
        return new Builder(ContextNbtProvider.forContextEntity(target));
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final NbtProvider source;
        private final List<CopyOperation> ops = Lists.newArrayList();

        Builder(NbtProvider source) {
            this.source = source;
        }

        public Builder copy(String sourcePath, String targetPath, MergeStrategy operator,
                                                      DataComponentType<CustomData> dataComponentType) {
            try {
                this.ops.add(new CopyOperation(NbtPathArgument.NbtPath.of(sourcePath),
                        NbtPathArgument.NbtPath.of(targetPath), operator, dataComponentType));
                return this;
            } catch (CommandSyntaxException var5) {
                throw new IllegalArgumentException(var5);
            }
        }

        public Builder copy(String source, String target, DataComponentType<CustomData> dataComponentType) {
            return this.copy(source, target, MergeStrategy.REPLACE, dataComponentType);
        }

        protected @NotNull Builder getThis() {
            return this;
        }

        public @NotNull LootItemFunction build() {
            return new CopyDataComponentFunction(this.getConditions(), this.source, this.ops);
        }
    }

    record CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, MergeStrategy op, DataComponentType<CustomData> dataComponentType) {
        public static final Codec<CopyOperation> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(NbtPathArgument.NbtPath.CODEC.fieldOf("source").forGetter(CopyOperation::sourcePath),
                NbtPathArgument.NbtPath.CODEC.fieldOf("target").forGetter(CopyOperation::targetPath),
                MergeStrategy.CODEC.fieldOf("op").forGetter(CopyOperation::op),
                DataComponentType.CODEC.fieldOf("dataComponentType").forGetter(CopyOperation::dataComponentType))
                .apply(instance, ((nbtPath, nbtPath2, mergeStrategy, dataComponentType1) ->
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
                              DataComponentType<CustomData> dataComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> sourceNbts) throws CommandSyntaxException {
                List<Tag> list = targetPath.getOrCreate(tags.get(dataComponentType), ListTag::new);
                list.forEach((foundNbt) -> {
                    if (foundNbt instanceof ListTag) {
                        sourceNbts.forEach(sourceNbt -> ((ListTag)foundNbt).add(sourceNbt.copy()));
                    }
                });
            }
        },
        MERGE("merge") {
            public void merge(HashMap<DataComponentType<CustomData>, CompoundTag> tags,
                              DataComponentType<CustomData> dataComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> sourceNbts) throws CommandSyntaxException {
                List<Tag> list = targetPath.getOrCreate(tags.get(dataComponentType), CompoundTag::new);
                list.forEach((foundNbt) -> {
                    if (foundNbt instanceof CompoundTag) {
                        sourceNbts.forEach((sourceNbt) -> {
                            if (sourceNbt instanceof CompoundTag) {
                                ((CompoundTag)foundNbt).merge((CompoundTag) sourceNbt);
                            }
                        });
                    }
                });
            }
        };

        public static final Codec<MergeStrategy> CODEC = StringRepresentable.fromEnum(MergeStrategy::values);
        private final String name;

        public abstract void merge(HashMap<DataComponentType<CustomData>, CompoundTag> tags,
                                   DataComponentType<CustomData> dataComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> sourceNbts)
                throws CommandSyntaxException;

        MergeStrategy(final String name) {
            this.name = name;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}