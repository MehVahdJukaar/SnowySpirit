package net.mehvahdjukaar.snowyspirit.configs;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.integration.SeasonModCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryConfigs {

    public static ConfigSpec SPEC;

    public static void earlyLoad() {
        ConfigBuilder builder = ConfigBuilder.create(SnowySpirit.res("registry"), ConfigType.COMMON);
        init(builder);
        builder.onChange(SnowySpirit::onConfigReload);
        SPEC = builder.build();
        //load early
        SPEC.loadFromFile();
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

    //registry stuff
    public static Supplier<Boolean> SLEDS;
    public static Supplier<Boolean> GUMDROPS;
    public static Supplier<Boolean> GLOW_LIGHTS;
    public static Supplier<Boolean> CANDY_CANE;
    public static Supplier<Boolean> GINGER;
    public static Supplier<Boolean> EGGNOG;
    public static Supplier<Boolean> WREATH;
    public static Supplier<Boolean> SNOW_GLOBE;

    public static boolean isEnabled(String key) {
        if(key.contains("candy_cane"))return CANDY_CANE.get();
        else if(key.contains("sled"))return SLEDS.get();
        else if(key.contains("gumdrop"))return GUMDROPS.get();
        else if(key.contains("glow_light"))return GLOW_LIGHTS.get();
        else if(key.contains("ginger"))return GINGER.get();
        return CONFIGS_BY_NAME.getOrDefault(key, () -> true).get();
    }


    private static void init(ConfigBuilder builder) {

        builder.push("snowy_season");
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

        builder.push("season_mod_compat");

        SERENE_SEASONS_COMPAT = builder.comment("Enables compatibility with Serene Seasons (Forge) or Fabric Seasons (Fabric). Only takes effect if the mod is installed. Will make snowy season only active during certain seasons. Note that this will override previous time window settings")
                .define("enabled", SnowySpirit.SEASON_MOD_INSTALLED);
        if (SnowySpirit.SEASON_MOD_INSTALLED) SeasonModCompat.addConfig(builder);
        builder.pop();
        builder.pop();

        builder.push("misc");
        PACK_DEPENDANT_ASSETS = builder.comment("Allows generated assets to depend on installed resource and data packs. " +
                "This means that if for example you have a texture pack that changes the planks texture all generated sleds textures will be based off that one instead" +
                "Disable to have it only use vanilla assets").define("pack_dependant_assets", true);
        DEBUG_RESOURCES = builder.comment("Save generated resources to disk in a 'debug' folder in your game directory. Mainly for debug purposes but can be used to generate assets in all wood types for your mods :0")
                .define("debug_save_dynamic_pack", false);
        CUSTOM_CONFIGURED_SCREEN = builder.comment("Enables custom Configured config screen")
                .define("custom_configured_screen", true);
        MOD_TAB = builder.define("mod_creative_tab", false);
        builder.pop();

        builder.push("blocks_and_items");
        SLEDS = regConfig(builder, "sleds", true);
        GUMDROPS = regConfig(builder, "gumdrops", true);
        GLOW_LIGHTS = regConfig(builder, "glow_lights", true);
        CANDY_CANE = regConfig(builder, "candy_cane", true);
        GINGER = regConfig(builder, "ginger", true);
        EGGNOG = regConfig(builder, "eggnog", true);
        WREATH = regConfig(builder, "wreath", true);
        SNOW_GLOBE = regConfig(builder, "snow_globe", true);

        builder.pop();
    }
}
