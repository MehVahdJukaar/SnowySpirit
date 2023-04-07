package net.mehvahdjukaar.snowyspirit.forge;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.integration.configured.ModConfigSelectScreen;
import net.mehvahdjukaar.snowyspirit.reg.ClientRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SnowySpirit.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SnowySpiritForgeClient {


    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientRegistry.setup();

            if (ModList.get().isLoaded("configured")) {
                ModConfigSelectScreen.registerConfigScreen(SnowySpirit.MOD_ID, ModConfigSelectScreen::new);
            }

        });
    }

}
