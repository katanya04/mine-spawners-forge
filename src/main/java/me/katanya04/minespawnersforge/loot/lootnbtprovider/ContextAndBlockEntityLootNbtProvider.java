package me.katanya04.minespawnersforge.loot.lootnbtprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawnersforge.loot.LootRegistration;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A similar class to {@link net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider}, except you can also create one using a
 * {@link LootContext.BlockEntityTarget} instance (you can set the source to be the block entity)
 */
public class ContextAndBlockEntityLootNbtProvider implements NbtProvider {
    private static final ExtraCodecs.LateBoundIdMapper<String, Source<?>> SOURCES = new ExtraCodecs.LateBoundIdMapper<>();
    private static final Codec<Source<?>> GETTER_CODEC;
    public static final MapCodec<ContextAndBlockEntityLootNbtProvider> MAP_CODEC;
    public static final Codec<ContextAndBlockEntityLootNbtProvider> INLINE_CODEC;
    private final Source<?> source;

    private ContextAndBlockEntityLootNbtProvider(Source<?> source) {
        this.source = source;
    }

    @Override
    public @NotNull LootNbtProviderType getType() {
        return LootRegistration.CONTEXT_AND_BLOCK_ENTITY_LOOT_NBT_PROVIDER.get();
    }

    @Override
    public @Nullable Tag get(@NotNull LootContext context) {
        return this.source.get(context);
    }

    @Override
    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(this.source.contextParam());
    }

    public static NbtProvider fromSource(LootContext.EntityTarget source) {
        return new ContextAndBlockEntityLootNbtProvider(new ContextAndBlockEntityLootNbtProvider.EntitySource(source.getParam()));
    }

    public static NbtProvider fromBlockEntitySource(LootContext.BlockEntityTarget source) {
        return new ContextAndBlockEntityLootNbtProvider(new ContextAndBlockEntityLootNbtProvider.BlockEntitySource(source.getParam()));
    }

    static {
        for (LootContext.EntityTarget lootcontext$entitysource : LootContext.EntityTarget.values()) {
            SOURCES.put(lootcontext$entitysource.getSerializedName(), new EntitySource(lootcontext$entitysource.getParam()));
        }

        for (LootContext.BlockEntityTarget lootcontext$blockentitysource : LootContext.BlockEntityTarget.values()) {
            SOURCES.put(lootcontext$blockentitysource.getSerializedName(), new BlockEntitySource(lootcontext$blockentitysource.getParam()));
        }

        GETTER_CODEC = SOURCES.codec(Codec.STRING);
        MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance ->
                        instance.group(GETTER_CODEC.fieldOf("source").forGetter(provider -> provider.source))
                                .apply(instance, ContextAndBlockEntityLootNbtProvider::new)
        );
        INLINE_CODEC = GETTER_CODEC.xmap(ContextAndBlockEntityLootNbtProvider::new, p_422276_ -> p_422276_.source);
    }

    record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements Source<BlockEntity> {
        public Tag get(BlockEntity blockEntity) {
            return blockEntity.saveWithFullMetadata(blockEntity.getLevel().registryAccess());
        }
    }

    record EntitySource(ContextKey<? extends Entity> contextParam) implements Source<Entity> {
        public Tag get(Entity entity) {
            return NbtPredicate.getEntityTagToCompare(entity);
        }
    }

    interface Source<T> {
        ContextKey<? extends T> contextParam();

        @Nullable
        Tag get(T value);

        @Nullable
        default Tag get(LootContext context) {
            T object = context.getOptionalParameter((ContextKey<T>)this.contextParam());
            return object != null ? this.get(object) : null;
        }
    }
}
