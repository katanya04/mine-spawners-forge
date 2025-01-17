package me.katanya04.minespawnersforge.loot;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.katanya04.minespawnersforge.config.Config;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The loot pool modifier class, specifies how to serialize and deserialize a list of loot pools,
 * and how to apply them. There's also a {@link ForgeConfigSpec.DoubleValue} attribute that holds
 * a config field reference that provides the chance value of applying the loot tables. If this field
 * is null, the chance value is 1.0 (always applies the loot pools)
 */
public class LootPoolWithConfigChanceModifier extends LootModifier {
    private final ImmutableList<LootPool> pools;
    private final ForgeConfigSpec.DoubleValue configField; //from 0.0 to 1.0

    public LootPoolWithConfigChanceModifier(Stream<LootPool> pools, ForgeConfigSpec.DoubleValue configField) {
        super(new LootItemCondition[0]);
        this.pools = pools.collect(ImmutableList.toImmutableList());
        this.configField = configField;
    }

    public LootPoolWithConfigChanceModifier(Collection<LootPool> pools, ForgeConfigSpec.DoubleValue configField) {
        this(pools.stream(), configField);
    }

    public LootPoolWithConfigChanceModifier(LootPool pool, ForgeConfigSpec.DoubleValue configField) {
        this(Collections.singleton(pool), configField);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        double random = RandomSource.create().nextDouble();
        double prob = this.configField == null ? 1.0 : this.configField.get() / 100;
        if (random <= prob) {
            for (LootPool pool : pools) {
                pool.addRandomItems(generatedLoot::add, context);
            }
        }

        return generatedLoot;
    }

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
                instance.group(lootPoolCodec.fieldOf("pools").forGetter(self -> self.pools),
                        Codec.STRING.fieldOf("configField").forGetter(self -> self.configField == null ? "" : String.join(".", self.configField.getPath()))
                ).apply(instance, ((lootPools, s) -> new LootPoolWithConfigChanceModifier(lootPools, s.isEmpty() ? null : Config.SPEC.getValues().get(s))))
        ));
    }

    public static final Supplier<Codec<LootPoolWithConfigChanceModifier>> CODEC;

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
