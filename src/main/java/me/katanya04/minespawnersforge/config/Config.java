package me.katanya04.minespawnersforge.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final HashMap<String, ConfigNumericField<?>> configValues;
    public static final ConfigNumericField<Double> DROP_CHANCE;

    static {
        BUILDER.push("Mine Spawners Config");
        configValues = new HashMap<>();
        var dropChance = BUILDER
                .comment("Chance of dropping the spawner when mined. From 0 (never) to 100 (always)")
                .defineInRange("dropChance", 1.0, 0.0, 1.0);
        DROP_CHANCE = new ConfigNumericField<>(dropChance);
        configValues.put("Mine Spawners Config.dropChance", DROP_CHANCE);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
