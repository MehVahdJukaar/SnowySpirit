package net.mehvahdjukaar.snowyspirit;


import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.snowyspirit.common.network.ModNetworking;
import net.mehvahdjukaar.snowyspirit.configs.ClientConfigs;
import net.mehvahdjukaar.snowyspirit.configs.CommonConfigs;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.integration.FDCompat;
import net.mehvahdjukaar.snowyspirit.integration.SeasonModCompat;
import net.mehvahdjukaar.snowyspirit.reg.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.MonthDay;


/**
 * Author: MehVahdJukaar
 */
public class SnowySpirit {
    public static final String MOD_ID = "snowyspirit";

    public static ResourceLocation res(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static final boolean SUPPLEMENTARIES_INSTALLED = PlatHelper.isModLoaded("supplementaries");
    public static final boolean FARMERSDELIGHT_INSTALLED = PlatHelper.isModLoaded("farmersdelight");
    public static final boolean SEASON_MOD_INSTALLED = PlatHelper.isModLoaded(PlatHelper.getPlatform().isForge() ? "sereneseasons" : "seasons");
    public static final boolean BOATLOAD_INSTALLED = PlatHelper.isModLoaded("boatload");


    public static void commonInit() {

        // ginger bread golem glitchy leaves blocks
        //TODO: check wolf height
        // add item burntime
        // add block burn time
        //TODO: mongo screen rotation
        //slime in gumdrop
        //hostile gingeerbread golems
        //mongo
        //TODO: sync xRot, chest weight, tweak values
        //TODO: nerf sled acceleration without wolf to make wolf more relevant. can still be used for downhill descent
        //TODO: maybe make friction delend also on xRot to better handle slope descent

        CommonConfigs.init();

        RegHelper.registerDynamicResourceProvider(ServerDynamicResourcesHandler.INSTANCE);

        ModNetworking.init();

        RegHelper.registerSimpleRecipeCondition(SnowySpirit.res("flag"), CommonConfigs::isEnabled);

        ModSounds.init();
        ModRegistry.init();
        ModMemoryModules.init();
        ModCreativeTabs.init();
        if (FARMERSDELIGHT_INSTALLED) FDCompat.init();


        PlatHelper.addCommonSetup(ModSetup::setup);

        if (PlatHelper.getPhysicalSide().isClient()) {
            ClientConfigs.init();

            RegHelper.registerDynamicResourceProvider(ClientDynamicResourcesHandler.INSTANCE);

            ClientRegistry.init();
            ClientHelper.addClientSetup(ClientRegistry::setup);
        }

        RegHelper.addLootTableInjects(SnowySpirit::injectLootPools);
    }

    private static void injectLootPools(RegHelper.LootInjectEvent event) {
        String table = event.getTable().toString();
        if (table.equals("minecraft:gameplay/sniffer_digging")) {
            event.addTableReference(SnowySpirit.res("injects/ginger_sniffer"));
        }
    }


    public static boolean USES_SEASON_MOD;

    private static LocalDate seasonCheckedOn;
    private static boolean inSeasonOnThatDay;

    public static void onConfigReload() {
        USES_SEASON_MOD = SEASON_MOD_INSTALLED && CommonConfigs.SEASONS_MOD_COMPAT.get();
        seasonCheckedOn = null;
        if (USES_SEASON_MOD) {
            SeasonModCompat.refresh();
        }
    }

    public static boolean isChristmasRealTime() {
        LocalDate today = LocalDate.now();
        if (!today.equals(seasonCheckedOn)) {
            seasonCheckedOn = today;
            inSeasonOnThatDay = isInSeason(MonthDay.from(today));
        }
        return inSeasonOnThatDay;
    }

    private static boolean isInSeason(MonthDay day) {
        MonthDay start = CommonConfigs.SEASON_START.get();
        MonthDay end = CommonConfigs.SEASON_END.get();
        boolean afterStart = !day.isBefore(start);
        boolean beforeEnd = !day.isAfter(end);
        boolean wrapsAroundNewYear = start.isAfter(end);
        if (wrapsAroundNewYear) return afterStart || beforeEnd;
        return afterStart && beforeEnd;
    }

    public static boolean isChristmasSeason(Level level) {
        if (USES_SEASON_MOD) return SeasonModCompat.isWinter(level);
        return isChristmasRealTime();
    }
}
