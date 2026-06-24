package net.dantemc.create_maintenance_control;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue WRITE_DEBUG_LOGS = BUILDER
            .comment("Enable debug logging for Create: Maintenance Control")
            .define("debugLogs", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
