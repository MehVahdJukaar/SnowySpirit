package net.mehvahdjukaar.snowboundspirit.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.snowboundspirit.entity.SledEntity;
import net.mehvahdjukaar.snowboundspirit.init.ClientSetup;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.stream.Stream;

public class SleighEntityRenderer extends EntityRenderer<SledEntity> {

    private final Map<Boat.Type,ResourceLocation> textures;
    private final SledModel<SledEntity> model;
    private final SledModel2<SledEntity> model2;

    public SleighEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
        this.model = new SledModel<>(context.bakeLayer(ClientSetup.SLED_MODEL));
        this.model2 = new SledModel2<>(context.bakeLayer(ClientSetup.SLED_MODEL2));
        this.textures = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap((e) -> e,
                (t) -> new ResourceLocation("snowboundspirit:textures/entity/sled/" + t.getName() + ".png")));
    }

    @Override
    public void render(SledEntity sleigh, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.375D + Mth.lerp(partialTicks, sleigh.prevAdditionalY, sleigh.additionalY), 0.0D);
        //same stuff that happens to yRot when its created
        float xRot = Mth.lerp(partialTicks, sleigh.xRotO, sleigh.getXRot());

        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yRot));
        poseStack.mulPose(Vector3f.XN.rotationDegrees(xRot));
        float f = (float) sleigh.getHurtTime() - partialTicks;
        float f1 = sleigh.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) sleigh.getHurtDir()));
        }

        float f2 = 0;
        if (!Mth.equal(f2, 0.0F)) {
            poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), 0, true));
        }


        ResourceLocation resourcelocation =  new ResourceLocation("snowboundspirit:textures/entity/sled/oak.png");
                //this.getTextureLocation(sleigh);
        //poseStack.translate(0,1,0);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        //poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        //boatmodel.setupAnim(sleigh, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        Model model = this.model;
        //poseStack.translate(0,-1.125,0);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(model.renderType(resourcelocation));
        model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(sleigh, yRot, partialTicks, poseStack, bufferSource, light);

        if(this.entityRenderDispatcher.shouldRenderHitBoxes()) {
            this.renderDebugHitbox(poseStack, bufferSource.getBuffer(RenderType.lines()), sleigh, partialTicks);
        }
    }

   @Override
    public ResourceLocation getTextureLocation(SledEntity sled) {
        return this.textures.get(sled.getWoodType());
    }

    private void renderDebugHitbox(PoseStack pMatrixStack, VertexConsumer pBuffer, SledEntity pEntity, float pPartialTicks) {
        AABB aabb = pEntity.getBoundingBox().move(lerpV(pPartialTicks, pEntity.prevProjectedPos, pEntity.projectedPos))
                .move(-pEntity.getX(), -pEntity.getY(), -pEntity.getZ());
        LevelRenderer.renderLineBox(pMatrixStack, pBuffer, aabb, 1.0F, 0, 0, 1.0F);


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
                .normal(matrix3f, (float) 0, (float) 1, (float) 0).endVertex();
        pBuffer.vertex(matrix4f, (float) 0, (float) ((double) eye + 0.25f + pEntity.maxUpStep), (float) 0)
                .color(255, 0, 255, 255)
                .normal(matrix3f, (float) 0, (float) 1, (float) 0).endVertex();


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

}
