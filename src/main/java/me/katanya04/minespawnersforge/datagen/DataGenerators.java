package me.katanya04.minespawnersforge.datagen;

import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The data generation class, used to generate the .json for the spawner loot table
 */
@Mod.EventBusSubscriber(modid = Mine_spawners_forge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD) @SuppressWarnings("unused")
public class DataGenerators {
    @SubscribeEvent @SuppressWarnings("unused")
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                // Tell generator to run only when server data are generating
                event.includeServer(),
                (DataProvider.Factory<ModGlobalLootModifiersProvider>) ModGlobalLootModifiersProvider::new
        );
    }
}
