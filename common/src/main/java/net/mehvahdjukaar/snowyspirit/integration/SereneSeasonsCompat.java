package net.mehvahdjukaar.snowyspirit.integration;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;
import java.util.List;

public class SereneSeasonsCompat {

    private static ForgeConfigSpec.ConfigValue<List<Season.SubSeason>> ALLOWED_SUB_SEASONS;
    private static List<Season.SubSeason> CACHED_SEASONS = new ArrayList<>();


    public static boolean isWinter(Level level){
        return (CACHED_SEASONS.contains(SeasonHelper.getSeasonState(level).getSubSeason()));
    }


    public static void addConfig(ConfigBuilder builder) {
        ALLOWED_SUB_SEASONS = builder.comment("Sub Seasons in which the mod will be active")
                .define("winter_season_sub_seasons",
                List.of(Season.SubSeason.MID_WINTER, Season.SubSeason.LATE_WINTER));
    }

    public static void refresh() {
        CACHED_SEASONS = ALLOWED_SUB_SEASONS.get();
    }
}
