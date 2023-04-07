package net.mehvahdjukaar.snowyspirit;


import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.mehvahdjukaar.snowyspirit.configs.ModConfigs;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.integration.FDCompat;
import net.mehvahdjukaar.snowyspirit.integration.SeasonModCompat;
import net.mehvahdjukaar.snowyspirit.reg.ModMemoryModules;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.mehvahdjukaar.snowyspirit.reg.ModSounds;
import net.mehvahdjukaar.snowyspirit.reg.ModWorldgenRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

/**
 * Author: MehVahdJukaar
 */
public class SnowySpirit {
    public static final String MOD_ID = "snowyspirit";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static final boolean SUPPLEMENTARIES_INSTALLED = PlatformHelper.isModLoaded("supplementaries");
    public static final boolean FARMERSDELIGHT_INSTALLED = PlatformHelper.isModLoaded("farmersdelight");
    public static final boolean SEASON_MOD_INSTALLED = PlatformHelper.isModLoaded(PlatformHelper.getPlatform().isForge() ? "sereneseasons" : "seasons");


    public static void commonInit() {
        ModConfigs.init();

        NetworkHandler.init();

        RegHelper.registerSimpleRecipeCondition(SnowySpirit.res("flag"), ModConfigs::isEnabled);

        ModSounds.init();
        ModRegistry.init();
        if (FARMERSDELIGHT_INSTALLED) FDCompat.init();
        ModMemoryModules.init();

        ModWorldgenRegistry.init();

        ServerDynamicResourcesHandler.INSTANCE.register();

        if (PlatformHelper.getEnv().isClient()) {
            ClientDynamicResourcesHandler.INSTANCE.register();
        }
    }
    //snow globe item texture
    //Do this shit next christmas
    //sleds loose their chest
    //TODO: add advancements
    //TODO: sync xRot, chest weight, tweak values
    //TODO: nerf sled acceleration without wolf to make wolf more relevant. can still be used for downhill descent
    //TODO: maybe make friction delend also on xRot to better handle slope descent

    public static boolean IS_CHRISTMAS_REAL_TIME;

    public static boolean USES_SEASON_MOD;

    public static void onConfigReload() {

        //refresh date after configs are loaded
        int startM = ModConfigs.START_MONTH.get() - 1;
        int startD = ModConfigs.START_DAY.get();

        int endM = ModConfigs.END_MONTH.get() - 1;
        int endD = ModConfigs.END_DAY.get();

        boolean inv = startM > endM;

        //pain
        Date start = new Date(0, startM, startD);
        Date end = new Date((inv ? 1 : 0), endM, endD);

        Date today = new Date(0, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));
        if (today.before(start) && inv) today = new Date(1, today.getMonth(), today.getDate());
        //TODO: rewrite properly
        //if seasonal use pumpkin placement time window
        IS_CHRISTMAS_REAL_TIME = today.after(start) && today.before(end);

        USES_SEASON_MOD = SEASON_MOD_INSTALLED && ModConfigs.SEASONS_MOD_COMPAT.get();

        if (USES_SEASON_MOD) {
            SeasonModCompat.refresh();
        }

    }

    public static boolean isChristmasSeason(Level level) {
        if (USES_SEASON_MOD) return SeasonModCompat.isWinter(level);
        return IS_CHRISTMAS_REAL_TIME;
    }
}
