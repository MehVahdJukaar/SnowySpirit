package net.mehvahdjukaar.snowyspirit;


import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.snowyspirit.configs.RegistryConfigs;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.integration.SereneSeasonsCompat;
import net.mehvahdjukaar.snowyspirit.reg.ModMemoryModules;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.mehvahdjukaar.snowyspirit.reg.ModSetup;
import net.mehvahdjukaar.snowyspirit.reg.ModSounds;
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

    public static boolean SUPP = PlatformHelper.isModLoaded("supplementaries");
    public static boolean SERENE_SEASONS_INSTALLED = PlatformHelper.isModLoaded("sereneseasons");

    public SnowySpirit() {
        bus.addListener(ModSetup::init);
        bus.addListener(SnowySpirit::reloadConfigsEvent);


        RegistryConfigs.earlyLoad();

        ModSounds.init();
        ModRegistry.init();
        ModMemoryModules.init();


        ServerDynamicResourcesHandler.INSTANCE.register();

        if (PlatformHelper.getEnv().isClient()) {
            ClientDynamicResourcesHandler.INSTANCE.register();
        }
    }
    //Do this shit next christmas
    //TODO: add glow light particles & emissive model
    //TODO: add advancements
    //TODO: sync xRot, chest weight, configs, tweak values
    //TODO: nerf sled acceleration without wolf to make wolf more relevant. can still be used for downhill descent
    //TODO: maybe make friction delend also on xRot to better handle slope descent

    public static boolean IS_CHRISTMAS_REAL_TIME;

    public static boolean SERENE_SEASONS;

    public static void reloadConfigsEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == Configs.SERVER_SPEC) {
            //refresh date after configs are loaded
            int startM = Configs.START_MONTH.get() - 1;
            int startD = Configs.START_DAY.get();

            int endM = Configs.END_MONTH.get() - 1;
            int endD = Configs.END_DAY.get();

            boolean inv = startM > endM;

            //pain
            Date start = new Date(0, startM, startD);
            Date end = new Date((inv ? 1 : 0), endM, endD);

            Date today = new Date(0, Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));

            //if seasonal use pumpkin placement time window
            IS_CHRISTMAS_REAL_TIME = today.after(start) && today.before(end);

            SERENE_SEASONS = SERENE_SEASONS_INSTALLED && RegistryConfigs.SERENE_SEASONS_COMPAT.get();

            if (SERENE_SEASONS) {

                SereneSeasonsCompat.refresh();
            }
        }
    }

    public static boolean isChristmasSeason(Level level) {
        if (SERENE_SEASONS) return SereneSeasonsCompat.isWinter(level);
        return IS_CHRISTMAS_REAL_TIME;
    }
}
