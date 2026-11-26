package com.prehistoricsurvival.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.DilophosaurusEntity;
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
 * Modelo generico de dinosaurio bipedo carnivoro. La geometria de cada especie (velociraptor, T. rex,
 * dilophosaurus) viene de TheropodSpecs (generado), asi que el modelo y la textura siempre coinciden.
 * Tres modelos independientes: adulto, adolescente (proporciones juveniles) y cria (cabeza grande, sin brazos).
 * Las piezas "a_*" (puas y cuernos) solo se muestran en los alfas.
 */
public class TheropodModel<T extends PrehistoricEntity> extends EntityModel<T> {
    public static final ModelLayerLocation RAPTOR_LAYER = layer("velociraptor");
    public static final ModelLayerLocation REX_LAYER = layer("tyrannosaurus");
    public static final ModelLayerLocation DILO_LAYER = layer("dilophosaurus");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(PrehistoricSurvival.MOD_ID, name), "main");
    }

    /** Referencias a las piezas animadas de un modelo (adulto o adolescente). */
    private static final class Rig {
        final ModelPart group;
        final ModelPart neck, neck2, head, jaw, tail1, tail2, tail3;
        final ModelPart thighR, thighL, lowerR, lowerL, footR, footL, armR, armL;
        final ModelPart frillR, frillL;
        final List<ModelPart> alphaParts = new ArrayList<>();

        Rig(Map<String, ModelPart> map, ModelPart group, String pre) {
            this.group = group;
            neck = map.get(pre + "neck");
            neck2 = map.get(pre + "neck2");
            head = map.get(pre + "head");
            jaw = map.get(pre + "jaw");
            tail1 = map.get(pre + "tail1");
            tail2 = map.get(pre + "tail2");
            tail3 = map.get(pre + "tail3");
            thighR = map.get(pre + "thigh_r");
            thighL = map.get(pre + "thigh_l");
            lowerR = map.get(pre + "lower_r");
            lowerL = map.get(pre + "lower_l");
            footR = map.get(pre + "foot_r");
            footL = map.get(pre + "foot_l");
            armR = map.get(pre + "arm_r");
            armL = map.get(pre + "arm_l");
            frillR = map.get(pre + "frill_r");   // null en las especies sin gorguera
            frillL = map.get(pre + "frill_l");
            for (Map.Entry<String, ModelPart> e : map.entrySet()) {
                if (e.getKey().startsWith(pre + "a_")) alphaParts.add(e.getValue());
            }
        }
    }

    private final TheropodSpecs.Spec spec;
    private final ModelPart root;
    private final ModelPart adultGroup, teenGroup, babyGroup;
    private final Rig adultRig, teenRig;
    private final ModelPart bHead, bTail, bLegR, bLegL;

    public TheropodModel(ModelPart root, TheropodSpecs.Spec spec) {
        this.spec = spec;
        this.root = root;
        this.adultGroup = root.getChild("adult");
        this.teenGroup = root.getChild("teen");
        this.babyGroup = root.getChild("baby");

        Map<String, ModelPart> map = new HashMap<>();
        map.put("adult", adultGroup);
        map.put("teen", teenGroup);
        map.put("baby", babyGroup);
        for (TheropodSpecs.Part p : spec.adult) map.put(p.name, map.get(p.parent).getChild(p.name));
        for (TheropodSpecs.Part p : spec.teen) map.put(p.name, map.get(p.parent).getChild(p.name));
        for (TheropodSpecs.Part p : spec.baby) map.put(p.name, map.get(p.parent).getChild(p.name));

        this.adultRig = new Rig(map, adultGroup, "");
        this.teenRig = new Rig(map, teenGroup, "t_");
        this.bHead = map.get("b_head");
        this.bTail = map.get("b_tail");
        this.bLegR = map.get("b_leg_r");
        this.bLegL = map.get("b_leg_l");
    }

    public static LayerDefinition createLayer(TheropodSpecs.Spec spec) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Map<String, PartDefinition> defs = new HashMap<>();
        defs.put("adult", root.addOrReplaceChild("adult", CubeListBuilder.create(), PartPose.ZERO));
        defs.put("teen", root.addOrReplaceChild("teen", CubeListBuilder.create(), PartPose.ZERO));
        defs.put("baby", root.addOrReplaceChild("baby", CubeListBuilder.create(), PartPose.ZERO));
        for (TheropodSpecs.Part p : spec.adult) addPart(defs, p);
        for (TheropodSpecs.Part p : spec.teen) addPart(defs, p);
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
        GrowthStage stage = entity.getStage();
        adultGroup.visible = stage == GrowthStage.ADULT;
        teenGroup.visible = stage == GrowthStage.JUVENILE;
        babyGroup.visible = stage == GrowthStage.BABY;

        EntityAction action = entity.getAction();
        boolean asleep = action == EntityAction.SLEEPING;
        boolean crouch = asleep || action == EntityAction.LAYING || entity.isInSittingPose();
        float swing = crouch ? 0f : limbSwing * spec.swingSpeed;
        float amt = crouch ? 0f : limbSwingAmount;
        float s = swing * 0.6662f;
        float yaw = netHeadYaw * Mth.DEG_TO_RAD;
        float pitch = Mth.clamp(headPitch, -35f, 35f) * Mth.DEG_TO_RAD;

        if (stage == GrowthStage.BABY) {
            babyGroup.y = crouch ? 3f : 0f;
            bLegR.visible = !crouch;
            bLegL.visible = !crouch;
            bLegR.xRot = Mth.cos(s) * 1.2f * amt;
            bLegL.xRot = Mth.cos(s + (float) Math.PI) * 1.2f * amt;
            bTail.yRot = Mth.sin(s) * 0.3f * amt + Mth.sin(age * 0.1f) * 0.1f;
            if (asleep) {
                bHead.xRot = 0.5f;
                bHead.yRot = 0f;
            } else if (action == EntityAction.EATING) {
                bHead.xRot = 0.8f + Mth.sin(age * 0.9f) * 0.3f;
                bHead.yRot = 0f;
            } else {
                bHead.xRot = pitch;
                bHead.yRot = yaw;
            }
            return;
        }

        Rig rig = stage == GrowthStage.ADULT ? adultRig : teenRig;
        animate(rig, entity, action, crouch, s, amt, age, yaw, pitch);
    }

    private void animate(Rig r, T entity, EntityAction action, boolean crouch, float s, float amt,
                         float age, float yaw, float pitch) {
        boolean asleep = action == EntityAction.SLEEPING;
        boolean eating = action == EntityAction.EATING;
        boolean threat = action == EntityAction.THREAT;
        boolean spitting = action == EntityAction.SPITTING;

        r.group.y = crouch ? spec.crouchY : 0f;
        for (ModelPart part : r.alphaParts) part.visible = entity.isAlpha();

        // ---- patas: postura de dedos (digitigrada), rodilla doblada y pie siempre plano
        if (crouch) {
            r.thighR.xRot = -1.3f;
            r.thighL.xRot = -1.3f;
            r.lowerR.xRot = 2.2f;
            r.lowerL.xRot = 2.2f;
            r.footR.xRot = -0.9f;
            r.footL.xRot = -0.9f;
        } else {
            r.thighR.xRot = -0.3f + Mth.cos(s) * spec.stride * amt;
            r.thighL.xRot = -0.3f + Mth.cos(s + (float) Math.PI) * spec.stride * amt;
            r.lowerR.xRot = 0.6f + Mth.cos(s - 1.0f) * 0.5f * amt;
            r.lowerL.xRot = 0.6f + Mth.cos(s + (float) Math.PI - 1.0f) * 0.5f * amt;
            r.footR.xRot = -(r.thighR.xRot + r.lowerR.xRot);
            r.footL.xRot = -(r.thighL.xRot + r.lowerL.xRot);
        }

        // ---- brazos y cola (la cola ondula con un retraso por segmento)
        float armPose = threat ? -1.0f : -0.5f + Mth.sin(age * 0.1f) * 0.04f;
        r.armR.xRot = armPose;
        r.armL.xRot = armPose;
        r.tail1.xRot = threat || spitting ? -0.3f : -0.08f;
        r.tail1.yRot = Mth.sin(s) * 0.2f * amt + Mth.sin(age * 0.08f) * 0.05f;
        r.tail2.yRot = Mth.sin(s - 0.6f) * 0.3f * amt + Mth.sin(age * 0.08f - 0.5f) * 0.08f;
        r.tail3.yRot = Mth.sin(s - 1.2f) * 0.35f * amt + Mth.sin(age * 0.08f - 1.0f) * 0.1f;

        // ---- cuello en S, cabeza y mandibula
        float n1 = spec.neck1, n2 = spec.neck2, hr = spec.headRest;
        float bob = Mth.cos(s * 2f) * 0.04f * amt;
        if (asleep) {
            r.neck.xRot = 1.1f;
            r.neck2.xRot = -0.2f;
            r.head.xRot = 0.4f;
            r.head.yRot = 0f;
            r.jaw.xRot = 0f;
        } else if (eating) {
            r.neck.xRot = n1 + 0.6f + Mth.sin(age * 0.8f) * 0.2f;
            r.neck2.xRot = n2 + 0.3f;
            r.head.xRot = hr + 0.5f;
            r.head.yRot = 0f;
            r.jaw.xRot = (Mth.sin(age * 1.6f) + 1f) * 0.25f;
        } else if (spitting) {
            r.neck.xRot = n1 - 0.3f;                      // se echa hacia atras y suelta el veneno
            r.neck2.xRot = n2 - 0.2f;
            r.head.xRot = hr - 0.1f + Mth.sin(age * 0.8f) * 0.05f;
            r.head.yRot = yaw;
            r.jaw.xRot = 0.8f;
        } else if (threat) {
            r.neck.xRot = n1 + 0.3f;
            r.neck2.xRot = n2 - 0.1f;
            r.head.xRot = hr + pitch * 0.5f;
            r.head.yRot = yaw;
            r.jaw.xRot = 0.55f + Mth.sin(age * 0.5f) * 0.1f;
        } else if (entity.isAggressive()) {
            r.neck.xRot = n1 + 0.2f;
            r.neck2.xRot = n2;
            r.head.xRot = hr + pitch;
            r.head.yRot = yaw;
            r.jaw.xRot = (Mth.sin(age * 0.9f) + 1f) * 0.3f;
        } else {
            r.neck.xRot = n1 + bob;
            r.neck2.xRot = n2;
            r.head.xRot = hr + pitch;
            r.head.yRot = yaw;
            r.jaw.xRot = 0f;
        }

        // ---- gorguera del dilophosaurus: se despliega con suavidad y vibra al estar abierta
        if (r.frillR != null && r.frillL != null) {
            float amount = 0f;
            if (entity instanceof DilophosaurusEntity dilo) amount = dilo.getFrill(age - entity.tickCount);
            boolean open = amount > 0.03f;
            r.frillR.visible = open;
            r.frillL.visible = open;
            float angle = Mth.lerp(amount, 1.45f, 0.1f) + Mth.sin(age * 1.5f) * 0.06f * amount;
            r.frillR.yRot = angle;
            r.frillL.yRot = -angle;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
