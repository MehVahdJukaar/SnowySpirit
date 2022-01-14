package net.mehvahdjukaar.snowyspirit.common.generation;

import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class ConfiguredFeaturesRegistry {

    //helper
    private static RandomPatchConfiguration makeRandomPatch(int tries, int xzSpread, int ySpread, ConfiguredFeature<?, ?> feature, BlockPredicate placementRule) {
        return new RandomPatchConfiguration(tries, xzSpread, ySpread, () -> feature.filtered(placementRule));
    }


    private static final BlockPredicate FLAX_PLACEMENT = BlockPredicate.allOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.wouldSurvive(ModRegistry.GINGER_WILD.get().defaultBlockState(), BlockPos.ZERO)
    );


    //configured features

    public static final ConfiguredFeature<RandomPatchConfiguration, ?> WILD_GINGER_PATCH = Feature.RANDOM_PATCH.configured(
            makeRandomPatch(40, 4, 1,
                    Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(BlockStateProvider.simple(ModRegistry.GINGER_WILD.get()))),
                    FLAX_PLACEMENT));


    //placed features

    public static final PlacedFeature PLACED_WILD_GINGER_PATCH = WILD_GINGER_PATCH.placed(
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            RarityFilter.onAverageOnceEvery(20),
            InSquarePlacement.spread(),
            BiomeFilter.biome());


    public static final PlacedFeature PLACED_WILD_GINGER_PATCH_DENSE = WILD_GINGER_PATCH.placed(
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            RarityFilter.onAverageOnceEvery(6),
            InSquarePlacement.spread(),
            BiomeFilter.biome());


    /**
     * Registers the configured structure which is what gets added to the biomes.
     * Noticed we are not using a forge registry because there is none for configured structures.
     * <p>
     * We can register configured structures at any time before a world is clicked on and made.
     * But the best time to register configured features by code is honestly to do it in FMLCommonSetupEvent.
     */
    protected static void registerFeatures() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                Christmas.res("wild_ginger"), WILD_GINGER_PATCH);


        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                Christmas.res("wild_ginger"), PLACED_WILD_GINGER_PATCH);

        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                Christmas.res("wild_ginger_dense"), PLACED_WILD_GINGER_PATCH_DENSE);

    }


}
