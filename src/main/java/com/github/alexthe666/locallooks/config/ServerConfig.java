package com.github.alexthe666.locallooks.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public final ForgeConfigSpec.BooleanValue giveMirrorOnStartup;
    public final ForgeConfigSpec.BooleanValue singleUseMirror;

    public ServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("locallooks-server");
        this.giveMirrorOnStartup = buildBoolean(builder, "Give Magic Mirror on Game Start", "all", true, "Whether players should be given a Magic Mirror when entering a world for the first time.");
        this.singleUseMirror = buildBoolean(builder, "Single Use Mirror", "all", true, "Whether Magic Mirrors are single-use in survival mode or not.");
    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, String catagory, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.DoubleValue buildDouble(ForgeConfigSpec.Builder builder, String name, String catagory, double defaultValue, double min, double max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
}
