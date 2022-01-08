package net.mehvahdjukaar.snowyspirit.init;

import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.client.SledModel;
import net.mehvahdjukaar.snowyspirit.client.SledModel2;
import net.mehvahdjukaar.snowyspirit.client.SledModel3;
import net.mehvahdjukaar.snowyspirit.client.SleighEntityRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Christmas.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static ModelLayerLocation SLED_MODEL = loc("sled");
    public static ModelLayerLocation SLED_MODEL2 = loc("sled2");
    public static ModelLayerLocation SLED_MODEL3 = loc("sled3");

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(Christmas.res(name), name);
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SLED_MODEL, SledModel::createBodyLayer);
        event.registerLayerDefinition(SLED_MODEL2, SledModel2::createBodyLayer);
        event.registerLayerDefinition(SLED_MODEL3, SledModel3::createBodyLayer);
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.SLED.get(), SleighEntityRenderer::new);
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.GINGER_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.WREATH.get(), RenderType.cutout());
        for(var v : ModRegistry.GUMDROPS_BUTTON.values()){
            ItemBlockRenderTypes.setRenderLayer(v.get(), RenderType.translucent());
        }

    }


}
