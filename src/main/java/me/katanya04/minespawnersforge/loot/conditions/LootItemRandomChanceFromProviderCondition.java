package me.katanya04.minespawnersforge.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.katanya04.minespawnersforge.loot.LootRegistration;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

/**
 * A loot condition similar to {@link net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition} that takes in a LootNumberProvider as the provider of the chance instead of a fixed value
 * @param chance the LootNumberProvider
 */
public record LootItemRandomChanceFromProviderCondition(NumberProvider chance) implements LootItemCondition {
    @Override
    public @NotNull LootItemConditionType getType() {
        return LootRegistration.lootItemRandomChanceFromProviderConditionType;
    }

    public boolean test(LootContext lootContext) {
        float f = this.chance.getFloat(lootContext);
        return lootContext.getRandom().nextFloat() < f;
    }

    public static LootItemCondition.Builder builder(NumberProvider lootNumberProvider) {
        return () -> new LootItemRandomChanceFromProviderCondition(lootNumberProvider);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceFromProviderCondition> {
        @Override
        public void serialize(JsonObject jsonObject, LootItemRandomChanceFromProviderCondition randomChanceFromProviderCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("chance", jsonSerializationContext.serialize(randomChanceFromProviderCondition.chance));
        }

        @Override
        public @NotNull LootItemRandomChanceFromProviderCondition deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext) {
            NumberProvider lootNumberProvider = jsonDeserializationContext.deserialize(jsonObject.get("chance"), NumberProvider.class);
            return new LootItemRandomChanceFromProviderCondition(lootNumberProvider);
        }
    }
}
