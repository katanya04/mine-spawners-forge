package me.katanya04.minespawnersforge;

import me.katanya04.minespawnersforge.config.Config;
import me.katanya04.minespawnersforge.config.ConfigScreen;
import me.katanya04.minespawnersforge.config.ConfigNumericField;
import me.katanya04.minespawnersforge.loot.ModLootModifiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main mod class
 */
@Mod(Mine_spawners_forge.MOD_ID)
public class Mine_spawners_forge {
    public static final String MOD_ID = "mine_spawners_forge";

    public Mine_spawners_forge(FMLJavaModLoadingContext context) {
        ModLootModifiers.register(context.getModEventBus());
        ConfigNumericField.register(context.getModEventBus());
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "mine_spawners_forge-config.toml");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, prevScreen) -> new ConfigScreen(){})
            );
        }
    }
}
