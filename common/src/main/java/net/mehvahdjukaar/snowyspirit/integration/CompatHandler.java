package net.mehvahdjukaar.snowyspirit.integration;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

public class CompatHandler {

    public static final boolean SUPPLEMENTARIES = isLoaded("supplementaries");
    public static final boolean FARMERS_DELIGHT = isLoaded("farmersdelight");
    public static final boolean SEASON_MOD = PlatHelper.getPlatform().isForge() ? isLoaded("sereneseasons") : isLoaded("seasons");
    public static final boolean BOATLOAD = isLoaded("boatload");
    public static final boolean POLYTONE = isLoaded("polytone");
    public static final boolean QUARK = isLoaded("quark");

    private static boolean isLoaded(String name) {
        return PlatHelper.isModLoaded(name);
    }
}
