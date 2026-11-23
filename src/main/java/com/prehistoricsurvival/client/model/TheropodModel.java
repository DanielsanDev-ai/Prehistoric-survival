package com.prehistoricsurvival.client.model;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.GrowthStage;
import com.prehistoricsurvival.entity.PrehistoricEntity;
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
 * Modelo generico de dinosaurio bipedo carnivoro. La geometria de cada especie (velociraptor, T. rex)
 * viene de TheropodSpecs (generado), asi que el modelo y la textura siempre coinciden.
 * Incluye un modelo de cria con proporciones propias (cabeza grande, patas cortas, sin brazos).
 */
public class TheropodModel<T extends PrehistoricEntity> extends EntityModel<T> {
    public static final ModelLayerLocation RAPTOR_LAYER =
            new ModelLayerLocation(new ResourceLocation(PrehistoricSurvival.MOD_ID, "velociraptor"), "main");
    public static final ModelLayerLocation REX_LAYER =
            new ModelLayerLocation(new ResourceLocation(PrehistoricSurvival.MOD_ID, "tyrannosaurus"), "main");

    private final TheropodSpecs.Spec spec;
    private final ModelPart root, adult, baby;
    private final ModelPart neck, head, jaw, tail1, tail2;
    private final ModelPart thighR, thighL, lowerR, lowerL, footR, footL, armR, armL;
    private final ModelPart bHead, bTail, bLegR, bLegL;

    public TheropodModel(ModelPart root, TheropodSpecs.Spec spec) {
        this.spec = spec;
        this.root = root;
        this.adult = root.getChild("adult");
        this.baby = root.getChild("baby");

        this.neck = adult.getChild("neck");
        this.head = neck.getChild("head");
        this.jaw = head.getChild("jaw");
        this.tail1 = adult.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.thighR = adult.getChild("thigh_r");
        this.thighL = adult.getChild("thigh_l");
        this.lowerR = thighR.getChild("lower_r");
        this.lowerL = thighL.getChild("lower_l");
        this.footR = lowerR.getChild("foot_r");
        this.footL = lowerL.getChild("foot_l");
        this.armR = adult.getChild("arm_r");
        this.armL = adult.getChild("arm_l");

        this.bHead = baby.getChild("b_head");
        this.bTail = baby.getChild("b_tail");
        this.bLegR = baby.getChild("b_leg_r");
        this.bLegL = baby.getChild("b_leg_l");
    }

