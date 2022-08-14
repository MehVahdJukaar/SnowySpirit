package net.mehvahdjukaar.snowyspirit.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.reg.ClientRegistry;
import net.mehvahdjukaar.snowyspirit.reg.ModSetup;
import net.mehvahdjukaar.snowyspirit.reg.ModTags;
import net.mehvahdjukaar.snowyspirit.reg.ModWorldgenRegistry;
import net.minecraft.world.level.levelgen.GenerationStep;

public class SnowySpiritFabric implements ModInitializer {

    public static final String MOD_ID = SnowySpirit.MOD_ID;


    @Override
    public void onInitialize() {

        SnowySpirit.commonInit();

        FabricSetupCallbacks.COMMON_SETUP.add(SnowySpiritFabric::commonSetup);


        if (PlatformHelper.getEnv().isClient()) {
            FabricSetupCallbacks.CLIENT_SETUP.add(SnowySpiritFabric::initClient);
        }
    }

    private static void initClient() {
        ClientRegistry.init();
        ClientRegistry.setup();
    }

    private static void commonSetup() {
        ModSetup.setup();

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.HAS_GINGER),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModWorldgenRegistry.WILD_GINGER.getHolder().unwrapKey().get());

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.HAS_GINGER_DENSE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ModWorldgenRegistry.WILD_GINGER.getHolder().unwrapKey().get());
    }


}
