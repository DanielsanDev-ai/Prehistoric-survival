package com.prehistoricsurvival.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.DilophosaurusEntity;
import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.GrowthStage;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Modelo propio: dos crestas craneales y gola bilateral que se abre en postura de amenaza. */
public class DilophosaurusModel extends EntityModel<DilophosaurusEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(PrehistoricSurvival.MOD_ID, "dilophosaurus"), "main");

    private final ModelPart root, adult, baby;
    private final ModelPart neck, head, jaw, tail, legR, legL, armR, armL, frillR, frillL;
    private final ModelPart bHead, bLegR, bLegL, bTail;

    public DilophosaurusModel(ModelPart root) {
        this.root = root;
        adult = root.getChild("adult");
        baby = root.getChild("baby");
        neck = adult.getChild("neck");
        head = neck.getChild("head");
        jaw = head.getChild("jaw");
        tail = adult.getChild("tail");
        legR = adult.getChild("leg_r");
        legL = adult.getChild("leg_l");
        armR = adult.getChild("arm_r");
        armL = adult.getChild("arm_l");
        frillR = adult.getChild("frill_r");
        frillL = adult.getChild("frill_l");
        bHead = baby.getChild("b_head");
        bLegR = baby.getChild("b_leg_r");
        bLegL = baby.getChild("b_leg_l");
        bTail = baby.getChild("b_tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition adult = root.addOrReplaceChild("adult", CubeListBuilder.create(), PartPose.ZERO);
        adult.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.5f, -3.5f, -6f, 7f, 7f, 12f), PartPose.offset(0, 13, 1));
        PartDefinition neck = adult.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(40, 0)
                .addBox(-2f, -4f, -2f, 4f, 5f, 4f), PartPose.offset(0, 11, -5));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(58, 0)
                .addBox(-2.5f, -3f, -3f, 5f, 4f, 5f)
                .texOffs(58, 10).addBox(-1.5f, -2f, -7f, 3f, 2f, 5f), PartPose.offset(0, -4, -1));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(78, 0)
                .addBox(-1.5f, 0, -5f, 3f, 1f, 5f), PartPose.offset(0, 0, 0));
        head.addOrReplaceChild("crest_l", CubeListBuilder.create().texOffs(82, 12)
                .addBox(0.2f, -5f, -1.5f, 1.5f, 3f, 2f), PartPose.offset(0, 0, 0));
        head.addOrReplaceChild("crest_r", CubeListBuilder.create().texOffs(90, 12)
                .addBox(-1.7f, -5f, -1.5f, 1.5f, 3f, 2f), PartPose.offset(0, 0, 0));

        adult.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 22)
                .addBox(-1.5f, -1.5f, 0, 3f, 3f, 11f), PartPose.offset(0, 13, 6));
        adult.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(30, 22)
                .addBox(-1f, 0, -1f, 2f, 7f, 3f), PartPose.offset(-2.2f, 16, 2));
        adult.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(40, 22)
                .addBox(-1f, 0, -1f, 2f, 7f, 3f), PartPose.offset(2.2f, 16, 2));
        adult.addOrReplaceChild("arm_r", CubeListBuilder.create().texOffs(50, 22)
                .addBox(-0.5f, 0, -1f, 1f, 4f, 2f), PartPose.offset(-3f, 11, -3));
        adult.addOrReplaceChild("arm_l", CubeListBuilder.create().texOffs(56, 22)
                .addBox(-0.5f, 0, -1f, 1f, 4f, 2f), PartPose.offset(3f, 11, -3));
        adult.addOrReplaceChild("frill_r", CubeListBuilder.create().texOffs(66, 22)
                .addBox(-5f, -3f, -0.5f, 5f, 7f, 1f), PartPose.offsetAndRotation(-1.5f, 10, -4, 0, 0, -0.12f));
        adult.addOrReplaceChild("frill_l", CubeListBuilder.create().texOffs(78, 22)
                .addBox(0f, -3f, -0.5f, 5f, 7f, 1f), PartPose.offsetAndRotation(1.5f, 10, -4, 0, 0, 0.12f));

        PartDefinition baby = root.addOrReplaceChild("baby", CubeListBuilder.create(), PartPose.ZERO);
        baby.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 40)
                .addBox(-3f, -3f, -4f, 6f, 6f, 8f), PartPose.offset(0, 17, 0));
        baby.addOrReplaceChild("b_head", CubeListBuilder.create().texOffs(28, 40)
                .addBox(-2.5f, -4f, -3f, 5f, 5f, 5f)
                .texOffs(48, 40).addBox(-1.5f, -3f, -6f, 3f, 2f, 4f)
                .texOffs(62, 40).addBox(-1.5f, -7f, -1.5f, 3f, 3f, 2f), PartPose.offset(0, 14, -3));
        baby.addOrReplaceChild("b_tail", CubeListBuilder.create().texOffs(0, 54)
                .addBox(-1f, -1f, 0, 2f, 2f, 7f), PartPose.offset(0, 17, 3));
        baby.addOrReplaceChild("b_leg_r", CubeListBuilder.create().texOffs(20, 54)
                .addBox(-1f, 0, -1f, 2f, 4f, 2f), PartPose.offset(-1.5f, 20, 0));
        baby.addOrReplaceChild("b_leg_l", CubeListBuilder.create().texOffs(30, 54)
                .addBox(-1f, 0, -1f, 2f, 4f, 2f), PartPose.offset(1.5f, 20, 0));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(DilophosaurusEntity entity, float limbSwing, float limbSwingAmount,
                          float age, float netHeadYaw, float headPitch) {
        boolean isBaby = entity.getStage() == GrowthStage.BABY;
        adult.visible = !isBaby;
        baby.visible = isBaby;
        boolean threat = entity.isAggressive() || entity.getAction() == EntityAction.THREAT;
        float swing = limbSwing * 0.85f;
        float amount = limbSwingAmount;
        float s = swing * 0.6662f;

        if (isBaby) {
            bLegR.xRot = Mth.cos(s) * 1.1f * amount;
            bLegL.xRot = Mth.cos(s + (float)Math.PI) * 1.1f * amount;
            bTail.yRot = Mth.sin(s) * 0.25f * amount;
            bHead.xRot = Mth.clamp(headPitch, -30f, 30f) * Mth.DEG_TO_RAD;
            bLegR.visible = !entity.isInSittingPose();
            bLegL.visible = !entity.isInSittingPose();
            return;
        }

        float flare = threat ? 0.82f : 0.04f;
        frillR.zRot = Mth.lerp(0.22f, frillR.zRot, -flare);
        frillL.zRot = Mth.lerp(0.22f, frillL.zRot, flare);
        legR.xRot = Mth.cos(s) * 1.15f * amount;
        legL.xRot = Mth.cos(s + (float)Math.PI) * 1.15f * amount;
        armR.xRot = -0.35f + Mth.sin(age * 0.08f) * 0.04f;
        armL.xRot = armR.xRot;
        tail.yRot = Mth.sin(s) * 0.22f * amount + Mth.sin(age * 0.07f) * 0.04f;
        neck.xRot = threat ? 0.28f : Mth.cos(s * 2f) * 0.05f * amount;
        head.xRot = headPitch * Mth.DEG_TO_RAD + (threat ? -0.12f : 0f);
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        jaw.xRot = threat ? 0.38f + Mth.sin(age * 0.35f) * 0.08f : 0f;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
