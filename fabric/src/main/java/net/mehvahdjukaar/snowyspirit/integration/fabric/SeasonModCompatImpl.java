package net.mehvahdjukaar.snowyspirit.integration.fabric;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

//fabric seasons
public class SeasonModCompatImpl {

    private static Supplier<List<String>> SEASONS_CONFIG;
    private static List<Season> VALID_WINTER_SEASONS = new ArrayList<>();


    //if winder AI should be on
    public static boolean isWinter(Level level) {
        return (VALID_WINTER_SEASONS.contains(FabricSeasons.getCurrentSeason(level)));
    }


    public static void addConfig(ConfigBuilder builder) {
        SEASONS_CONFIG = builder.comment("Season in which the mod villager AI behaviors will be active")
                .define("winter_seasons",
                        List.of(Season.WINTER.toString()),
                        s -> Arrays.stream(Season.values()).anyMatch(d -> d.toString().equals(s)));
    }

    public static void refresh() {
        VALID_WINTER_SEASONS = SEASONS_CONFIG.get().stream().map(Season::valueOf).toList();
    }
}
