package me.katanya04.minespawnersforge.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * Holds a numeric config field and implements {@link NumberProvider}
 * @param configField the config field
 * @param <T> the numeric (extends {@link Number}) type of the config field
 */
public record ConfigNumericField<T extends Number>(ForgeConfigSpec.ConfigValue<T> configField) implements NumberProvider {
    static DeferredRegister<LootNumberProviderType> LOOT_NUMBER_PROVIDERS =
            DeferredRegister.create(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.key(), Mine_spawners_forge.MOD_ID);

    public static void register(IEventBus eventBus) {
        LOOT_NUMBER_PROVIDERS.register(eventBus);
    }
    public static final RegistryObject<LootNumberProviderType> FROM_CONFIG = LOOT_NUMBER_PROVIDERS.register("from_config", () -> new LootNumberProviderType(new Serializer<>()));

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

    public static class Serializer<T extends Number> implements net.minecraft.world.level.storage.loot.Serializer<ConfigNumericField<T>> {
        @Override
        public void serialize(JsonObject jsonObject, ConfigNumericField<T> configNumericField, @NotNull JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("key",  String.join(".", configNumericField.configField.getPath()));
        }

        @Override
        public @NotNull ConfigNumericField<T> deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext) {
            return (ConfigNumericField<T>) Config.configValues.get(GsonHelper.getAsString(jsonObject, "key"));
        }
    }
}
