package net.mehvahdjukaar.snowboundspirit;


import net.mehvahdjukaar.snowboundspirit.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: MehVahdJukaar
 */
@Mod(Christmas.MOD_ID)
public class Christmas {
    public static final String MOD_ID = "snowboundspirit";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public Christmas() {
        int a = 6;
        int b = 6;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.init(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }


}
