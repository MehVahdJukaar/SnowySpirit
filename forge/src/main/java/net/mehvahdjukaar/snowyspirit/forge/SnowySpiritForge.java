package net.mehvahdjukaar.snowyspirit.forge;

import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.mehvahdjukaar.snowyspirit.reg.ClientRegistry;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.mehvahdjukaar.snowyspirit.reg.ModSetup;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.network.ClientBoundSyncAllWreaths;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.network.ClientBoundSyncWreathMessage;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MehVahdJukaar
 */
@Mod(SnowySpirit.MOD_ID)
public class SnowySpiritForge {

    public SnowySpiritForge() {


        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(SnowySpiritForge::init);

        SnowySpirit.commonInit();

        if (PlatformHelper.getEnv().isClient()) {
            ClientRegistry.init();
        }
    }


    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            ModSetup.setup();

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(Utils.getID(ModRegistry.GINGER.get()), ModRegistry.GINGER_POT);

            NetworkHandler.CHANNEL.register(NetworkDir.PLAY_TO_CLIENT,
                    ClientBoundSyncWreathMessage.class,
                    ClientBoundSyncWreathMessage::new);

            NetworkHandler.CHANNEL.register(NetworkDir.PLAY_TO_CLIENT,
                    ClientBoundSyncAllWreaths.class,
                    ClientBoundSyncAllWreaths::new);

        });
    }
}
