package net.mehvahdjukaar.snowyspirit.configs;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.integration.SereneSeasonsCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryConfigs {

    public static ConfigSpec SPEC;

    public static void earlyLoad() {
        ConfigBuilder builder = ConfigBuilder.create(SnowySpirit.res("registry"), ConfigType.COMMON);
        init(builder);

        SPEC = builder.build();
        //load early
        SPEC.loadFromFile();
    }


    public static boolean isEnabled(String key) {
        return CONFIGS_BY_NAME.getOrDefault(key, () -> true).get();

    }

    private static Supplier<Boolean> regConfig(ConfigBuilder builder, String name, Boolean value) {
        var config = builder.define(name, value);
        CONFIGS_BY_NAME.put(name, config);
        return config;
    }

    private static final Map<String, Supplier<Boolean>> CONFIGS_BY_NAME = new HashMap<>();

    public static Supplier<Integer> START_DAY;
    public static Supplier<Integer> START_MONTH;

    public static Supplier<Integer> END_DAY;
    public static Supplier<Integer> END_MONTH;

    public static Supplier<Boolean> SERENE_SEASONS_COMPAT;

    public static Supplier<Boolean> MOD_TAB;

    public static Supplier<Boolean> CUSTOM_CONFIGURED_SCREEN;

    public static Supplier<Boolean> PACK_DEPENDANT_ASSETS;
    public static Supplier<Boolean> DEBUG_RESOURCES;

    private static void init(ConfigBuilder builder) {

        builder.push("snow_season_start");
        START_MONTH = builder.comment("Day from which villagers will start placing wreaths, presents and throwing snowballs")
                .define("month", 12, 1, 12);
        START_DAY = builder.comment("Day from which villagers will  start placing wreaths, presents and throwing snowballs")
                .define("day", 20, 1, 31);
        builder.pop();
        builder.push("snow_season_end");
        END_MONTH = builder.comment("Day from which villagers will start removing placed wreaths")
                .define("month", 1, 1, 12);
        END_DAY = builder.comment("Day from which villagers will start removing placed wreaths")
                .define("day", 30, 1, 31);
        builder.pop();

        builder.push("serene_seasons_compat");

        SERENE_SEASONS_COMPAT = builder.comment("Enables Serene Seasons compatibility. Only takes effect if the mod is installed. Will make snowy season only active during certain seasons. Note that this will override previous time window settings")
                .define("enabled", SnowySpirit.SERENE_SEASONS_INSTALLED);
        if (SnowySpirit.SERENE_SEASONS_INSTALLED) SereneSeasonsCompat.addConfig(builder);
        builder.pop();

        builder.push("misc");
        PACK_DEPENDANT_ASSETS = builder.comment("Allows generated assets to depend on installed resource and data packs. " +
                "This means that if for example you have a texture pack that changes the planks texture all generated sleds textures will be based off that one instead" +
                "Disable to have it only use vanilla assets").define("pack_dependant_assets", true);
        DEBUG_RESOURCES = builder.comment("Save generated resources to disk in a 'debug' folder in your game directory. Mainly for debug purposes but can be used to generate assets in all wood types for your mods :0")
                .define("debug_save_dynamic_pack", false);
        CUSTOM_CONFIGURED_SCREEN = builder.comment("Enables custom Configured config screen")
                .define("custom_configured_screen", true);
        MOD_TAB = builder.define("mod_creative_tab",false);
        builder.pop();
    }
}
