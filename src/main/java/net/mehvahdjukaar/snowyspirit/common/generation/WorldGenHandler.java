package net.mehvahdjukaar.snowyspirit.common.generation;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

import static net.minecraftforge.common.BiomeDictionary.Type.JUNGLE;
import static net.minecraftforge.common.BiomeDictionary.Type.SPARSE;

public class WorldGenHandler {


    public static void init() {
        // For registration and init stuff.

        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EventPriority.NORMAL, WorldGenHandler::addStuffToBiomes);
    }

    /**
     * Here, setupStructures will be ran after registration of all structures are finished.
     * This is important to be done here so that the Deferred Registry has already ran and
     * registered/created our structure for us.
     * <p>
     * Once after that structure instance is made, we then can now do the rest of the setup
     * that requires a structure instance such as setting the structure spacing, creating the
     * configured structure instance, and more.
     */
    public static void setup(final FMLCommonSetupEvent event) {
        ConfiguredFeaturesRegistry.registerFeatures();
    }


    public static void addStuffToBiomes(BiomeLoadingEvent event) {

        Biome.BiomeCategory category = event.getCategory();
        if (category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND && category != Biome.BiomeCategory.NONE) {


            ResourceLocation res = event.getName();
            if (res != null && category != Biome.BiomeCategory.UNDERGROUND) {

                ResourceKey<Biome> key = ResourceKey.create(ForgeRegistries.Keys.BIOMES, res);
                Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);
                if (types.contains(JUNGLE)) {
                    if (types.contains(SPARSE)) {
                        event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.WILD_GINGER_DENSE);
                    } else {
                        event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ConfiguredFeaturesRegistry.WILD_GINGER);
                    }
                }
            }

        }
    }

}
