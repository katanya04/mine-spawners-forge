package me.katanya04.minespawnersforge.loot;

import com.mojang.serialization.Codec;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registration of the {@link LootPoolWithConfigChanceModifier} codec
 */
public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Mine_spawners_forge.MOD_ID);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }

    static {
        LOOT_MODIFIER_SERIALIZERS.register("loot_pool", LootPoolWithConfigChanceModifier.CODEC);
    }
}
