package me.katanya04.minespawnersforge.mixins;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

import static net.minecraft.core.RegistryAccess.LOGGER;

@Mixin(value = LootModifierManager.class, remap = false)
public abstract class DebugMixin {
    @Final
    @Shadow
    private HolderLookup.Provider registries;

    @Shadow
    private Map<ResourceLocation, IGlobalLootModifier> modifiers;
    @Unique
    private JsonElement mine_spawners_forge$prev;

    @Inject(at = @At(value = "HEAD"), method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
    private void injected2(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManagerIn, ProfilerFiller profilerIn, CallbackInfo ci) {
        mine_spawners_forge$prev = resources.remove(ResourceLocation.fromNamespaceAndPath(Mine_spawners_forge.MOD_ID, "drop_spawner"));
    }

    @Inject(at = @At(value = "TAIL"), method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManagerIn, ProfilerFiller profilerIn, CallbackInfo ci, ImmutableMap.Builder<ResourceLocation, IGlobalLootModifier> builder, RegistryOps<JsonElement> ops) {
        new Thread(() -> {
            int msTime = 0;
            while (registries.listRegistries().filter(r -> r.key().equals(Registries.ITEM)).findAny().map(r -> r.listTags().filter(t -> t.key().equals(ItemTags.PICKAXES))).isEmpty()) {
                if (msTime >= 10000) {
                    LOGGER.warn("More than 10 seconds waiting for #minecraft:pickaxes to be loaded into the registry...");
                    break;
                }
                try {
                    Thread.sleep(10); // Would be better to make the thread that loads the #minecraft:pickaxes tag into the registry to wake up this thread... But I don't know when or from where it gets added lol
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                msTime += 10;
            }
            var ops2 = registries.createSerializationContext(JsonOps.INSTANCE);
            ResourceLocation thisFuckingTable = ResourceLocation.fromNamespaceAndPath(Mine_spawners_forge.MOD_ID, "drop_spawner");
            IGlobalLootModifier.DIRECT_CODEC.parse(ops2, mine_spawners_forge$prev)
                    // log error if parse fails
                    .ifError(error -> LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", thisFuckingTable, error.message()))
                    // add loot modifier if parse succeeds
                    .ifSuccess(modifier -> builder.put(thisFuckingTable, modifier));
            modifiers = builder.build();
        }).start();
    }
}
