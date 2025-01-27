package me.katanya04.minespawnersforge.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ConfigNumericField<Double> DROP_CHANCE;

    static {
        BUILDER.push("Mine Spawners Config");
        DROP_CHANCE = new ConfigNumericField<>(BUILDER
                .comment("Chance of dropping the spawner when mined. From 0 (never) to 100 (always)")
                .defineInRange("dropChance", 100.0, 0.0, 100.0));
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
