package me.katanya04.minespawnersforge.loot;

import com.google.common.base.Suppliers;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * The loot pool modifier class, specifies how to serialize and deserialize a list of loot pools,
 * and how to apply them
 */
public class LootTableModifier extends LootModifier {
    private final List<LootPool> pools;

    public LootTableModifier(List<LootPool> pools) {
        super(new LootItemCondition[0]);
        this.pools = pools;
    }

    public LootTableModifier(LootPool pool) {
        this(Collections.singletonList(pool));
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        pools.forEach(p -> p.addRandomItems(generatedLoot::add, context));
        return generatedLoot;
    }

    public static final Supplier<Codec<LootTableModifier>> CODEC;

    static {
        var serializer = Deserializers.createLootTableSerializer().create();
        var lootPoolCodec = Codec.list(Codec.PASSTHROUGH.flatXmap(it -> {
            try {
                return DataResult.success(serializer.fromJson(it.convert(JsonOps.INSTANCE).getValue(), LootPool.class));
            } catch(JsonSyntaxException err) {
                return null;
            }
        }, it -> {
            try {
                return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, serializer.toJsonTree(it)));
            } catch(JsonSyntaxException err) {
                return null;
            }
        }));

        CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(instance ->
                instance.group(lootPoolCodec.fieldOf("pools").forGetter(self -> self.pools))
                .apply(instance, LootTableModifier::new)
        ));
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}