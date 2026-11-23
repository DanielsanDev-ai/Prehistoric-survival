// ARCHIVO GENERADO por tools/theropods.py - NO EDITAR A MANO.
package com.prehistoricsurvival.client.model;

/** Geometria y coordenadas de textura de los terópodos (generadas a partir de una unica definicion). */
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
        public final float stride, swingSpeed, neckLean, crouchY;
        public final Part[] adult, baby;
        Spec(int texW, int texH, float stride, float swingSpeed, float neckLean, float crouchY,
             Part[] adult, Part[] baby) {
            this.texW = texW; this.texH = texH; this.stride = stride; this.swingSpeed = swingSpeed;
            this.neckLean = neckLean; this.crouchY = crouchY; this.adult = adult; this.baby = baby;
        }
    }

    public static final Spec VELOCIRAPTOR = new Spec(128, 64, 0.9f, 1f, 0.5f, 5f,
        new Part[] {
            new Part("body", "adult", 0, 0, -2.5f, -3f, -5f, 5, 6, 10, 0f, 12f, 0f),
            new Part("neck", "adult", 70, 0, -1.5f, -6f, -1.5f, 3, 6, 3, 0f, 10f, -4.5f),
            new Part("head", "neck", 36, 16, -2f, -3f, -2f, 4, 3, 4, 0f, -6f, 0f),
            new Part("snout", "head", 68, 16, -1.5f, -2.5f, -6f, 3, 2, 4, 0f, 0f, 0f),
            new Part("jaw", "head", 98, 16, -1.5f, 0f, -4f, 3, 1, 4, 0f, -0.5f, -2f),
            new Part("tail1", "adult", 30, 0, -1.5f, -1.5f, 0f, 3, 3, 7, 0f, 11.5f, 5f),
            new Part("tail2", "tail1", 82, 0, -1f, -1f, 0f, 2, 2, 7, 0f, 0f, 7f),
            new Part("thigh_r", "adult", 100, 0, -1.5f, 0f, -2f, 3, 5, 4, -3f, 13f, 1f),
            new Part("lower_r", "thigh_r", 52, 16, -1f, 0f, -1f, 2, 5, 2, 0f, 5f, 1f),
            new Part("foot_r", "lower_r", 112, 16, -1f, 0f, -3f, 2, 1, 4, 0f, 5f, 0f),
            new Part("claw_r", "foot_r", 28, 25, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_r", "adult", 124, 16, -0.5f, 0f, -0.5f, 1, 4, 1, -3f, 12f, -3.5f),
            new Part("thigh_l", "adult", 114, 0, -1.5f, 0f, -2f, 3, 5, 4, 3f, 13f, 1f),
            new Part("lower_l", "thigh_l", 60, 16, -1f, 0f, -1f, 2, 5, 2, 0f, 5f, 1f),
            new Part("foot_l", "lower_l", 0, 25, -1f, 0f, -3f, 2, 1, 4, 0f, 5f, 0f),
            new Part("claw_l", "foot_l", 32, 25, -0.5f, -1f, -3.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_l", "adult", 12, 25, -0.5f, 0f, -0.5f, 1, 4, 1, 3f, 12f, -3.5f)
        },
        new Part[] {
            new Part("b_body", "baby", 50, 0, -2f, -2f, -3f, 4, 4, 6, 0f, 18f, 0f),
            new Part("b_head", "baby", 0, 16, -2.5f, -4f, -3.5f, 5, 4, 5, 0f, 16.5f, -2.5f),
            new Part("b_snout", "b_head", 16, 25, -1.5f, -2f, -6.5f, 3, 2, 3, 0f, 0f, 0f),
            new Part("b_tail", "baby", 20, 16, -1f, -1f, 0f, 2, 2, 6, 0f, 17.5f, 3f),
            new Part("b_leg_r", "baby", 82, 16, -1f, 0f, -1f, 2, 4, 2, -1.5f, 20f, 0.5f),
            new Part("b_leg_l", "baby", 90, 16, -1f, 0f, -1f, 2, 4, 2, 1.5f, 20f, 0.5f)
        });

    public static final Spec TREX = new Spec(128, 128, 0.55f, 0.7f, 0.8f, 6f,
        new Part[] {
            new Part("body", "adult", 0, 0, -4f, -4.5f, -7.5f, 8, 9, 15, 0f, 7f, 0f),
            new Part("neck", "adult", 74, 24, -3f, -6f, -3f, 6, 6, 6, 0f, 3.5f, -7f),
            new Part("head", "neck", 0, 39, -3f, -5f, -3f, 6, 5, 6, 0f, -6f, 0f),
            new Part("snout", "head", 82, 39, -2.5f, -4f, -10f, 5, 3, 7, 0f, 0f, 0f),
            new Part("jaw", "head", 22, 50, -2.5f, 0f, -6f, 5, 2, 6, 0f, -1.5f, -3f),
            new Part("tail1", "adult", 46, 0, -3f, -3f, 0f, 6, 6, 12, 0f, 6f, 7.5f),
            new Part("tail2", "tail1", 82, 0, -2f, -2f, 0f, 4, 4, 12, 0f, 0f, 12f),
            new Part("thigh_r", "adult", 30, 24, -2.5f, 0f, -3f, 5, 8, 6, -5.5f, 7f, 1f),
            new Part("lower_r", "thigh_r", 24, 39, -2f, 0f, -2f, 4, 7, 4, 0f, 8f, 2f),
            new Part("foot_r", "lower_r", 106, 39, -2f, 0f, -5f, 4, 2, 7, 0f, 7f, 0f),
            new Part("claw_r", "foot_r", 102, 50, -0.5f, -1f, -5.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_r", "adult", 86, 50, -1f, 0f, -1f, 2, 4, 2, -4.5f, 6f, -6f),
            new Part("thigh_l", "adult", 52, 24, -2.5f, 0f, -3f, 5, 8, 6, 5.5f, 7f, 1f),
            new Part("lower_l", "thigh_l", 40, 39, -2f, 0f, -2f, 4, 7, 4, 0f, 8f, 2f),
            new Part("foot_l", "lower_l", 0, 50, -2f, 0f, -5f, 4, 2, 7, 0f, 7f, 0f),
            new Part("claw_l", "foot_l", 106, 50, -0.5f, -1f, -5.5f, 1, 2, 1, 0f, 0f, 0f),
            new Part("arm_l", "adult", 94, 50, -1f, 0f, -1f, 2, 4, 2, 4.5f, 6f, -6f)
        },
        new Part[] {
            new Part("b_body", "baby", 0, 24, -3f, -3f, -4.5f, 6, 6, 9, 0f, 17f, 0f),
            new Part("b_head", "baby", 56, 39, -3.5f, -5f, -4f, 7, 5, 6, 0f, 15.5f, -4f),
            new Part("b_snout", "b_head", 44, 50, -2.5f, -3f, -8f, 5, 3, 4, 0f, 0f, 0f),
            new Part("b_tail", "baby", 98, 24, -1.5f, -1.5f, 0f, 3, 3, 9, 0f, 16.5f, 4.5f),
            new Part("b_leg_r", "baby", 62, 50, -1.5f, 0f, -1.5f, 3, 4, 3, -2.5f, 20f, 0.5f),
            new Part("b_leg_l", "baby", 74, 50, -1.5f, 0f, -1.5f, 3, 4, 3, 2.5f, 20f, 0.5f)
        });

    private TheropodSpecs() {}
}