    public static LayerDefinition createLayer(TheropodSpecs.Spec spec) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Map<String, PartDefinition> defs = new HashMap<>();
        defs.put("adult", root.addOrReplaceChild("adult", CubeListBuilder.create(), PartPose.ZERO));
        defs.put("baby", root.addOrReplaceChild("baby", CubeListBuilder.create(), PartPose.ZERO));
        for (TheropodSpecs.Part p : spec.adult) addPart(defs, p);
        for (TheropodSpecs.Part p : spec.baby) addPart(defs, p);
        return LayerDefinition.create(mesh, spec.texW, spec.texH);
    }

    private static void addPart(Map<String, PartDefinition> defs, TheropodSpecs.Part p) {
        PartDefinition def = defs.get(p.parent).addOrReplaceChild(p.name,
                CubeListBuilder.create().texOffs(p.u, p.v).addBox(p.ox, p.oy, p.oz, p.w, p.h, p.d),
                PartPose.offset(p.px, p.py, p.pz));
        defs.put(p.name, def);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float age,
                          float netHeadYaw, float headPitch) {
        boolean isBaby = entity.getStage() == GrowthStage.BABY;
        adult.visible = !isBaby;
        baby.visible = isBaby;

        EntityAction action = entity.getAction();
        boolean asleep = action == EntityAction.SLEEPING;
        boolean eating = action == EntityAction.EATING;
        boolean threat = action == EntityAction.THREAT;
        boolean crouch = asleep || action == EntityAction.LAYING || entity.isInSittingPose();

        float swing = crouch ? 0f : limbSwing * spec.swingSpeed;
        float amt = crouch ? 0f : limbSwingAmount;
        float s = swing * 0.6662f;
        float yaw = netHeadYaw * Mth.DEG_TO_RAD;
        float pitch = Mth.clamp(headPitch, -35f, 35f) * Mth.DEG_TO_RAD;

        if (isBaby) {
            baby.y = crouch ? 3f : 0f;
            bLegR.visible = !crouch;
            bLegL.visible = !crouch;
            bLegR.xRot = Mth.cos(s) * 1.2f * amt;
            bLegL.xRot = Mth.cos(s + (float) Math.PI) * 1.2f * amt;
            bTail.yRot = Mth.sin(s) * 0.3f * amt + Mth.sin(age * 0.1f) * 0.1f;
            if (asleep) {
                bHead.xRot = 0.5f;
                bHead.yRot = 0f;
            } else if (eating) {
                bHead.xRot = 0.8f + Mth.sin(age * 0.9f) * 0.3f;
                bHead.yRot = 0f;
            } else {
                bHead.xRot = pitch;
                bHead.yRot = yaw;
            }
            return;
        }

        adult.y = crouch ? spec.crouchY : 0f;

        // piernas: postura de dedos (digitigrada) con la rodilla doblada y el pie siempre plano
        if (crouch) {
            thighR.xRot = -1.3f;
            thighL.xRot = -1.3f;
            lowerR.xRot = 2.2f;
            lowerL.xRot = 2.2f;
            footR.xRot = -0.9f;
            footL.xRot = -0.9f;
        } else {
            thighR.xRot = -0.3f + Mth.cos(s) * spec.stride * amt;
            thighL.xRot = -0.3f + Mth.cos(s + (float) Math.PI) * spec.stride * amt;
            lowerR.xRot = 0.6f + Mth.cos(s - 1.0f) * 0.5f * amt;
            lowerL.xRot = 0.6f + Mth.cos(s + (float) Math.PI - 1.0f) * 0.5f * amt;
            footR.xRot = -(thighR.xRot + lowerR.xRot);
            footL.xRot = -(thighL.xRot + lowerL.xRot);
        }

        // brazos y cola
        float armPose = threat ? -1.0f : -0.5f + Mth.sin(age * 0.1f) * 0.04f;
        armR.xRot = armPose;
        armL.xRot = armPose;
        tail1.xRot = threat ? -0.3f : -0.08f;
        tail1.yRot = Mth.sin(s) * 0.2f * amt + Mth.sin(age * 0.08f) * 0.05f;
        tail2.yRot = Mth.sin(s - 0.6f) * 0.3f * amt + Mth.sin(age * 0.08f - 0.5f) * 0.08f;

        // cuello, cabeza y mandibula
        float lean = spec.neckLean;
        if (asleep) {
            neck.xRot = 1.0f;
            head.xRot = 0.3f;
            head.yRot = 0f;
            jaw.xRot = 0f;
        } else if (eating) {
            neck.xRot = lean + 0.7f + Mth.sin(age * 0.8f) * 0.25f;
            head.xRot = -lean * 0.3f;
            head.yRot = 0f;
            jaw.xRot = (Mth.sin(age * 1.6f) + 1f) * 0.25f;
        } else if (threat) {
            neck.xRot = lean + 0.5f;
            head.xRot = -lean + pitch * 0.5f;
            head.yRot = yaw;
            jaw.xRot = 0.55f + Mth.sin(age * 0.5f) * 0.1f;
        } else if (entity.isAggressive()) {
            neck.xRot = lean + 0.3f;
            head.xRot = -lean + pitch;
            head.yRot = yaw;
            jaw.xRot = (Mth.sin(age * 0.9f) + 1f) * 0.3f;
        } else {
            neck.xRot = lean + Mth.cos(s * 2f) * 0.05f * amt;
            head.xRot = -lean + pitch;
            head.yRot = yaw;
            jaw.xRot = 0f;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
