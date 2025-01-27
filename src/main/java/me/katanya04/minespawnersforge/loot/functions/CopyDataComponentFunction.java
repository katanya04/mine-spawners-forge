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
import net.minecraft.core.component.DataComponents;
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
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

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

    public @NotNull ItemStack run(@NotNull ItemStack item, @NotNull LootContext p_334578_) {
        Tag $$2 = this.source.get(p_334578_);
        if ($$2 != null) {
            MutableObject<CompoundTag> $$3 = new MutableObject<>();
            Supplier<Tag> $$4 = () -> {
                if ($$3.getValue() == null) {
                    $$3.setValue(item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
                }

                return (Tag) $$3.getValue();
            };
            this.operations.forEach((op) -> {
                op.apply($$4, $$2);
                CustomData.set(op.dataComponentType, item, $$3.getValue());
            });

        }
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

        public Builder copy(String p_331311_, String p_335916_, MergeStrategy p_332655_,
                                                      DataComponentType<CustomData> dataComponentType) {
            try {
                this.ops.add(new CopyOperation(NbtPathArgument.NbtPath.of(p_331311_),
                        NbtPathArgument.NbtPath.of(p_335916_), p_332655_, dataComponentType));
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

        CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, MergeStrategy op,
                      DataComponentType<CustomData> dataComponentType) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
            this.op = op;
            this.dataComponentType = dataComponentType;
        }

        public void apply(Supplier<Tag> p_328581_, Tag sourcePath) {
            try {
                List<Tag> sourceNBT = this.sourcePath.get(sourcePath);
                if (!sourceNBT.isEmpty()) {
                    this.op.merge(p_328581_.get(), this.targetPath, sourceNBT);
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
            public void merge(Tag p_327968_, NbtPathArgument.NbtPath p_329545_, List<Tag> p_330977_) throws CommandSyntaxException {
                p_329545_.set(p_327968_, Iterables.getLast(p_330977_));
            }
        },
        APPEND("append") {
            public void merge(Tag p_334866_, NbtPathArgument.NbtPath p_330111_, List<Tag> p_331184_) throws CommandSyntaxException {
                List<Tag> $$3 = p_330111_.getOrCreate(p_334866_, ListTag::new);
                $$3.forEach((p_328852_) -> {
                    if (p_328852_ instanceof ListTag) {
                        p_331184_.forEach((p_333613_) -> {
                            ((ListTag)p_328852_).add(p_333613_.copy());
                        });
                    }

                });
            }
        },
        MERGE("merge") {
            public void merge(Tag p_330874_, NbtPathArgument.NbtPath p_329263_, List<Tag> p_336007_) throws CommandSyntaxException {
                List<Tag> $$3 = p_329263_.getOrCreate(p_330874_, CompoundTag::new);
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

        public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        MergeStrategy(final String p_328833_) {
            this.name = p_328833_;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
