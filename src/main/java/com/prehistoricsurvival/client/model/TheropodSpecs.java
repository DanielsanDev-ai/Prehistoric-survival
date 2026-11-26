// ARCHIVO GENERADO por tools/theropods.py - NO EDITAR A MANO.
package com.prehistoricsurvival.client.model;

/** Geometria y coordenadas de textura de los teropodos (adulto, adolescente y bebe). */
public final class TheropodSpecs {
    public static final class Part {
        public final String name, parent;
        public final int u, v, w, h, d;
        public final float ox, oy, oz, px, py, pz;
        Part(String name, String parent, int u, int v, float ox, float oy, float oz, int w, int h, int d,
             float px, float py, float pz) {
            this.name = name; this.parent = parent; this.u = u; this.v = v;
            this.ox = ox; this.oy = oy; this.oz = oz; this.w = w; this.h = h; this.d = d;
            this.px = px; this.py = py; this.pz = pz;
        }
    }

    public static final class Spec {
        public final int texW, texH;
        public final float stride, swingSpeed, neck1, neck2, headRest, crouchY;
        public final Part[] adult, teen, baby;
        Spec(int texW, int texH, float stride, float swingSpeed, float neck1, float neck2, float headRest,
             float crouchY, Part[] adult, Part[] teen, Part[] baby) {
            this.texW = texW; this.texH = texH; this.stride = stride; this.swingSpeed = swingSpeed;
            this.neck1 = neck1; this.neck2 = neck2; this.headRest = headRest; this.crouchY = crouchY;
            this.adult = adult; this.teen = teen; this.baby = baby;
        }
    }

