package net.mehvahdjukaar.snowyspirit.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE;

public class SammysParticleHacks {

    public static MultiBufferSource.BufferSource DELAYED_RENDER;
    public static Map<RenderType, BufferBuilder> BUFFERS = new HashMap<>();

    public static Matrix4f PARTICLE_MATRIX;

    public static void init() {
        int size = PlatformHelper.isModLoaded("rubidium") ? 262144 : 256;
        DELAYED_RENDER = MultiBufferSource.immediateWithBuffers(BUFFERS, new BufferBuilder(size));
    }

    public static VertexConsumer getMagicBuffer(){
        return DELAYED_RENDER.getBuffer(ADDITIVE_PARTICLE);
    }

    public static void renderLast(PoseStack poseStack) {
        poseStack.pushPose();
        var c =            Minecraft.getInstance().gameRenderer.getMainCamera();
        var b =getMagicBuffer();
        if (true) {


            RenderSystem.getModelViewStack().pushPose();
            RenderSystem.getModelViewStack().setIdentity();
            if (PARTICLE_MATRIX != null) {
                var m = new Matrix4f();
                 m.translate(new Vector3f(
                        (float) c.getPosition().x(), (float) c.getPosition().y(), (float) c.getPosition().z()
                ));
                RenderSystem.getModelViewStack().mulPoseMatrix(m);
            }
            RenderSystem.applyModelViewMatrix();
            DELAYED_RENDER.endBatch(ADDITIVE_PARTICLE);
            RenderSystem.getModelViewStack().popPose();
            RenderSystem.applyModelViewMatrix();

            endBatches(DELAYED_RENDER, BUFFERS);

        }

        poseStack.popPose();
    }

    private static void endBatches(MultiBufferSource.BufferSource source, Map<RenderType, BufferBuilder> buffers) {
        for (RenderType type : buffers.keySet()) {
            source.endBatch(type);
        }
        source.endBatch();
    }

    public static final RenderType ADDITIVE_PARTICLE = RenderTypeAccessor.createGenericRenderType(
            SnowySpirit.res("addittive_particle"),
            PARTICLE, VertexFormat.Mode.QUADS, RenderStateShardAccessor.SHARD, RenderStateShardAccessor.getAdditiveTransparency(), TextureAtlas.LOCATION_PARTICLES);


    public abstract static class RenderStateShardAccessor extends RenderStateShard{


        @NotNull
        public static RenderStateShard.TransparencyStateShard getAdditiveTransparency() {
            return RenderStateShard.ADDITIVE_TRANSPARENCY;
        }

        public static final RenderStateShard.ShaderStateShard SHARD = new RenderStateShard.ShaderStateShard(GameRenderer::getParticleShader);

        protected RenderStateShardAccessor(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    public abstract static class RenderTypeAccessor extends RenderType{

        protected RenderTypeAccessor(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
            super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
        }

        /**
         * Creates a custom render type and creates a buffer builder for it.
         */
        public static RenderType createGenericRenderType(ResourceLocation name, VertexFormat format, VertexFormat.Mode mode,
                                                         ShaderStateShard shader, TransparencyStateShard transparency, ResourceLocation texture) {
            return RenderType.create(
                    name.toString(), format, mode, 256, CompositeState.builder()
                            .setShaderState(shader)
                            .setWriteMaskState(new WriteMaskStateShard(true, true))
                            .setLightmapState(new LightmapStateShard(false))
                            .setTransparencyState(transparency)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setCullState(new CullStateShard(true))
                            .createCompositeState(true)
            );
        }
    }

}
