package net.mehvahdjukaar.snowyspirit.reg;

import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModWorldgenRegistry {

    public static void init() {
    }

    //helper
    private static RandomPatchConfiguration makeRandomPatch(int tries, int xzSpread, int ySpread, ConfiguredFeature<?, ?> feature, BlockPredicate placementRule) {
        return new RandomPatchConfiguration(tries, xzSpread, ySpread, PlacementUtils.inlinePlaced(Holder.direct(feature),
                BlockPredicateFilter.forPredicate(placementRule)));
    }


    public static RegSupplier<ConfiguredFeature<RandomPatchConfiguration, Feature<RandomPatchConfiguration>>> CONFIGURED_WILD_GINGER =
            RegHelper.registerConfiguredFeature(SnowySpirit.res("wild_ginger"),
                    () -> Feature.RANDOM_PATCH,
                    () -> makeRandomPatch(40, 4, 1,
                            new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                                    new SimpleBlockConfiguration(BlockStateProvider.simple(ModRegistry.GINGER_WILD.get()))),
                            BlockPredicate.allOf(
                                    BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                    BlockPredicate.wouldSurvive(ModRegistry.GINGER_WILD.get().defaultBlockState(), BlockPos.ZERO)
                            )));


    public static RegSupplier<PlacedFeature> WILD_GINGER =
            RegHelper.registerPlacedFeature(SnowySpirit.res("wild_ginger"),
                    CONFIGURED_WILD_GINGER,
                    () -> List.of(
                            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                            RarityFilter.onAverageOnceEvery(20),
                            InSquarePlacement.spread(),
                            BiomeFilter.biome()));

    public static RegSupplier<PlacedFeature> WILD_GINGER_DENSE =
            RegHelper.registerPlacedFeature(SnowySpirit.res("wild_ginger_dense"),
                    CONFIGURED_WILD_GINGER,
                    () -> List.of(
                            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                            RarityFilter.onAverageOnceEvery(10),
                            InSquarePlacement.spread(),
                            BiomeFilter.biome()));


}
