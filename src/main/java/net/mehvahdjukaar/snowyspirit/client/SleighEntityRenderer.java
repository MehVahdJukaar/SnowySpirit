package net.mehvahdjukaar.snowyspirit.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.mehvahdjukaar.snowyspirit.init.ClientSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.stream.Stream;

public class SleighEntityRenderer extends EntityRenderer<SledEntity> {

    private final Map<Boat.Type,ResourceLocation> textures;
    private final SledModel<SledEntity> model;
    private final SledModel2<SledEntity> model2;
    private final SledModel3<SledEntity> model3;

    public SleighEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
        this.model = new SledModel<>(context.bakeLayer(ClientSetup.SLED_MODEL));
        this.model2 = new SledModel2<>(context.bakeLayer(ClientSetup.SLED_MODEL2));
        this.model3 = new SledModel3<>(context.bakeLayer(ClientSetup.SLED_MODEL3));
        this.textures = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap((e) -> e,
                (t) -> new ResourceLocation(Christmas.MOD_ID+":textures/entity/sled/" + t.getName() + ".png")));
    }

    @Override
    public void render(SledEntity sled, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.375D + Mth.lerp(partialTicks, sled.prevAdditionalY, sled.additionalY), 0.0D);
        //same stuff that happens to yRot when its created
        float xRot = Mth.lerp(partialTicks, sled.xRotO, sled.getXRot());

        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yRot));
        poseStack.mulPose(Vector3f.XN.rotationDegrees(xRot));
        float f = (float) sled.getHurtTime() - partialTicks;
        float f1 = sled.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) sled.getHurtDir()));
        }

        poseStack.pushPose();
        poseStack.translate(-0.5, 0.125, -0.4125);

        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(Blocks.RED_CARPET.defaultBlockState(), poseStack, bufferSource, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        //float f2 = 0;
        //if (!Mth.equal(f2, 0.0F)) {
            //poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), 0, true));
       // }


        ResourceLocation resourcelocation = this.getTextureLocation(sled);
        //poseStack.translate(0,1,0);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        //poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        //boatmodel.setupAnim(sled, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        Model model = this.model3;
        //poseStack.translate(0,-1.125,0);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(model.renderType(resourcelocation));
        model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);


        poseStack.popPose();
        super.render(sled, yRot, partialTicks, poseStack, bufferSource, light);

        if(this.entityRenderDispatcher.shouldRenderHitBoxes()) {
            this.renderDebugHitbox(poseStack, bufferSource.getBuffer(RenderType.lines()), sled, partialTicks);
        }

        this.renderLeash(sled,partialTicks, poseStack, bufferSource);
    }

   @Override
    public ResourceLocation getTextureLocation(SledEntity sled) {
        return this.textures.get(sled.getWoodType());
    }

    private void renderDebugHitbox(PoseStack pMatrixStack, VertexConsumer pBuffer, SledEntity pEntity, float pPartialTicks) {
        AABB aabb = pEntity.getBoundingBox().move(lerpV(pPartialTicks, pEntity.prevProjectedPos, pEntity.projectedPos))
                .move(-pEntity.getX(), -pEntity.getY(), -pEntity.getZ());
        LevelRenderer.renderLineBox(pMatrixStack, pBuffer, aabb, 1.0F, 0, 0, 1.0F);

        aabb = pEntity.pullerAABB.move(-pEntity.getX(), -pEntity.getY(), -pEntity.getZ());

        LevelRenderer.renderLineBox(pMatrixStack, pBuffer, aabb, 0, 1, 0, 1.0F);

        Vec3 movement = lerpV(pPartialTicks, pEntity.prevDeltaMovement, pEntity.getDeltaMovement());

        Matrix4f matrix4f = pMatrixStack.last().pose();
        Matrix3f matrix3f = pMatrixStack.last().normal();
        float mult = 6;
        float eye = (float) (pEntity.getEyeHeight() + 1 + pEntity.additionalY);
        pBuffer.vertex(matrix4f, 0.0F, eye, 0.0F)
                .color(0, 255, 0, 255)
                .normal(matrix3f, (float) movement.x, (float) movement.y, (float) movement.z).endVertex();
        pBuffer.vertex(matrix4f, (float) (movement.x * mult), (float) ((double) eye + movement.y * mult), (float) (movement.z * mult))
                .color(0, 255, 0, 255)
                .normal(matrix3f, (float) movement.x, (float) movement.y, (float) movement.z).endVertex();


        pBuffer.vertex(matrix4f, 0.0F, eye + 0.25f, 0.0F)
                .color(255, 0, 255, 255)
                .normal(matrix3f, 0, 1, 0).endVertex();
        pBuffer.vertex(matrix4f,  0, (float) (eye + 0.25f + pEntity.misalignedFrictionFactor), 0)
                .color(255, 0, 255, 255)
                .normal(matrix3f, 0, 1, 0).endVertex();



        if(pEntity.boost) {
            movement = movement.normalize().scale(-1);
            pBuffer.vertex(matrix4f, 0.0F, eye, 0.0F)
                    .color(255, 255, 0, 255)
                    .normal(matrix3f, (float) movement.x, (float) movement.y, (float) movement.z).endVertex();
            pBuffer.vertex(matrix4f, (float) (movement.x), (float) ((double) eye + movement.y), (float) (movement.z))
                    .color(255, 255, 0, 255)
                    .normal(matrix3f, (float) movement.x, (float) movement.y, (float) movement.z).endVertex();
        }


    }

    public static Vec3 lerpV(float delta, Vec3 start, Vec3 end) {
        return new Vec3(
                Mth.lerp(delta, start.x, end.x),
                Mth.lerp(delta, start.y, end.y),
                Mth.lerp(delta, start.z, end.z));
    }



    private void renderLeash(SledEntity sled, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer) {
        Entity wolf = sled.getWolf();
        if(wolf != null) {
            poseStack.pushPose();
            Vec3 vec3 = wolf.getRopeHoldPosition(pPartialTicks);
            double d0 = (double) (Mth.lerp(pPartialTicks, sled.getYRot(), sled.yRotO) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
            Vec3 vec31 = sled.getLeashOffset();
            double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
            double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
            double d3 = Mth.lerp(pPartialTicks, sled.xo, sled.getX()) + d1;
            double d4 = Mth.lerp(pPartialTicks, sled.yo, sled.getY()) + vec31.y;
            double d5 = Mth.lerp(pPartialTicks, sled.zo, sled.getZ()) + d2;
            poseStack.translate(d1, vec31.y, d2);
            float f = (float) (vec3.x - d3);
            float f1 = (float) (vec3.y - d4);
            float f2 = (float) (vec3.z - d5);
            float f3 = 0.025F;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.leash());
            Matrix4f matrix4f = poseStack.last().pose();
            float f4 = Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
            float f5 = f2 * f4;
            float f6 = f * f4;
            BlockPos blockpos = new BlockPos(sled.getEyePosition(pPartialTicks));
            BlockPos blockpos1 = new BlockPos(wolf.getEyePosition(pPartialTicks));
            int i = this.getBlockLightLevel(sled, blockpos);
            int j = sled.level.getBrightness(LightLayer.BLOCK, blockpos1);
            int k = sled.level.getBrightness(LightLayer.SKY, blockpos);
            int l = sled.level.getBrightness(LightLayer.SKY, blockpos1);

            for (int i1 = 0; i1 <= 24; ++i1) {
                addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
            }

            for (int j1 = 24; j1 >= 0; --j1) {
                addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
            }

            poseStack.popPose();
        }
    }

    private static void addVertexPair(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float)p_174321_ / 24.0F;
        int i = (int)Mth.lerp(f, (float)p_174313_, (float)p_174314_);
        int j = (int)Mth.lerp(f, (float)p_174315_, (float)p_174316_);
        int k = LightTexture.pack(i, j);
        float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = 0.5F * f1;
        float f3 = 0.4F * f1;
        float f4 = 0.3F * f1;
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.vertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
        p_174308_.vertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
    }

}
