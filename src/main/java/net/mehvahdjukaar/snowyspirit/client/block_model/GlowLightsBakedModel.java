package net.mehvahdjukaar.snowyspirit.client.block_model;

import net.mehvahdjukaar.snowyspirit.common.block.GlowLightsBlockTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GlowLightsBakedModel implements IDynamicBakedModel {
    private final BlockModelShaper blockModelShaper;
    private final BakedModel overlay;
    private final boolean translucent;

    public GlowLightsBakedModel(BakedModel overlay, boolean translucent) {
        this.overlay = overlay;
        this.translucent = translucent;
        this.blockModelShaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        //always on cutout layer
        List<BakedQuad> quads = new ArrayList<>();
        if (state != null) {
            RenderType layer = MinecraftForgeClient.getRenderType();
            boolean onTranslucent = layer == RenderType.translucent();

            //always cutout
            if (!onTranslucent) {
                try {
                    BlockState mimic = extraData.getData(GlowLightsBlockTile.MIMIC);
                    if (mimic != null && !mimic.isAir()) {
                        BakedModel model = blockModelShaper.getBlockModel(mimic);

                        quads.addAll(model.getQuads(mimic, side, rand, EmptyModelData.INSTANCE));
                    }

                } catch (Exception ignored) {
                }
            }

            //need to be added later so they go ontop
            if (onTranslucent == translucent) {
                quads.addAll(overlay.getQuads(state, side, rand, EmptyModelData.INSTANCE));
            }
        }

        return quads;
    }


    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    private static final ResourceLocation emptyRes = new ResourceLocation("oak_leaves");

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(emptyRes);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data) {
        BlockState mimic = data.getData(GlowLightsBlockTile.MIMIC);
        if (mimic != null && !mimic.isAir()) {

            BakedModel model = blockModelShaper.getBlockModel(mimic);
            try {
                return model.getParticleIcon();
            } catch (Exception ignored) {
            }

        }
        return getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }
}
