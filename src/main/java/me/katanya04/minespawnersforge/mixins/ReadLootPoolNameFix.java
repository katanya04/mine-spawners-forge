package me.katanya04.minespawnersforge.mixins;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin that fixes a {@link NullPointerException} when deserializing a loot pool name (the exception
 * gets thrown when there is no {@link net.minecraftforge.common.ForgeHooks.LootTableContext} available,
 * as it is in our case. See <a href="https://github.com/MinecraftForge/MinecraftForge/issues/9114">Forge Issue 9114</a>).
 */
@Mixin(value = ForgeHooks.class, remap = false)
public class ReadLootPoolNameFix {
    @Inject(method = "readPoolName", at = @At(value = "HEAD"), cancellable = true)
    private static void injected(JsonObject json, CallbackInfoReturnable<String> cir) {
        if (json.has("name")) {
            cir.setReturnValue(GsonHelper.getAsString(json, "name"));
        }
    }
}