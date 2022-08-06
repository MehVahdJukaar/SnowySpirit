package net.mehvahdjukaar.snowyspirit.common.generation;

import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

public class ConfiguredFeaturesRegistry {

    //helper
    private static RandomPatchConfiguration makeRandomPatch(int tries, int xzSpread, int ySpread, ConfiguredFeature<?, ?> feature, BlockPredicate placementRule) {
        return new RandomPatchConfiguration(tries, xzSpread, ySpread, PlacementUtils.inlinePlaced(Holder.direct(feature),
                BlockPredicateFilter.forPredicate(placementRule)));
    }

    private static final BlockPredicate GINGER_PLACEMENT = BlockPredicate.allOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.wouldSurvive(ModRegistry.GINGER_WILD.get().defaultBlockState(), BlockPos.ZERO)
    );

    public static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> CONFIGURED_WILD_GINGER = FeatureUtils.register("snowy_spirit:wild_ginger", Feature.RANDOM_PATCH,
            makeRandomPatch(40, 4, 1,
                    new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                            new SimpleBlockConfiguration(BlockStateProvider.simple(ModRegistry.GINGER_WILD.get()))),
                    GINGER_PLACEMENT));


      public static Holder<PlacedFeature> WILD_GINGER = PlacementUtils.register("snowy_spirit:wild_ginger", CONFIGURED_WILD_GINGER,
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                RarityFilter.onAverageOnceEvery(20),
                InSquarePlacement.spread(),
                BiomeFilter.biome());

    public static Holder<PlacedFeature> WILD_GINGER_DENSE = PlacementUtils.register("snowy_spirit:wild_ginger_dense", CONFIGURED_WILD_GINGER,
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                RarityFilter.onAverageOnceEvery(10),
                InSquarePlacement.spread(),
                BiomeFilter.biome());




}
