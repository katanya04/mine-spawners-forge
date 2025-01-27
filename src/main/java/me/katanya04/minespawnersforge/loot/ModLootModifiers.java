package me.katanya04.minespawnersforge.loot;

import com.mojang.serialization.MapCodec;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import me.katanya04.minespawnersforge.loot.functions.CopyDataComponentFunction;
import me.katanya04.minespawnersforge.loot.functions.SetDataComponentFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registration of the {@link LootTableModifier} codec
 */
public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Mine_spawners_forge.MOD_ID);
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE.key(), Mine_spawners_forge.MOD_ID);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        LOOT_FUNCTIONS.register(eventBus);
    }

    public static RegistryObject<LootItemFunctionType<SetDataComponentFunction>> SET_DATA_COMPONENT;
    public static RegistryObject<LootItemFunctionType<CopyDataComponentFunction>> COPY_DATA_COMPONENT;
    static {
        LOOT_MODIFIER_SERIALIZERS.register("loot_pool", LootTableModifier.CODEC);
        SET_DATA_COMPONENT = LOOT_FUNCTIONS.register("set_data_component", () -> new LootItemFunctionType<>(SetDataComponentFunction.CODEC));
        COPY_DATA_COMPONENT = LOOT_FUNCTIONS.register("copy_data_component", () -> new LootItemFunctionType<>(CopyDataComponentFunction.CODEC));
    }
}
