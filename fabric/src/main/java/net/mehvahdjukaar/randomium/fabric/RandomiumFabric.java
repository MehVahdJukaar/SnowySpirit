package net.mehvahdjukaar.randomium.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.RandomiumClient;
import net.mehvahdjukaar.randomium.world.ModFeatures;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

public class RandomiumFabric implements ModInitializer {

    public static final String MOD_ID = Randomium.MOD_ID;


    @Override
    public void onInitialize() {

        Randomium.commonInit();
        FabricSetupCallbacks.COMMON_SETUP.add(RandomiumFabric::commonSetup);
        FabricSetupCallbacks.CLIENT_SETUP.add(RandomiumClient::init);
    }

    private static void commonSetup() {
        Randomium.commonSetup();
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_OVERWORLD),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ModFeatures.RANDOMIUM_ORE_PLACED.unwrapKey().get());
    }


}
