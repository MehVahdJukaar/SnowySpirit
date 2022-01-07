package net.mehvahdjukaar.snowboundspirit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.snowboundspirit.entity.SledEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SledModel2<T extends SledEntity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor

    private final ModelPart bone;

    public SledModel2(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
                .texOffs(0, 12).addBox(-16.0F, -9.0F, 5.0F, 32.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(56, 56).addBox(-2.0F, -8.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-14.0F, -2.0F, 5.0F, 28.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 6).addBox(-16.0F, -9.0F, -8.0F, 32.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 21).addBox(-16.0F, -9.0F, -5.0F, 25.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(85, 6).addBox(14.0F, -8.0F, -5.0F, 1.0F, 0.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(16, 40).addBox(12.0F, -8.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(6.0F, -8.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(0.0F, -8.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(-6.0F, -8.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(-12.0F, -8.0F, 5.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(-12.0F, -8.0F, -7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(-6.0F, -8.0F, -7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(0.0F, -8.0F, -7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 40).addBox(6.0F, -8.0F, -7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 40).addBox(12.0F, -8.0F, -7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-14.0F, -2.0F, -7.0F, 28.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, buffer, packedLight, packedOverlay);
    }
}