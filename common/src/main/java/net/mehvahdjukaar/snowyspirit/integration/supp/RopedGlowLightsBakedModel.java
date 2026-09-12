package net.mehvahdjukaar.snowyspirit.integration.supp;

import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RopedGlowLightsBakedModel implements CustomBakedModel {
    private final BakedModel overlay;
    private final BlockModelShaper blockModelShaper;

    public RopedGlowLightsBakedModel(BakedModel overlay, ModelState state) {
        this.overlay = overlay;
        this.blockModelShaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, RandomSource rand,
                                         RenderType renderType, ExtraModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();
        if (state != null) {
            try {
                BlockState rope = SuppCompat.glowLightsToRope(state);
                quads.addAll(blockModelShaper.getBlockModel(rope).getQuads(rope, side, rand));
            } catch (Exception ignored) {
            }
        }
        quads.addAll(this.overlay.getQuads(state, side, rand));
        return quads;
    }

    @Override
    public TextureAtlasSprite getBlockParticle(ExtraModelData data) {
        return this.overlay.getParticleIcon();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
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

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }
}
