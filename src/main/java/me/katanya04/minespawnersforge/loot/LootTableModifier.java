package me.katanya04.minespawnersforge.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 *
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
    protected @NotNull ObjectArrayList<ItemStack> doApply(LootTable lootTable, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot;
    }

    static {
        /*CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance ->
                instance.group(LootTable.DIRECT_CODEC.fieldOf("pools").forGetter(self -> Collections.singletonList(self.pool))).and(
                        Codec.STRING.fieldOf("configField").forGetter(self -> self.configField == null ? "" : String.join(".", self.configField.getPath()))
                ).apply(instance, ((lootPools, s) -> new LootTableModifier(lootPools, s.isEmpty() ? null : Config.SPEC.getValues().get(s))))
        ));*/
        CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(instance ->
                instance.group(LootPool.CODEC.listOf().fieldOf("pools").forGetter(self -> self.pools))
                        .apply(instance, LootTableModifier::new))
        );
    }

    public static final Supplier<MapCodec<LootTableModifier>> CODEC;

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
