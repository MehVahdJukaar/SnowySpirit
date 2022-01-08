package net.mehvahdjukaar.snowyspirit.init;

import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.registerMessages();
        });
    }
}
