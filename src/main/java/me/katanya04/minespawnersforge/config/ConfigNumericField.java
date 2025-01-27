package me.katanya04.minespawnersforge.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public record ConfigNumericField<T extends Number>(ForgeConfigSpec.ConfigValue<T> configField) implements NumberProvider {
    static DeferredRegister<LootNumberProviderType> LOOT_NUMBER_PROVIDERS =
            DeferredRegister.create(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.key(), Mine_spawners_forge.MOD_ID);

    public static void register(IEventBus eventBus) {
        LOOT_NUMBER_PROVIDERS.register(eventBus);
    }

    public static final MapCodec<ConfigNumericField<Number>> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(Codec.STRING.fieldOf("value").forGetter(self -> String.join(".", self.configField.getPath())))
                    .apply(instance, path -> new ConfigNumericField<>(path.isEmpty() ? null : (ForgeConfigSpec.ConfigValue<Number>) Config.SPEC.getValues().get(path))));
    public static final RegistryObject<LootNumberProviderType> FROM_CONFIG = LOOT_NUMBER_PROVIDERS.register("from_config", () -> new LootNumberProviderType(CODEC));

    public float getFloat() {
        return this.configField.get().floatValue();
    }

    public void setValue(T value) {
        this.configField.set(value);
    }

    @Override
    public float getFloat(@NotNull LootContext lootContext) {
        return this.getFloat();
    }

    @Override
    @NotNull
    public LootNumberProviderType getType() {
        return FROM_CONFIG.get();
    }
}
