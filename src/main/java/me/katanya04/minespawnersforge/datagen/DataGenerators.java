package me.katanya04.minespawnersforge.datagen;

import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

/**
 * The data generation class, used to generate the .json for the spawner loot table
 */
@Mod.EventBusSubscriber(modid = Mine_spawners_forge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        var lookup = event.getLookupProvider();
        event.getGenerator().addProvider(
                // Tell generator to run only when server data are generating
                event.includeServer(),
                new ModGlobalLootModifiersProvider(out, lookup)
        );
    }
}
