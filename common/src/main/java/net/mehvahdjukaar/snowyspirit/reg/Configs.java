package net.mehvahdjukaar.snowyspirit.reg;

import net.mehvahdjukaar.snowyspirit.integration.SereneSeasonsCompat;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;

public class Configs {
    public static ForgeConfigSpec.IntValue START_DAY;
    public static ForgeConfigSpec.IntValue START_MONTH;

    public static ForgeConfigSpec.IntValue END_DAY;
    public static ForgeConfigSpec.IntValue END_MONTH;

    public static ForgeConfigSpec.BooleanValue SERENE_SEASONS_COMPAT;

    public static ForgeConfigSpec SERVER_SPEC;

    public static ForgeConfigSpec buildConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("snow_season_start");
        START_MONTH = builder.comment("Day from which villagers will start placing wreaths, presents and throwing snowballs")
                .defineInRange("month", 12, 1, 12);
        START_DAY = builder.comment("Day from which villagers will  start placing wreaths, presents and throwing snowballs")
                .defineInRange("day", 20, 1, 31);
        builder.pop();
        builder.push("snow_season_end");
        END_MONTH = builder.comment("Day from which villagers will start removing placed wreaths")
                .defineInRange("month", 1, 1, 12);
        END_DAY = builder.comment("Day from which villagers will start removing placed wreaths")
                .defineInRange("day", 30, 1, 31);
        builder.pop();

        builder.push("serene_seasons_compat");

        SERENE_SEASONS_COMPAT = builder.comment("Enables Serene Seasons compatibility. Only takes effect if the mod is installed. Will make snowy season only active during certain seasons. Note that this will override previous time window settings")
                .define("enabled", ModList.get().isLoaded("sereneseasons"));
        if (ModList.get().isLoaded("sereneseasons")) SereneSeasonsCompat.addConfig(builder);
        builder.pop();

        SERVER_SPEC = builder.build();
        return SERVER_SPEC;
    }

    public static ForgeConfigSpec CLIENT_SPEC;
    public static ForgeConfigSpec.BooleanValue RESOURCE_PACK_SUPPORT;

    public static ForgeConfigSpec buildClientConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        RESOURCE_PACK_SUPPORT = builder.comment("Allows dynamic resource generations for leaf piles and bark to pick textures from installed resource pack. Will only really work well if those texture pack have a similar format to vanilla otherwise it will result in glitched textures")
                .define("dynamic_textures_from_resource_packs", false);
        CLIENT_SPEC = builder.build();
        return CLIENT_SPEC;
    }



}
