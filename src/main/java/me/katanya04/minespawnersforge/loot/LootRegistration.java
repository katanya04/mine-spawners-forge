package me.katanya04.minespawnersforge.loot;

import com.mojang.serialization.Codec;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import me.katanya04.minespawnersforge.loot.conditions.LootItemRandomChanceFromProviderCondition;

/**
 * Registration of the {@link LootTableModifier} codec and the {@link LootItemRandomChanceFromProviderCondition} loot condition type
 */
public class LootRegistration {
    public static LootItemConditionType lootItemRandomChanceFromProviderConditionType =
            new LootItemConditionType(new LootItemRandomChanceFromProviderCondition.Serializer());
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Mine_spawners_forge.MOD_ID);
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Mine_spawners_forge.MOD_ID);

    static {
        LOOT_MODIFIER_SERIALIZERS.register("loot_pool", LootTableModifier.CODEC);
        LOOT_CONDITION_TYPES.register("random_chance_from_provider", () -> lootItemRandomChanceFromProviderConditionType);
    }

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        LOOT_CONDITION_TYPES.register(eventBus);
    }
}
