package me.katanya04.minespawnersforge.mixins;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin that fixes a {@link NullPointerException} when deserializing a loot pool name (the exception
 * gets thrown when there is no {@link net.minecraftforge.common.ForgeHooks.LootTableContext} available,
 * as it is in our case. See <a href="https://github.com/MinecraftForge/MinecraftForge/issues/9114">Forge Issue 9114</a>).
 */
/*@Mixin(LootPool.Serializer.class)
public class ReadLootPoolNameFix {
    @Unique
    private static int mine_spawners_forge$globalPoolCount = 0;
    @Redirect(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/world/level/storage/loot/LootPool;",
    at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;readPoolName(Lcom/google/gson/JsonObject;)Ljava/lang/String;"))
    private String injected(JsonObject json) {
        if (json.has("name"))
            return GsonHelper.getAsString(json, "name");
        else
            return "pool" + mine_spawners_forge$globalPoolCount++;
    }
}*/
