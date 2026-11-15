package net.mehvahdjukaar.snowyspirit.client;

import com.google.common.collect.ImmutableList;
import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class GingyModel extends HumanoidModel<GingyEntity> {

    public static final float HOP_TICKS = 5;

    private final ModelPart bodyEaten;
    private final float scale;
    private float cartwheelWeight;

    public GingyModel(ModelPart modelPart, float scale) {
        super(modelPart);
        this.bodyEaten = modelPart.getChild("body_eaten");
        this.scale = scale;
    }

    @Override
    public void prepareMobModel(GingyEntity entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);

        int b = entity.getBodyIntegrity().ordinal();
        this.leftArm.visible = b < 1;
        this.rightArm.visible = b < 2;
        this.head.visible = b < 3;
        this.body.visible = b < 4;
        this.bodyEaten.visible = !this.body.visible;
        this.cartwheelWeight = cartwheelPoseWeight(entity.getCartwheelProgress(partialTick));
    }

    public static float cartwheelPoseWeight(float progress) {
        return Mth.clamp(Mth.sin(Mth.PI * progress) * 2.5f, 0, 1);
    }

    // legs are too short to keep up when chasing
    public static float hopWeight(float limbSwingAmount, float scale) {
        return Mth.clamp((limbSwingAmount / scale - 0.65f) / 0.15f, 0, 1);
    }

    @Override
    public void setupAnim(GingyEntity entity, float walkAnimation, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        boolean flyingTicks = entity.getFallFlyingTicks() > 4;
        float f = 1.0F;
        if (flyingTicks) {
            f = (float) entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        float period = GingyEntity.walkCyclePeriod(scale);
        float angle = walkAnimation * (Mth.TWO_PI / period);
        float hop = hopWeight(limbSwingAmount, scale);

        float armSwingPower = 2 + hop;
        float cos = Mth.lerp(hop, Mth.cos(angle), Mth.cos(ageInTicks * (Mth.PI / HOP_TICKS)));
        float legSwing = GingyEntity.WALK_LEG_SWING * Mth.clamp(limbSwingAmount / 0.3f, 0, 1) * (1 + 0.3f * hop);
        float walkCycleClamped = cos * Mth.clamp(limbSwingAmount, 0, 0.4f);

        this.head.zRot = walkCycleClamped * 0.3f;

        this.rightArm.yRot = walkCycleClamped * armSwingPower / f;
        this.leftArm.yRot = walkCycleClamped * armSwingPower / f;

        float multiplier = 1.6f * Mth.clamp(1 - limbSwingAmount * 4, 0, 1);
        float zRot = multiplier * (Mth.cos(ageInTicks * 0.09F) * 0.05F);
        this.rightArm.zRot = zRot;
        this.leftArm.zRot = -zRot;
        this.rightArm.xRot = 0.005F;
        this.leftArm.xRot = 0.005F;


        this.rightLeg.xRot = cos * legSwing / f;
        this.leftLeg.xRot = -cos * legSwing / f;

        this.rightLeg.yRot = 0.005F;
        this.leftLeg.yRot = -0.005F;


        float legZRot = 0.2f;
        this.rightLeg.zRot = (0.005F + legZRot);
        this.leftLeg.zRot = -(0.005F + legZRot);

        if (cartwheelWeight > 0) {
            float w = cartwheelWeight;
            this.rightArm.zRot = Mth.lerp(w, this.rightArm.zRot, 0.1f);
            this.leftArm.zRot = Mth.lerp(w, this.leftArm.zRot, -0.1f);
            this.rightArm.yRot = Mth.lerp(w, this.rightArm.yRot, 0);
            this.leftArm.yRot = Mth.lerp(w, this.leftArm.yRot, 0);
            this.rightLeg.zRot = Mth.lerp(w, this.rightLeg.zRot, 0.65f);
            this.leftLeg.zRot = Mth.lerp(w, this.leftLeg.zRot, -0.65f);
            this.rightLeg.xRot = Mth.lerp(w, this.rightLeg.xRot, 0);
            this.leftLeg.xRot = Mth.lerp(w, this.leftLeg.xRot, 0);
            this.head.zRot *= 1 - w;
        }

        if (this.riding || entity.isOrderedToSit()) {
            this.rightLeg.xRot = -Mth.HALF_PI + 0.05f;
            this.rightLeg.yRot = 0.31415927F;
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -Mth.HALF_PI + 0.05f;
            this.leftLeg.yRot = -0.31415927F;
            this.leftLeg.zRot = -0.07853982F;
        }
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.bodyEaten);
    }

    public static LayerDefinition createBodyLayer() {
        CubeDeformation cubeDeformation = CubeDeformation.NONE;
        float hOffset = 11;
        MeshDefinition meshDefinition = HumanoidModel.createMesh(cubeDeformation, hOffset);
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
       var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -5.0F, -1.5F, 6.0F, 5.0F, 3.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F + hOffset, 0.0F));

       head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(11, 10)
               .addBox(-4.0F, -4.0F, -2.5F, 8.0F, 0.0F, 5.0F), PartPose.ZERO);

       var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8)
                .addBox(-3.0F, 0.0F, -1.0F, 6.0F, 7.0F, 2.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F + hOffset, 0.0F));

       body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(12, 28)
               .addBox(-4.0F, 7, -2.0F, 8.0F, 0.0F, 4.0F), PartPose.ZERO);

       partDefinition.addOrReplaceChild("body_eaten", CubeListBuilder.create().texOffs(0, 27)
                .addBox(-3.0F, 0.0F, -1.0F, 6.0F, 3.0F, 2.0F, cubeDeformation), PartPose.offset(0.0F, 4.0F + hOffset, 0.0F));
        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 17)
                .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0,0,-0.001f)), PartPose.offset(-1.5F, 7.0F + hOffset, 0.0F));
        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(10, 17)
                .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0,0,-0.001f)), PartPose.offset(1.5F, 7.0F + hOffset, 0.0F));

        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(20, 0)
                .addBox(-4.0F, -1.5F, -1.0F, 4.0F, 3.0F, 2.0F, cubeDeformation), PartPose.offset(-3.0F, 1.5F + hOffset, 0.0F));
        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(20, 5)
                .addBox(0F, -1.5F, -1.0F, 4.0F, 3.0F, 2.0F, cubeDeformation), PartPose.offset(3.0F, 1.5F + hOffset, 0.0F));


        return LayerDefinition.create(meshDefinition, 32, 32);
    }

}
