package net.mehvahdjukaar.snowyspirit.forge;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.reg.ModSetup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MehVahdJukaar
 */
@Mod(SnowySpirit.MOD_ID)
public class SnowySpiritForge {
    public static final String MOD_ID = SnowySpirit.MOD_ID;

    public SnowySpiritForge() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(SnowySpiritForge::init);

        SnowySpirit.commonInit();

        RandomiumClient.init();
    }


    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModSetup.setup();
        });
    }
}
