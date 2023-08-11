package net.mehvahdjukaar.snowyspirit.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.reg.ClientRegistry;
import net.mehvahdjukaar.snowyspirit.reg.ModSetup;
import net.mehvahdjukaar.snowyspirit.reg.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.GenerationStep;

public class SnowySpiritFabric implements ModInitializer {

    public static final String MOD_ID = SnowySpirit.MOD_ID;

    @Override
    public void onInitialize() {

        SnowySpirit.commonInit();

        PlatHelper.addCommonSetup(SnowySpiritFabric::commonSetup);
    }


    private static void commonSetup() {
        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.HAS_GINGER),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ResourceKey.create(Registries.PLACED_FEATURE, SnowySpirit.res("wild_ginger")));

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.HAS_GINGER_DENSE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ResourceKey.create(Registries.PLACED_FEATURE, SnowySpirit.res("wild_ginger_dense")));
    }


}
