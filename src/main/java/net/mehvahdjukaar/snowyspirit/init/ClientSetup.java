package net.mehvahdjukaar.snowyspirit.init;

import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.client.ContainerHolderEntityRenderer;
import net.mehvahdjukaar.snowyspirit.client.QuiltModel;
import net.mehvahdjukaar.snowyspirit.client.SledEntityRenderer;
import net.mehvahdjukaar.snowyspirit.client.SledModel;
import net.mehvahdjukaar.snowyspirit.client.block_model.GlowLightsModelLoader;
import net.mehvahdjukaar.snowyspirit.common.block.GlowLightsBlockTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Christmas.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static ModelLayerLocation SLED_MODEL = loc("sled");
    public static ModelLayerLocation QUILT_MODEL = loc("quilt");

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(Christmas.res(name), name);
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SLED_MODEL, SledModel::createBodyLayer);
        event.registerLayerDefinition(QUILT_MODEL, QuiltModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.SLED.get(), SledEntityRenderer::new);
        event.registerEntityRenderer(ModRegistry.CONTAINER_ENTITY.get(), ContainerHolderEntityRenderer::new);
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.GINGER_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.GINGER_WILD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.SNOW_GLOBE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.WREATH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.GINGER_POT.get(), RenderType.cutout());
        for (var v : ModRegistry.GUMDROPS_BUTTONS.values()) {
            ItemBlockRenderTypes.setRenderLayer(v.get(), RenderType.translucent());
        }
        for (var v : ModRegistry.GLOW_LIGHTS_BLOCKS.values()) {
            ItemBlockRenderTypes.setRenderLayer(v.get(), r -> r == RenderType.translucent() || r == RenderType.cutout());
        }

        ItemProperties.register(ModRegistry.GINGERBREAD_COOKIE.get(), new ResourceLocation("shape"),
                (stack, world, entity, s) -> entity == null ? 0 : System.identityHashCode(stack) % 4);

    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        //loaders
        ModelLoaderRegistry.registerLoader(Christmas.res("glow_lights_loader"), new GlowLightsModelLoader());
    }


    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register(new MimicBlockColor(), ModRegistry.GLOW_LIGHTS_BLOCKS.values().stream()
                .map(RegistryObject::get).toArray(Block[]::new));

    }

    public static class MimicBlockColor implements BlockColor {

        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tint) {
            return col(state, world, pos, tint);
        }

        public static int col(BlockState state, BlockAndTintGetter level, BlockPos pos, int tint) {
            if (level != null && pos != null) {
                if (level.getBlockEntity(pos) instanceof GlowLightsBlockTile tile) {
                    BlockState mimic = tile.mimic;
                    if (mimic != null && !mimic.hasBlockEntity()) {
                        return Minecraft.getInstance().getBlockColors().getColor(mimic, level, pos, tint);
                    }
                }
            }
            return -1;
        }

        public static class noParticle implements BlockColor {
            @Override
            public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tint) {
                if (tint == 0) return -1;
                return col(state, world, pos, tint);
            }
        }
    }


}
