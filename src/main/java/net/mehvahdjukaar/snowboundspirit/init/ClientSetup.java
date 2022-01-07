package net.mehvahdjukaar.snowboundspirit.init;

import net.mehvahdjukaar.snowboundspirit.Christmas;
import net.mehvahdjukaar.snowboundspirit.client.SledModel;
import net.mehvahdjukaar.snowboundspirit.client.SledModel2;
import net.mehvahdjukaar.snowboundspirit.client.SleighEntityRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
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

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(Christmas.res(name), name);
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SLED_MODEL, SledModel::createBodyLayer);
        event.registerLayerDefinition(SLED_MODEL2, SledModel2::createBodyLayer);
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.SLEIGH.get(), SleighEntityRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public static void init(final FMLClientSetupEvent event) {

    }


}
