package net.mehvahdjukaar.snowyspirit;


import net.mehvahdjukaar.snowyspirit.common.generation.WorldGenHandler;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.init.Configs;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.mehvahdjukaar.snowyspirit.init.ModSetup;
import net.mehvahdjukaar.snowyspirit.integration.SereneSeasonsCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

/**
 * Author: MehVahdJukaar
 */
@Mod(Christmas.MOD_ID)
public class Christmas {
    public static final String MOD_ID = "snowyspirit";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean SUPP = ModList.get().isLoaded("supplementaries");

    public Christmas() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ModSetup::init);
        bus.addListener(Christmas::reloadConfigsEvent);

        MinecraftForge.EVENT_BUS.register(this);

        ModRegistry.init(bus);
        WorldGenHandler.init();

        ServerDynamicResourcesHandler.registerBus(bus);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientDynamicResourcesHandler.registerBus(bus));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.buildConfig());
    }
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

            SERENE_SEASONS = ModList.get().isLoaded("sereneseasons") && Configs.SERENE_SEASONS_COMPAT.get();

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
