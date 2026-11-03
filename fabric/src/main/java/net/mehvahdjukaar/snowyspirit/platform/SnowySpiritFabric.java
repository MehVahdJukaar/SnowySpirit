package net.mehvahdjukaar.snowyspirit.platform;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.client.WreathRenderer;
import net.mehvahdjukaar.snowyspirit.common.wreath.WreathHandler;
import net.mehvahdjukaar.snowyspirit.configs.CommonConfigs;
import net.mehvahdjukaar.snowyspirit.reg.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;

public class SnowySpiritFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        SnowySpirit.commonInit();

        PlatHelper.addCommonSetup(SnowySpiritFabric::commonSetup);

        if (PlatHelper.getPhysicalSide().isClient()) {
            WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
                WreathRenderer.renderAllWreaths(context.matrixStack());
            });
        }

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
                WreathHandler.onRightClickBlock(player, world, player.getItemInHand(hand), hitResult.getBlockPos()));
    }


    private static void commonSetup() {
        if (!CommonConfigs.GINGER.get()) return;

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.HAS_GINGER),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ResourceKey.create(Registries.PLACED_FEATURE, SnowySpirit.res("wild_ginger")));

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.HAS_GINGER_DENSE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ResourceKey.create(Registries.PLACED_FEATURE, SnowySpirit.res("wild_ginger_dense")));
    }


}
