package com.prehistoricsurvival.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.DodoEntity;
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

/**
 * Modelo del Dodo, escrito a mano. Contiene DOS modelos independientes:
 *  - "adult": usado en juveniles y adultos.
 *  - "baby":  bebe con proporciones propias (cabeza grande, patas cortas, sin cola).
 * Las coordenadas de textura (texOffs) deben coincidir con tools/generate_assets.py.
 */
public class DodoModel extends EntityModel<DodoEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(PrehistoricSurvival.MOD_ID, "dodo"), "main");

    private final ModelPart root;
    private final ModelPart adult, teen, baby;
    // adulto
    private final ModelPart neck, head, legR, legL, wingR, wingL, tail, crest;
    // adolescente
    private final ModelPart tNeck, tHead, tLegR, tLegL, tWingR, tWingL, tTail, tCrest;
    // bebe
    private final ModelPart bHead, bLegR, bLegL, bWingR, bWingL;

    public DodoModel(ModelPart root) {
        this.root = root;
        this.adult = root.getChild("adult");
        this.teen = root.getChild("teen");
        this.baby = root.getChild("baby");

        this.neck = adult.getChild("neck");
        this.head = neck.getChild("head");
        this.crest = head.getChild("crest");
        this.legR = adult.getChild("leg_right");
        this.legL = adult.getChild("leg_left");
        this.wingR = adult.getChild("wing_right");
        this.wingL = adult.getChild("wing_left");
        this.tail = adult.getChild("tail");

        this.tNeck = teen.getChild("neck");
        this.tHead = tNeck.getChild("head");
        this.tCrest = tHead.getChild("crest");
        this.tLegR = teen.getChild("leg_right");
        this.tLegL = teen.getChild("leg_left");
        this.tWingR = teen.getChild("wing_right");
        this.tWingL = teen.getChild("wing_left");
        this.tTail = teen.getChild("tail");

        this.bHead = baby.getChild("head");
        this.bLegR = baby.getChild("leg_right");
        this.bLegL = baby.getChild("leg_left");
        this.bWingR = baby.getChild("wing_right");
        this.bWingL = baby.getChild("wing_left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ---------------- ADULTO (el suelo esta en y = 24)
        PartDefinition adult = root.addOrReplaceChild("adult", CubeListBuilder.create(), PartPose.ZERO);
        adult.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4f, -4f, -5f, 8f, 8f, 10f), PartPose.offset(0f, 14f, 0f));
        PartDefinition neck = adult.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-1.5f, -5f, -1.5f, 3f, 5f, 3f), PartPose.offset(0f, 11f, -4f));
        neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-2.5f, -4f, -2.5f, 5f, 4f, 5f)
                .texOffs(20, 28).addBox(-1.5f, -3f, -6.5f, 3f, 3f, 4f), PartPose.offset(0f, -5f, 0f))
                .addOrReplaceChild("crest", CubeListBuilder.create()
                        .texOffs(50, 89).addBox(-1.5f, -3f, -1f, 3f, 3f, 2f), PartPose.offset(0f, -4f, -0.5f));
        adult.addOrReplaceChild("leg_right", CubeListBuilder.create()
                .texOffs(0, 40).addBox(-1f, 0f, -1f, 2f, 6f, 2f), PartPose.offset(-2.5f, 18f, 0.5f));
        adult.addOrReplaceChild("leg_left", CubeListBuilder.create()
                .texOffs(8, 40).addBox(-1f, 0f, -1f, 2f, 6f, 2f), PartPose.offset(2.5f, 18f, 0.5f));
        adult.addOrReplaceChild("wing_right", CubeListBuilder.create()
                .texOffs(0, 50).addBox(-1f, 0f, -3f, 1f, 5f, 6f), PartPose.offset(-4f, 11f, -1f));
        adult.addOrReplaceChild("wing_left", CubeListBuilder.create()
                .texOffs(14, 50).addBox(0f, 0f, -3f, 1f, 5f, 6f), PartPose.offset(4f, 11f, -1f));
        adult.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-3f, -2f, 0f, 6f, 4f, 3f), PartPose.offsetAndRotation(0f, 11f, 5f, -0.35f, 0f, 0f));

        // ---------------- ADOLESCENTE: cuerpo mas esbelto, cuello largo, cabeza grande y patas largas
        PartDefinition teen = root.addOrReplaceChild("teen", CubeListBuilder.create(), PartPose.ZERO);
        teen.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 64).addBox(-3.5f, -3.5f, -4.5f, 7f, 7f, 9f), PartPose.offset(0f, 13.5f, 0f));
        PartDefinition tneck = teen.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(32, 64).addBox(-1.5f, -6f, -1.5f, 3f, 6f, 3f), PartPose.offset(0f, 10.5f, -3.5f));
        tneck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(44, 64).addBox(-2.5f, -5f, -2.5f, 5f, 5f, 5f)
                .texOffs(0, 80).addBox(-1.5f, -3.5f, -6.5f, 3f, 3f, 4f), PartPose.offset(0f, -6f, 0f))
                .addOrReplaceChild("crest", CubeListBuilder.create()
                        .texOffs(50, 89).addBox(-1.5f, -3f, -1f, 3f, 3f, 2f), PartPose.offset(0f, -5f, -0.5f));
        teen.addOrReplaceChild("leg_right", CubeListBuilder.create()
                .texOffs(14, 80).addBox(-1f, 0f, -1f, 2f, 7f, 2f), PartPose.offset(-2.5f, 17f, 0.5f));
        teen.addOrReplaceChild("leg_left", CubeListBuilder.create()
                .texOffs(22, 80).addBox(-1f, 0f, -1f, 2f, 7f, 2f), PartPose.offset(2.5f, 17f, 0.5f));
        teen.addOrReplaceChild("wing_right", CubeListBuilder.create()
                .texOffs(30, 80).addBox(-1f, 0f, -2.5f, 1f, 4f, 5f), PartPose.offset(-3.5f, 11f, -1f));
        teen.addOrReplaceChild("wing_left", CubeListBuilder.create()
                .texOffs(42, 80).addBox(0f, 0f, -2.5f, 1f, 4f, 5f), PartPose.offset(3.5f, 11f, -1f));
        teen.addOrReplaceChild("tail", CubeListBuilder.create()
                .texOffs(0, 89).addBox(-2.5f, -1.5f, 0f, 5f, 3f, 2f), PartPose.offsetAndRotation(0f, 11f, 4.5f, -0.3f, 0f, 0f));

        // ---------------- BEBE
        PartDefinition baby = root.addOrReplaceChild("baby", CubeListBuilder.create(), PartPose.ZERO);
        baby.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(34, 36).addBox(-3.5f, -3.5f, -4f, 7f, 7f, 8f), PartPose.offset(0f, 16.5f, 0f));
        baby.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(34, 52).addBox(-3f, -6f, -3f, 6f, 6f, 6f)
                .texOffs(22, 36).addBox(-1f, -3.5f, -6f, 2f, 2f, 3f), PartPose.offset(0f, 13.5f, -3.5f));
        baby.addOrReplaceChild("leg_right", CubeListBuilder.create()
                .texOffs(16, 42).addBox(-1f, 0f, -1f, 2f, 4f, 2f), PartPose.offset(-2f, 20f, 0.5f));
        baby.addOrReplaceChild("leg_left", CubeListBuilder.create()
                .texOffs(24, 42).addBox(-1f, 0f, -1f, 2f, 4f, 2f), PartPose.offset(2f, 20f, 0.5f));
        baby.addOrReplaceChild("wing_right", CubeListBuilder.create()
                .texOffs(54, 0).addBox(-1f, 0f, -2f, 1f, 3f, 4f), PartPose.offset(-3.5f, 14.5f, -0.5f));
        baby.addOrReplaceChild("wing_left", CubeListBuilder.create()
                .texOffs(36, 8).addBox(0f, 0f, -2f, 1f, 3f, 4f), PartPose.offset(3.5f, 14.5f, -0.5f));

        return LayerDefinition.create(mesh, 64, 96);
    }

    @Override
    public void setupAnim(DodoEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        GrowthStage stage = entity.getStage();
        adult.visible = stage == GrowthStage.ADULT;
        teen.visible = stage == GrowthStage.JUVENILE;
        baby.visible = stage == GrowthStage.BABY;

        EntityAction action = entity.getAction();
        boolean asleep = action == EntityAction.SLEEPING;
        boolean eating = action == EntityAction.EATING;
        boolean crouch = asleep || action == EntityAction.LAYING || entity.isInSittingPose();

        float swing = crouch ? 0f : limbSwing;
        float amount = crouch ? 0f : limbSwingAmount;
        float legA = Mth.cos(swing * 0.6662f) * 1.4f * amount;
        float legB = Mth.cos(swing * 0.6662f + (float) Math.PI) * 1.4f * amount;
        float flap = !entity.onGround() ? Mth.sin(ageInTicks * 1.2f) * 0.9f + 0.7f : 0.05f;
        float yaw = netHeadYaw * Mth.DEG_TO_RAD;
        float pitch = headPitch * Mth.DEG_TO_RAD;

        if (stage == GrowthStage.BABY) {
            baby.y = crouch ? 3f : 0f;
            bLegR.visible = !crouch;
            bLegL.visible = !crouch;
            bLegR.xRot = legA;
            bLegL.xRot = legB;
            bWingR.zRot = flap;
            bWingL.zRot = -flap;
            if (asleep) {
                bHead.xRot = 0.5f;
                bHead.yRot = 2.4f;
            } else if (eating) {
                bHead.xRot = 0.9f + Mth.sin(ageInTicks * 0.9f) * 0.35f;
                bHead.yRot = 0f;
            } else {
                bHead.xRot = pitch;
                bHead.yRot = yaw;
            }
            return;
        }

        boolean isTeen = stage == GrowthStage.JUVENILE;
        ModelPart group = isTeen ? teen : adult;
        ModelPart n = isTeen ? tNeck : neck;
        ModelPart h = isTeen ? tHead : head;
        ModelPart lr = isTeen ? tLegR : legR;
        ModelPart ll = isTeen ? tLegL : legL;
        ModelPart wr = isTeen ? tWingR : wingR;
        ModelPart wl = isTeen ? tWingL : wingL;
        ModelPart t = isTeen ? tTail : tail;
        (isTeen ? tCrest : crest).visible = entity.isAlpha();   // cresta de plumas: solo los alfas

        group.y = crouch ? (isTeen ? 5f : 4f) : 0f;
        lr.visible = !crouch;
        ll.visible = !crouch;
        lr.xRot = legA;
        ll.xRot = legB;
        wr.zRot = flap;
        wl.zRot = -flap;
        t.yRot = Mth.sin(swing * 0.6662f) * 0.3f * amount + Mth.sin(ageInTicks * 0.1f) * 0.05f;

        if (asleep) {
            n.xRot = 0.9f;
            h.xRot = 0.5f;
            h.yRot = 2.4f;
        } else if (eating) {
            n.xRot = 0.9f + Mth.sin(ageInTicks * 0.9f) * 0.35f;
            h.xRot = 0.5f;
            h.yRot = 0f;
        } else {
            n.xRot = Mth.cos(swing * 0.6662f * 2f) * 0.08f * amount;
            h.xRot = pitch;
            h.yRot = yaw;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
