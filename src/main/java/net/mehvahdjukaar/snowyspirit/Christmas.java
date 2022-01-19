package net.mehvahdjukaar.snowyspirit;


import net.mehvahdjukaar.snowyspirit.common.generation.WorldGenHandler;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.mehvahdjukaar.snowyspirit.init.ModSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: MehVahdJukaar
 */
@Mod(Christmas.MOD_ID)
public class Christmas {
    public static final String MOD_ID = "snowyspirit";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean SUPP = ModList.get().isLoaded("supplementaries");

    public Christmas() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.init(bus);
        bus.addListener(ModSetup::init);
        MinecraftForge.EVENT_BUS.register(this);
        WorldGenHandler.init();
    }

    //TODO: nerf sled acceleration without wolf to make wolf more relevant. can still be used for downhill descent
    //TODO: maybe make friction delend also on xRot to better handle slope descent

    public static boolean isChristmasTime() {
        return true;
    }
}