    public static final Spec VELOCIRAPTOR = new Spec(128, 64, 0.9f, 1f, 0.6f, -0.5f, 0.1f, 5f,
        new Part[] {
            new Part("body", "adult", 0, 0, -2.5f, -3f, -5f, 5, 6, 10, 0f, 12f, 0f),
            new Part("neck", "adult", 96, 16, -1.5f, -4f, -1.5f, 3, 4, 3, 0f, 10.5f, -4.5f),
            new Part("neck2", "neck", 76, 25, -1f, -4f, -1f, 2, 4, 2, 0f, -4f, 0f),
            new Part("head", "neck2", 108, 16, -2f, -3f, -2f, 4, 3, 4, 0f, -4f, 0f),
            new Part("snout", "head", 84, 25, -1.5f, -2.5f, -6f, 3, 2, 4, 0f, 0f, 0f),
            new Part("jaw", "head", 44, 32, -1.5f, 0f, -4f, 3, 1, 4, 0f, -0.5f, -2f),
            new Part("brow_r", "head", 24, 38, -0.5f, -0.5f, -1f, 1, 1, 3, -2f, -3f, -1f),
            new Part("brow_l", "head", 32, 38, -0.5f, -0.5f, -1f, 1, 1, 3, 2f, -3f, -1f),
            new Part("tail1", "adult", 48, 16, -1.5f, -1.5f, 0f, 3, 3, 5, 0f, 11.5f, 5f),
            new Part("tail2", "tail1", 0, 25, -1f, -1f, 0f, 2, 2, 5, 0f, 0f, 5f),
            new Part("tail3", "tail2", 98, 25, -2f, -0.5f, 0f, 4, 1, 5, 0f, 0f, 5f),
            new Part("thigh_r", "adult", 74, 0, -1.5f, 0f, -2f, 3, 5, 4, -3f, 13f, 1f),
            new Part("lower_r", "thigh_r", 14, 25, -1f, 0f, -1f, 2, 5, 2, 0f, 5f, 1f),
            new Part("foot_r", "lower_r", 58, 32, -1f, 0f, -3f, 2, 1, 4, 0f, 5f, 0f),
            new Part("claw_r", "foot_r", 8, 43, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_r", "adult", 40, 38, -0.5f, 0f, -0.5f, 1, 3, 1, -2.5f, 12f, -3.5f),
            new Part("hand_r", "arm_r", 12, 43, -0.5f, 0f, -0.5f, 1, 2, 1, 0f, 3f, 0f),
            new Part("plume_r", "arm_r", 22, 25, -0.5f, 0f, 0.5f, 1, 3, 4, 0f, 0f, 0f),
            new Part("thigh_l", "adult", 88, 0, -1.5f, 0f, -2f, 3, 5, 4, 3f, 13f, 1f),
            new Part("lower_l", "thigh_l", 32, 25, -1f, 0f, -1f, 2, 5, 2, 0f, 5f, 1f),
            new Part("foot_l", "lower_l", 70, 32, -1f, 0f, -3f, 2, 1, 4, 0f, 5f, 0f),
            new Part("claw_l", "foot_l", 16, 43, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_l", "adult", 44, 38, -0.5f, 0f, -0.5f, 1, 3, 1, 2.5f, 12f, -3.5f),
            new Part("hand_l", "arm_l", 20, 43, -0.5f, 0f, -0.5f, 1, 2, 1, 0f, 3f, 0f),
            new Part("plume_l", "arm_l", 40, 25, -0.5f, 0f, 0.5f, 1, 3, 4, 0f, 0f, 0f),
            new Part("a_spike_1", "body", 48, 38, -0.5f, -2f, -1f, 1, 2, 2, 0f, -3f, -3f),
            new Part("a_spike_2", "body", 54, 38, -0.5f, -2f, -1f, 1, 2, 2, 0f, -3f, 1f),
            new Part("a_spike_3", "tail1", 60, 38, -0.5f, -2f, -1f, 1, 2, 2, 0f, -1.5f, 2f),
            new Part("a_horn_r", "head", 66, 38, -0.5f, -3f, -0.5f, 1, 3, 1, -1.5f, -3f, -1.5f),
            new Part("a_horn_l", "head", 70, 38, -0.5f, -3f, -0.5f, 1, 3, 1, 1.5f, -3f, -1.5f)
        },
        new Part[] {
            new Part("t_body", "teen", 30, 0, -2.25f, -2.7f, -4.25f, 4, 5, 8, 0f, 11f, 0f),
            new Part("t_neck", "teen", 50, 25, -1.425f, -3.6f, -1.425f, 3, 4, 3, 0f, 9.5f, -3.825f),
            new Part("t_neck2", "t_neck", 116, 25, -0.95f, -3.6f, -0.95f, 2, 4, 2, 0f, -3.6f, 0f),
            new Part("t_head", "t_neck2", 102, 0, -2.5f, -3.75f, -2.4f, 5, 4, 5, 0f, -3.6f, 0f),
            new Part("t_snout", "t_head", 82, 32, -1.5f, -2.875f, -4.8f, 3, 2, 3, 0f, 0f, 0f),
            new Part("t_jaw", "t_head", 74, 38, -1.5f, 0f, -3.2f, 3, 1, 3, 0f, -0.625f, -2.4f),
            new Part("t_brow_r", "t_head", 86, 38, -0.6f, -0.6f, -1f, 1, 1, 3, -2.5f, -3.75f, -1.2f),
            new Part("t_brow_l", "t_head", 94, 38, -0.6f, -0.6f, -1f, 1, 1, 3, 2.5f, -3.75f, -1.2f),
            new Part("t_tail1", "teen", 62, 25, -1.35f, -1.35f, 0f, 3, 3, 4, 0f, 10.5f, 4.25f),
            new Part("t_tail2", "t_tail1", 0, 32, -0.9f, -0.9f, 0f, 2, 2, 4, 0f, 0f, 4f),
            new Part("t_tail3", "t_tail2", 94, 32, -1.8f, -0.45f, 0f, 4, 1, 4, 0f, 0f, 4f),
            new Part("t_thigh_r", "teen", 0, 16, -1.35f, 0f, -1.8f, 3, 5, 4, -2.7f, 12f, 0.85f),
            new Part("t_lower_r", "t_thigh_r", 64, 16, -0.9f, 0f, -0.9f, 2, 6, 2, 0f, 5f, 0.9f),
            new Part("t_foot_r", "t_lower_r", 110, 32, -1f, 0f, -2.85f, 2, 1, 4, 0f, 6f, 0f),
            new Part("t_claw_r", "t_foot_r", 24, 43, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("t_arm_r", "teen", 102, 38, -0.55f, 0f, -0.55f, 1, 3, 1, -2.25f, 11f, -2.975f),
            new Part("t_hand_r", "t_arm_r", 28, 43, -0.55f, 0f, -0.55f, 1, 2, 1, 0f, 3.3f, 0f),
            new Part("t_plume_r", "t_arm_r", 12, 32, -0.5f, 0f, 0.4f, 1, 3, 3, 0f, 0f, 0f),
            new Part("t_thigh_l", "teen", 14, 16, -1.35f, 0f, -1.8f, 3, 5, 4, 2.7f, 12f, 0.85f),
            new Part("t_lower_l", "t_thigh_l", 72, 16, -0.9f, 0f, -0.9f, 2, 6, 2, 0f, 5f, 0.9f),
            new Part("t_foot_l", "t_lower_l", 0, 38, -1f, 0f, -2.85f, 2, 1, 4, 0f, 6f, 0f),
            new Part("t_claw_l", "t_foot_l", 32, 43, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("t_arm_l", "teen", 106, 38, -0.55f, 0f, -0.55f, 1, 3, 1, 2.25f, 11f, -2.975f),
            new Part("t_hand_l", "t_arm_l", 36, 43, -0.55f, 0f, -0.55f, 1, 2, 1, 0f, 3.3f, 0f),
            new Part("t_plume_l", "t_arm_l", 20, 32, -0.5f, 0f, 0.4f, 1, 3, 3, 0f, 0f, 0f),
            new Part("t_a_spike_1", "t_body", 110, 38, -0.5f, -2f, -1f, 1, 2, 2, 0f, -2.7f, -2.55f),
            new Part("t_a_spike_2", "t_body", 116, 38, -0.5f, -2f, -1f, 1, 2, 2, 0f, -2.7f, 0.85f),
            new Part("t_a_spike_3", "t_tail1", 122, 38, -0.5f, -2f, -1f, 1, 2, 2, 0f, -1.35f, 1.6f),
            new Part("t_a_horn_r", "t_head", 0, 43, -0.5f, -3f, -0.5f, 1, 3, 1, -1.875f, -3.75f, -1.8f),
            new Part("t_a_horn_l", "t_head", 4, 43, -0.5f, -3f, -0.5f, 1, 3, 1, 1.875f, -3.75f, -1.8f)
        },
        new Part[] {
            new Part("b_body", "baby", 54, 0, -2f, -2f, -3f, 4, 4, 6, 0f, 18f, 0f),
            new Part("b_head", "baby", 28, 16, -2.5f, -4f, -3.5f, 5, 4, 5, 0f, 16.5f, -2.5f),
            new Part("b_snout", "b_head", 12, 38, -1.5f, -2f, -6.5f, 3, 2, 3, 0f, 0f, 0f),
            new Part("b_tail", "baby", 80, 16, -1f, -1f, 0f, 2, 2, 6, 0f, 17.5f, 3f),
            new Part("b_leg_r", "baby", 28, 32, -1f, 0f, -1f, 2, 4, 2, -1.5f, 20f, 0.5f),
            new Part("b_leg_l", "baby", 36, 32, -1f, 0f, -1f, 2, 4, 2, 1.5f, 20f, 0.5f)
        });

    public static final Spec TREX = new Spec(128, 128, 0.55f, 0.7f, 1.2f, -0.6f, -0.6f, 6f,
        new Part[] {
            new Part("body", "adult", 0, 0, -4f, -4.5f, -7.5f, 8, 9, 15, 0f, 7f, 0f),
            new Part("neck", "adult", 24, 66, -3f, -5f, -3f, 6, 5, 6, 0f, 3.5f, -7f),
            new Part("neck2", "neck", 50, 78, -2.5f, -4f, -2.5f, 5, 4, 5, 0f, -5f, 0f),
            new Part("head", "neck2", 26, 53, -3.5f, -4f, -3.5f, 7, 5, 7, 0f, -3f, 0f),
            new Part("snout", "head", 0, 78, -3f, -3.5f, -10.5f, 6, 3, 7, 0f, 0f, 0f),
            new Part("jaw", "head", 70, 78, -2.5f, 0f, -6.5f, 5, 2, 7, 0f, -1.5f, -3.5f),
            new Part("brow_r", "head", 64, 97, -1f, -1f, -2f, 2, 2, 4, -3.5f, -4f, -2f),
            new Part("brow_l", "head", 76, 97, -1f, -1f, -2f, 2, 2, 4, 3.5f, -4f, -2f),
            new Part("tail1", "adult", 86, 0, -3.5f, -3.5f, 0f, 7, 7, 10, 0f, 6.5f, 7.5f),
            new Part("tail2", "tail1", 0, 24, -2.5f, -2.5f, 0f, 5, 5, 10, 0f, 0f, 10f),
            new Part("tail3", "tail2", 0, 53, -1.5f, -1.5f, 0f, 3, 3, 10, 0f, 0f, 10f),
            new Part("thigh_r", "adult", 30, 24, -3f, 0f, -3.5f, 6, 8, 7, -5.5f, 7f, 1f),
            new Part("lower_r", "thigh_r", 48, 66, -2f, 0f, -2f, 4, 7, 4, 0f, 8f, 2.5f),
            new Part("foot_r", "lower_r", 94, 78, -2.5f, 0f, -6f, 5, 2, 7, 0f, 7f, 0f),
            new Part("claw_r", "foot_r", 24, 111, -0.5f, -1f, -5.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_r", "adult", 96, 105, -1f, 0f, -1f, 2, 3, 2, -4.5f, 6f, -6f),
            new Part("hand_r", "arm_r", 0, 111, -0.5f, 0f, -1f, 1, 2, 2, 0f, 3f, 0f),
            new Part("thigh_l", "adult", 56, 24, -3f, 0f, -3.5f, 6, 8, 7, 5.5f, 7f, 1f),
            new Part("lower_l", "thigh_l", 64, 66, -2f, 0f, -2f, 4, 7, 4, 0f, 8f, 2.5f),
            new Part("foot_l", "lower_l", 0, 88, -2.5f, 0f, -6f, 5, 2, 7, 0f, 7f, 0f),
            new Part("claw_l", "foot_l", 28, 111, -0.5f, -1f, -5.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_l", "adult", 104, 105, -1f, 0f, -1f, 2, 3, 2, 4.5f, 6f, -6f),
            new Part("hand_l", "arm_l", 6, 111, -0.5f, 0f, -1f, 1, 2, 2, 0f, 3f, 0f),
            new Part("a_spike_1", "body", 88, 97, -1f, -3f, -1.5f, 2, 3, 3, 0f, -4.5f, -4f),
            new Part("a_spike_2", "body", 98, 97, -1f, -3f, -1.5f, 2, 3, 3, 0f, -4.5f, 1f),
            new Part("a_spike_3", "body", 108, 97, -1f, -3f, -1.5f, 2, 3, 3, 0f, -4.5f, 5f),
            new Part("a_spike_4", "tail1", 118, 97, -1f, -3f, -1.5f, 2, 3, 3, 0f, -3.5f, 4f),
            new Part("a_horn_r", "head", 0, 105, -1f, -4f, -1f, 2, 4, 2, -3f, -4.5f, -3f),
            new Part("a_horn_l", "head", 8, 105, -1f, -4f, -1f, 2, 4, 2, 3f, -4.5f, -3f)
        },
        new Part[] {
            new Part("t_body", "teen", 46, 0, -3.6f, -4.05f, -6.375f, 7, 8, 13, 0f, 6f, 0f),
            new Part("t_neck", "teen", 26, 78, -2.85f, -4.5f, -2.85f, 6, 4, 6, 0f, 2.5f, -5.95f),
            new Part("t_neck2", "t_neck", 24, 88, -2.375f, -3.6f, -2.375f, 5, 4, 5, 0f, -4.5f, 0f),
            new Part("t_head", "t_neck2", 0, 39, -4.375f, -5f, -4.2f, 9, 6, 8, 0f, -2.7f, 0f),
            new Part("t_snout", "t_head", 44, 88, -3f, -4.025f, -8.4f, 6, 3, 6, 0f, 0f, 0f),
            new Part("t_jaw", "t_head", 0, 97, -2.5f, 0f, -5.2f, 5, 2, 6, 0f, -1.875f, -4.2f),
            new Part("t_brow_r", "t_head", 16, 105, -1.2f, -1.2f, -2f, 2, 2, 4, -4.375f, -5f, -2.4f),
            new Part("t_brow_l", "t_head", 28, 105, -1.2f, -1.2f, -2f, 2, 2, 4, 4.375f, -5f, -2.4f),
            new Part("t_tail1", "teen", 34, 39, -3.15f, -3.15f, 0f, 6, 6, 8, 0f, 5.5f, 6.375f),
            new Part("t_tail2", "t_tail1", 54, 53, -2.25f, -2.25f, 0f, 4, 4, 8, 0f, 0f, 8f),
            new Part("t_tail3", "t_tail2", 80, 66, -1.35f, -1.35f, 0f, 3, 3, 8, 0f, 0f, 8f),
            new Part("t_thigh_r", "teen", 62, 39, -2.7f, 0f, -3.15f, 5, 8, 6, -4.95f, 6f, 0.85f),
            new Part("t_lower_r", "t_thigh_r", 78, 53, -1.8f, 0f, -1.8f, 4, 8, 4, 0f, 8f, 2.25f),
            new Part("t_foot_r", "t_lower_r", 68, 88, -2.5f, 0f, -5.7f, 5, 2, 7, 0f, 8f, 0f),
            new Part("t_claw_r", "t_foot_r", 32, 111, -0.5f, -1f, -5.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("t_arm_r", "teen", 112, 105, -1.1f, 0f, -1.1f, 2, 3, 2, -4.05f, 5f, -5.1f),
            new Part("t_hand_r", "t_arm_r", 12, 111, -0.55f, 0f, -1.1f, 1, 2, 2, 0f, 3.3f, 0f),
            new Part("t_thigh_l", "teen", 84, 39, -2.7f, 0f, -3.15f, 5, 8, 6, 4.95f, 6f, 0.85f),
            new Part("t_lower_l", "t_thigh_l", 94, 53, -1.8f, 0f, -1.8f, 4, 8, 4, 0f, 8f, 2.25f),
            new Part("t_foot_l", "t_lower_l", 92, 88, -2.5f, 0f, -5.7f, 5, 2, 7, 0f, 8f, 0f),
            new Part("t_claw_l", "t_foot_l", 36, 111, -0.5f, -1f, -5.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("t_arm_l", "teen", 120, 105, -1.1f, 0f, -1.1f, 2, 3, 2, 4.05f, 5f, -5.1f),
            new Part("t_hand_l", "t_arm_l", 18, 111, -0.55f, 0f, -1.1f, 1, 2, 2, 0f, 3.3f, 0f),
            new Part("t_a_spike_1", "t_body", 40, 105, -1f, -3f, -1.5f, 2, 3, 3, 0f, -4.05f, -3.4f),
            new Part("t_a_spike_2", "t_body", 50, 105, -1f, -3f, -1.5f, 2, 3, 3, 0f, -4.05f, 0.85f),
            new Part("t_a_spike_3", "t_body", 60, 105, -1f, -3f, -1.5f, 2, 3, 3, 0f, -4.05f, 4.25f),
            new Part("t_a_spike_4", "t_tail1", 70, 105, -1f, -3f, -1.5f, 2, 3, 3, 0f, -3.15f, 3.2f),
            new Part("t_a_horn_r", "t_head", 80, 105, -1f, -4f, -1f, 2, 4, 2, -3.75f, -5.625f, -3.6f),
            new Part("t_a_horn_l", "t_head", 88, 105, -1f, -4f, -1f, 2, 4, 2, 3.75f, -5.625f, -3.6f)
        },
        new Part[] {
            new Part("b_body", "baby", 82, 24, -3f, -3f, -4.5f, 6, 6, 9, 0f, 17f, 0f),
            new Part("b_head", "baby", 102, 66, -3.5f, -5f, -4f, 7, 5, 6, 0f, 15.5f, -4f),
            new Part("b_snout", "b_head", 22, 97, -2.5f, -3f, -8f, 5, 3, 4, 0f, 0f, 0f),
            new Part("b_tail", "baby", 0, 66, -1.5f, -1.5f, 0f, 3, 3, 9, 0f, 16.5f, 4.5f),
            new Part("b_leg_r", "baby", 40, 97, -1.5f, 0f, -1.5f, 3, 4, 3, -2.5f, 20f, 0.5f),
            new Part("b_leg_l", "baby", 52, 97, -1.5f, 0f, -1.5f, 3, 4, 3, 2.5f, 20f, 0.5f)
        });

    public static final Spec DILOPHOSAURUS = new Spec(128, 64, 0.85f, 0.95f, 0.5f, -0.4f, 0.15f, 5f,
        new Part[] {
            new Part("body", "adult", 0, 0, -3f, -3.5f, -6f, 6, 7, 12, 0f, 11f, 0f),
            new Part("neck", "adult", 110, 28, -1.5f, -4f, -1.5f, 3, 4, 3, 0f, 9.5f, -5.5f),
            new Part("neck2", "neck", 0, 37, -1.5f, -4f, -1.5f, 3, 4, 3, 0f, -4f, 0f),
            new Part("head", "neck2", 28, 28, -2f, -3f, -2.5f, 4, 3, 5, 0f, -4f, 0f),
            new Part("snout", "head", 12, 37, -1.5f, -2.5f, -7.5f, 3, 2, 5, 0f, 0f, 0f),
            new Part("jaw", "head", 90, 37, -1.5f, 0f, -5f, 3, 1, 5, 0f, -0.5f, -2.5f),
            new Part("brow_r", "head", 28, 50, -0.5f, -0.5f, -1f, 1, 1, 3, -2f, -3f, -1f),
            new Part("brow_l", "head", 36, 50, -0.5f, -0.5f, -1f, 1, 1, 3, 2f, -3f, -1f),
            new Part("crest_r", "head", 0, 19, -0.5f, -3f, -3f, 1, 3, 6, -1f, -3f, -1f),
            new Part("crest_l", "head", 14, 19, -0.5f, -3f, -3f, 1, 3, 6, 1f, -3f, -1f),
            new Part("frill_r", "neck2", 106, 37, -5f, -2.5f, -0.5f, 5, 5, 1, -1f, -2f, 0.5f),
            new Part("frill_l", "neck2", 0, 44, 0f, -2.5f, -0.5f, 5, 5, 1, 1f, -2f, 0.5f),
            new Part("tail1", "adult", 66, 0, -2f, -2f, 0f, 4, 4, 6, 0f, 10.5f, 6f),
            new Part("tail2", "tail1", 28, 19, -1.5f, -1.5f, 0f, 3, 3, 6, 0f, 0f, 6f),
            new Part("tail3", "tail2", 46, 28, -1f, -1f, 0f, 2, 2, 6, 0f, 0f, 6f),
            new Part("thigh_r", "adult", 46, 19, -1.5f, 0f, -2f, 3, 5, 4, -3.5f, 12f, 1f),
            new Part("lower_r", "thigh_r", 62, 28, -1f, 0f, -1f, 2, 6, 2, 0f, 5f, 1f),
            new Part("foot_r", "lower_r", 56, 44, -1f, 0f, -3f, 2, 1, 4, 0f, 6f, 0f),
            new Part("claw_r", "foot_r", 0, 55, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_r", "adult", 44, 50, -0.5f, 0f, -0.5f, 1, 3, 1, -3f, 11.5f, -4.5f),
            new Part("hand_r", "arm_r", 4, 55, -0.5f, 0f, -0.5f, 1, 2, 1, 0f, 3f, 0f),
            new Part("thigh_l", "adult", 60, 19, -1.5f, 0f, -2f, 3, 5, 4, 3.5f, 12f, 1f),
            new Part("lower_l", "thigh_l", 70, 28, -1f, 0f, -1f, 2, 6, 2, 0f, 5f, 1f),
            new Part("foot_l", "lower_l", 68, 44, -1f, 0f, -3f, 2, 1, 4, 0f, 6f, 0f),
            new Part("claw_l", "foot_l", 8, 55, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_l", "adult", 48, 50, -0.5f, 0f, -0.5f, 1, 3, 1, 3f, 11.5f, -4.5f),
            new Part("hand_l", "arm_l", 12, 55, -0.5f, 0f, -0.5f, 1, 2, 1, 0f, 3f, 0f),
            new Part("a_spike_1", "body", 52, 50, -0.5f, -2f, -1f, 1, 2, 2, 0f, -3.5f, -3f),
            new Part("a_spike_2", "body", 58, 50, -0.5f, -2f, -1f, 1, 2, 2, 0f, -3.5f, 1f),
            new Part("a_spike_3", "tail1", 64, 50, -0.5f, -2f, -1f, 1, 2, 2, 0f, -2f, 2f),
            new Part("a_horn_r", "head", 70, 50, -0.5f, -3f, -0.5f, 1, 3, 1, -1.5f, -3f, -2f),
            new Part("a_horn_l", "head", 74, 50, -0.5f, -3f, -0.5f, 1, 3, 1, 1.5f, -3f, -2f)
        },
        new Part[] {
            new Part("t_body", "teen", 36, 0, -2.7f, -3.15f, -5.1f, 5, 6, 10, 0f, 10f, 0f),
            new Part("t_neck", "teen", 28, 37, -1.425f, -3.6f, -1.425f, 3, 4, 3, 0f, 8.5f, -4.675f),
            new Part("t_neck2", "t_neck", 40, 37, -1.425f, -3.6f, -1.425f, 3, 4, 3, 0f, -3.6f, 0f),
            new Part("t_head", "t_neck2", 86, 0, -2.5f, -3.75f, -3f, 5, 4, 6, 0f, -3.6f, 0f),
            new Part("t_snout", "t_head", 12, 44, -1.5f, -2.875f, -6f, 3, 2, 4, 0f, 0f, 0f),
            new Part("t_jaw", "t_head", 80, 44, -1.5f, 0f, -4f, 3, 1, 4, 0f, -0.625f, -3f),
            new Part("t_brow_r", "t_head", 78, 50, -0.6f, -0.6f, -1f, 1, 1, 3, -2.5f, -3.75f, -1.2f),
            new Part("t_brow_l", "t_head", 86, 50, -0.6f, -0.6f, -1f, 1, 1, 3, 2.5f, -3.75f, -1.2f),
            new Part("t_crest_r", "t_head", 52, 37, -0.5f, -2.4f, -2.4f, 1, 2, 5, -1.25f, -3.75f, -1.2f),
            new Part("t_crest_l", "t_head", 64, 37, -0.5f, -2.4f, -2.4f, 1, 2, 5, 1.25f, -3.75f, -1.2f),
            new Part("t_frill_r", "t_neck2", 94, 44, -4f, -2f, -0.4f, 4, 4, 1, -0.95f, -1.8f, 0.475f),
            new Part("t_frill_l", "t_neck2", 104, 44, 0f, -2f, -0.4f, 4, 4, 1, 0.95f, -1.8f, 0.475f),
            new Part("t_tail1", "teen", 74, 19, -1.8f, -1.8f, 0f, 4, 4, 5, 0f, 9.5f, 5.1f),
            new Part("t_tail2", "t_tail1", 78, 28, -1.35f, -1.35f, 0f, 3, 3, 5, 0f, 0f, 4.8f),
            new Part("t_tail3", "t_tail2", 76, 37, -0.9f, -0.9f, 0f, 2, 2, 5, 0f, 0f, 4.8f),
            new Part("t_thigh_r", "teen", 92, 19, -1.35f, 0f, -1.8f, 3, 5, 4, -3.15f, 11f, 0.85f),
            new Part("t_lower_r", "t_thigh_r", 106, 19, -0.9f, 0f, -0.9f, 2, 7, 2, 0f, 5f, 0.9f),
            new Part("t_foot_r", "t_lower_r", 114, 44, -1f, 0f, -2.85f, 2, 1, 4, 0f, 7f, 0f),
            new Part("t_claw_r", "t_foot_r", 16, 55, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("t_arm_r", "teen", 94, 50, -0.55f, 0f, -0.55f, 1, 3, 1, -2.7f, 10.5f, -3.825f),
            new Part("t_hand_r", "t_arm_r", 20, 55, -0.55f, 0f, -0.55f, 1, 2, 1, 0f, 3.3f, 0f),
            new Part("t_thigh_l", "teen", 114, 19, -1.35f, 0f, -1.8f, 3, 5, 4, 3.15f, 11f, 0.85f),
            new Part("t_lower_l", "t_thigh_l", 0, 28, -0.9f, 0f, -0.9f, 2, 7, 2, 0f, 5f, 0.9f),
            new Part("t_foot_l", "t_lower_l", 0, 50, -1f, 0f, -2.85f, 2, 1, 4, 0f, 7f, 0f),
            new Part("t_claw_l", "t_foot_l", 24, 55, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("t_arm_l", "teen", 98, 50, -0.55f, 0f, -0.55f, 1, 3, 1, 2.7f, 10.5f, -3.825f),
            new Part("t_hand_l", "t_arm_l", 28, 55, -0.55f, 0f, -0.55f, 1, 2, 1, 0f, 3.3f, 0f),
            new Part("t_a_spike_1", "t_body", 102, 50, -0.5f, -2f, -1f, 1, 2, 2, 0f, -3.15f, -2.55f),
            new Part("t_a_spike_2", "t_body", 108, 50, -0.5f, -2f, -1f, 1, 2, 2, 0f, -3.15f, 0.85f),
            new Part("t_a_spike_3", "t_tail1", 114, 50, -0.5f, -2f, -1f, 1, 2, 2, 0f, -1.8f, 1.6f),
            new Part("t_a_horn_r", "t_head", 120, 50, -0.5f, -3f, -0.5f, 1, 3, 1, -1.875f, -3.75f, -2.4f),
            new Part("t_a_horn_l", "t_head", 124, 50, -0.5f, -3f, -0.5f, 1, 3, 1, 1.875f, -3.75f, -2.4f)
        },
        new Part[] {
            new Part("b_body", "baby", 108, 0, -2f, -2f, -3f, 4, 4, 6, 0f, 18f, 0f),
            new Part("b_head", "baby", 8, 28, -2.5f, -4f, -3.5f, 5, 4, 5, 0f, 16.5f, -2.5f),
            new Part("b_snout", "b_head", 26, 44, -1.5f, -2f, -7.5f, 3, 2, 4, 0f, 0f, 0f),
            new Part("b_crest_r", "b_head", 12, 50, -0.5f, -2f, -1f, 1, 2, 3, -1f, -4f, -1f),
            new Part("b_crest_l", "b_head", 20, 50, -0.5f, -2f, -1f, 1, 2, 3, 1f, -4f, -1f),
            new Part("b_tail", "baby", 94, 28, -1f, -1f, 0f, 2, 2, 6, 0f, 17.5f, 3f),
            new Part("b_leg_r", "baby", 40, 44, -1f, 0f, -1f, 2, 4, 2, -1.5f, 20f, 0.5f),
            new Part("b_leg_l", "baby", 48, 44, -1f, 0f, -1f, 2, 4, 2, 1.5f, 20f, 0.5f)
        });

    private TheropodSpecs() {}
}
