package net.mehvahdjukaar.snowyspirit.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.reg.ClientRegistry;
import net.mehvahdjukaar.snowyspirit.reg.ModSetup;
import net.mehvahdjukaar.snowyspirit.reg.ModTags;
import net.mehvahdjukaar.snowyspirit.reg.ModWorldgenRegistry;
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


        if (PlatHelper.getEnv().isClient()) {
            ClientHelper.addClientSetup(SnowySpiritFabric::initClient);
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
                ModWorldgenRegistry.WILD_GINGER_DENSE.getHolder().unwrapKey().get());
    }


}
