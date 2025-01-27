package me.katanya04.minespawnersforge.datagen;

import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

/**
 * The data generation class, used to generate the .json for the spawner loot table
 */
@Mod.EventBusSubscriber(modid = Mine_spawners_forge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    private static final Supplier<RegistrySetBuilder> REGISTRY = () -> new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, ctx -> ctx.lookup(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.SILK_TOUCH).get()
            );
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        var lookup = event.getLookupProvider();
        var patched = RegistryPatchGenerator.createLookup(lookup, REGISTRY.get())
                .thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        event.getGenerator().addProvider(
                // Tell generator to run only when server data are generating
                event.includeServer(),
                new ModGlobalLootModifiersProvider(out, patched)
        );
    }
}
