package me.katanya04.minespawnersforge.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.katanya04.minespawnersforge.config.Config;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * The loot pool modifier class, specifies how to serialize and deserialize a list of loot pools,
 * and how to apply them. There's also a {@link ForgeConfigSpec.DoubleValue} attribute that holds
 * a config field reference that provides the chance value of applying the loot tables. If this field
 * is null, the chance value is 1.0 (always applies the loot pools)
 */
public class LootPoolWithConfigChanceModifier extends LootModifier {
    private final LootPool pool;
    private final ForgeConfigSpec.DoubleValue configField; //from 0.0 to 1.0

    public LootPoolWithConfigChanceModifier(LootPool pool, ForgeConfigSpec.DoubleValue configField) {
        super(new LootItemCondition[0]);
        this.pool = pool;
        this.configField = configField;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(LootTable lootTable, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        double random = RandomSource.create().nextDouble();
        double prob = this.configField == null ? 1.0 : this.configField.get() / 100;
        if (random <= prob) {
            pool.addRandomItems(generatedLoot::add, context);
        }

        return generatedLoot;
    }

    static {
        CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance ->
                instance.group(LootPool.CODEC.fieldOf("pools").forGetter(self -> self.pool)).and(
                        Codec.STRING.fieldOf("configField").forGetter(self -> self.configField == null ? "" : String.join(".", self.configField.getPath()))
                ).apply(instance, ((lootPools, s) -> new LootPoolWithConfigChanceModifier(lootPools, s.isEmpty() ? null : Config.SPEC.getValues().get(s))))
        ));
    }

    public static final Supplier<MapCodec<LootPoolWithConfigChanceModifier>> CODEC;

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
