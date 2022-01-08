package net.mehvahdjukaar.snowyspirit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.snowyspirit.entity.SledEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SledModel3<T extends SledEntity> extends EntityModel<T> {

    private final ModelPart sled;

    public SledModel3(ModelPart root) {
        this.sled = root.getChild("sled");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition front = partdefinition.addOrReplaceChild("sled", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 4.0F, 3.5F, 0.0F, 3.1416F, 0.0F));

        PartDefinition skiis = front.addOrReplaceChild("skiis", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 4.5F, 1.5708F, 1.5708F, 0.0F));

        PartDefinition slednew = skiis.addOrReplaceChild("slednew", CubeListBuilder.create().texOffs(0, 6).addBox(-15.5F, -9.0F, 8.0F, 31.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 12).addBox(-9.5F, -10.0F, 14.0F, 24.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-15.5F, 2.0F, 8.0F, 31.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -10.0F));

        PartDefinition supports = front.addOrReplaceChild("supports", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, -5.5F));

        PartDefinition left = supports.addOrReplaceChild("left", CubeListBuilder.create().texOffs(92, 6).addBox(-7.0F, -5.0F, 22.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(104, 6).addBox(-7.0F, -6.0F, 14.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(116, 6).addBox(-7.0F, -6.0F, -2.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right = supports.addOrReplaceChild("right", CubeListBuilder.create().texOffs(92, 0).addBox(4.0F, -5.0F, 22.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(104, 0).addBox(4.0F, -6.0F, 14.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(116, 0).addBox(4.0F, -6.0F, -2.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        sled.render(poseStack, buffer, packedLight, packedOverlay);
    }
}