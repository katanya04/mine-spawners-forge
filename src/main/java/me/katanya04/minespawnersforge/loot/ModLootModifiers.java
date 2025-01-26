package me.katanya04.minespawnersforge.loot;

import com.mojang.serialization.MapCodec;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registration of the {@link LootPoolWithConfigChanceModifier} codec
 */
public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Mine_spawners_forge.MOD_ID);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }

    public static LootItemFunctionType<SetDataComponentFunction> SET_DATA_COMPONENT;
    public static LootItemFunctionType<CopyDataComponentFunction> COPY_DATA_COMPONENT;
    static {
        LOOT_MODIFIER_SERIALIZERS.register("loot_pool", LootPoolWithConfigChanceModifier.CODEC);
        SET_DATA_COMPONENT = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Mine_spawners_forge.MOD_ID + ":set_data_component",
                new LootItemFunctionType<>(SetDataComponentFunction.CODEC));
        COPY_DATA_COMPONENT = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Mine_spawners_forge.MOD_ID + ":copy_data_component",
                new LootItemFunctionType<>(CopyDataComponentFunction.CODEC));
    }
}
