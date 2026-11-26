#!/usr/bin/env python3
"""
Terópodos (Velociraptor, Tyrannosaurus, Dilophosaurus): definición ÚNICA de las piezas del modelo.
De aquí salen (1) TheropodSpecs.java con la geometría y las coordenadas de textura,
(2) las texturas (adulto + adolescente + bebé + alfa + brillo del alfa) y (3) vistas previas.

pieza = (nombre, padre, (ox,oy,oz) origen de la caja, (w,h,d) tamaño, (px,py,pz) pivote)
Etapas: adulto (nombres normales), adolescente (prefijo t_, derivado automáticamente del adulto con
proporciones juveniles: cabeza grande, hocico corto, patas largas, cola corta), bebé (prefijo b_).
Piezas "a_*": exclusivas del alfa (púas, cuernos) — el modelo las oculta si no es alfa.
"""
import math
import os
import random
import re
import zlib

import numpy as np
from PIL import Image, ImageDraw

import generate_assets as g

JAVA_DIR = os.path.normpath(os.path.join(g.HERE, "..", "src", "main", "java", "com", "prehistoricsurvival",
                                         "client", "model"))
PREVIEW_DIR = os.path.normpath(os.path.join(g.HERE, "..", "build_previews"))


# =========================================================================== piezas
def legs(hipx, hipy, thigh, lower, foot, claw_z, arm=None):
    """Patas (muslo, pierna, pie, garra) y brazos opcionales para ambos lados."""
    out = []
    for side, sx in (("r", -1), ("l", 1)):
        out += [
            (f"thigh_{side}", "adult", (-thigh[0] / 2, 0, -thigh[2] / 2), thigh, (hipx * sx, hipy, 1)),
            (f"lower_{side}", f"thigh_{side}", (-lower[0] / 2, 0, -lower[2] / 2), lower, (0, thigh[1], thigh[2] / 2 - 1)),
            (f"foot_{side}", f"lower_{side}", (-foot[0] / 2, 0, -foot[2] + 1), foot, (0, lower[1], 0)),
            (f"claw_{side}", f"foot_{side}", (-0.5, -1, claw_z), (1, 2, 1), (0, 0, 0)),
        ]
        if arm:
            (ax, ay, az), arm_s, hand_s, plume = arm
            out.append((f"arm_{side}", "adult", (-arm_s[0] / 2, 0, -arm_s[2] / 2), arm_s, (ax * sx, ay, az)))
            out.append((f"hand_{side}", f"arm_{side}", (-hand_s[0] / 2, 0, -hand_s[2] / 2), hand_s, (0, arm_s[1], 0)))
            if plume:
                out.append((f"plume_{side}", f"arm_{side}", (-0.5, 0, 0.5), plume, (0, 0, 0)))
    return out


def raptor():
    adult = [
        ("body", "adult", (-2.5, -3, -5), (5, 6, 10), (0, 12, 0)),
        ("neck", "adult", (-1.5, -4, -1.5), (3, 4, 3), (0, 10.5, -4.5)),
        ("neck2", "neck", (-1, -4, -1), (2, 4, 2), (0, -4, 0)),
        ("head", "neck2", (-2, -3, -2), (4, 3, 4), (0, -4, 0)),
        ("snout", "head", (-1.5, -2.5, -6), (3, 2, 4), (0, 0, 0)),
        ("jaw", "head", (-1.5, 0, -4), (3, 1, 4), (0, -0.5, -2)),
        ("brow_r", "head", (-0.5, -0.5, -1), (1, 1, 3), (-2, -3, -1)),
        ("brow_l", "head", (-0.5, -0.5, -1), (1, 1, 3), (2, -3, -1)),
        ("tail1", "adult", (-1.5, -1.5, 0), (3, 3, 5), (0, 11.5, 5)),
        ("tail2", "tail1", (-1, -1, 0), (2, 2, 5), (0, 0, 5)),
        ("tail3", "tail2", (-2, -0.5, 0), (4, 1, 5), (0, 0, 5)),     # abanico de plumas
    ]
    adult += legs(3, 13, (3, 5, 4), (2, 5, 2), (2, 1, 4), -3.5,
                  arm=((2.5, 12, -3.5), (1, 3, 1), (1, 2, 1), (1, 3, 4)))
    alpha = [
        ("a_spike_1", "body", (-0.5, -2, -1), (1, 2, 2), (0, -3, -3)),
        ("a_spike_2", "body", (-0.5, -2, -1), (1, 2, 2), (0, -3, 1)),
        ("a_spike_3", "tail1", (-0.5, -2, -1), (1, 2, 2), (0, -1.5, 2)),
        ("a_horn_r", "head", (-0.5, -3, -0.5), (1, 3, 1), (-1.5, -3, -1.5)),
        ("a_horn_l", "head", (-0.5, -3, -0.5), (1, 3, 1), (1.5, -3, -1.5)),
    ]
    baby = [
        ("b_body", "baby", (-2, -2, -3), (4, 4, 6), (0, 18, 0)),
        ("b_head", "baby", (-2.5, -4, -3.5), (5, 4, 5), (0, 16.5, -2.5)),
        ("b_snout", "b_head", (-1.5, -2, -6.5), (3, 2, 3), (0, 0, 0)),
        ("b_tail", "baby", (-1, -1, 0), (2, 2, 6), (0, 17.5, 3)),
        ("b_leg_r", "baby", (-1, 0, -1), (2, 4, 2), (-1.5, 20, 0.5)),
        ("b_leg_l", "baby", (-1, 0, -1), (2, 4, 2), (1.5, 20, 0.5)),
    ]
    return adult + alpha, baby


def rex():
    adult = [
        ("body", "adult", (-4, -4.5, -7.5), (8, 9, 15), (0, 7, 0)),
        ("neck", "adult", (-3, -5, -3), (6, 5, 6), (0, 3.5, -7)),
        ("neck2", "neck", (-2.5, -4, -2.5), (5, 4, 5), (0, -5, 0)),
        ("head", "neck2", (-3.5, -4, -3.5), (7, 5, 7), (0, -3, 0)),
        ("snout", "head", (-3, -3.5, -10.5), (6, 3, 7), (0, 0, 0)),
        ("jaw", "head", (-2.5, 0, -6.5), (5, 2, 7), (0, -1.5, -3.5)),
        ("brow_r", "head", (-1, -1, -2), (2, 2, 4), (-3.5, -4, -2)),
        ("brow_l", "head", (-1, -1, -2), (2, 2, 4), (3.5, -4, -2)),
        ("tail1", "adult", (-3.5, -3.5, 0), (7, 7, 10), (0, 6.5, 7.5)),
        ("tail2", "tail1", (-2.5, -2.5, 0), (5, 5, 10), (0, 0, 10)),
        ("tail3", "tail2", (-1.5, -1.5, 0), (3, 3, 10), (0, 0, 10)),
    ]
    adult += legs(5.5, 7, (6, 8, 7), (4, 7, 4), (5, 2, 7), -5.5,
                  arm=((4.5, 6, -6), (2, 3, 2), (1, 2, 2), None))
    alpha = [
        ("a_spike_1", "body", (-1, -3, -1.5), (2, 3, 3), (0, -4.5, -4)),
        ("a_spike_2", "body", (-1, -3, -1.5), (2, 3, 3), (0, -4.5, 1)),
        ("a_spike_3", "body", (-1, -3, -1.5), (2, 3, 3), (0, -4.5, 5)),
        ("a_spike_4", "tail1", (-1, -3, -1.5), (2, 3, 3), (0, -3.5, 4)),
        ("a_horn_r", "head", (-1, -4, -1), (2, 4, 2), (-3, -4.5, -3)),
        ("a_horn_l", "head", (-1, -4, -1), (2, 4, 2), (3, -4.5, -3)),
    ]
    baby = [
        ("b_body", "baby", (-3, -3, -4.5), (6, 6, 9), (0, 17, 0)),
        ("b_head", "baby", (-3.5, -5, -4), (7, 5, 6), (0, 15.5, -4)),
        ("b_snout", "b_head", (-2.5, -3, -8), (5, 3, 4), (0, 0, 0)),
        ("b_tail", "baby", (-1.5, -1.5, 0), (3, 3, 9), (0, 16.5, 4.5)),
        ("b_leg_r", "baby", (-1.5, 0, -1.5), (3, 4, 3), (-2.5, 20, 0.5)),
        ("b_leg_l", "baby", (-1.5, 0, -1.5), (3, 4, 3), (2.5, 20, 0.5)),
    ]
    return adult + alpha, baby


def dilo():
    adult = [
        ("body", "adult", (-3, -3.5, -6), (6, 7, 12), (0, 11, 0)),
        ("neck", "adult", (-1.5, -4, -1.5), (3, 4, 3), (0, 9.5, -5.5)),
        ("neck2", "neck", (-1.5, -4, -1.5), (3, 4, 3), (0, -4, 0)),
        ("head", "neck2", (-2, -3, -2.5), (4, 3, 5), (0, -4, 0)),
        ("snout", "head", (-1.5, -2.5, -7.5), (3, 2, 5), (0, 0, 0)),
        ("jaw", "head", (-1.5, 0, -5), (3, 1, 5), (0, -0.5, -2.5)),
        ("brow_r", "head", (-0.5, -0.5, -1), (1, 1, 3), (-2, -3, -1)),
        ("brow_l", "head", (-0.5, -0.5, -1), (1, 1, 3), (2, -3, -1)),
        ("crest_r", "head", (-0.5, -3, -3), (1, 3, 6), (-1, -3, -1)),    # las dos crestas
        ("crest_l", "head", (-0.5, -3, -3), (1, 3, 6), (1, -3, -1)),
        ("frill_r", "neck2", (-5, -2.5, -0.5), (5, 5, 1), (-1, -2, 0.5)),  # gorguera desplegable
        ("frill_l", "neck2", (0, -2.5, -0.5), (5, 5, 1), (1, -2, 0.5)),
        ("tail1", "adult", (-2, -2, 0), (4, 4, 6), (0, 10.5, 6)),
        ("tail2", "tail1", (-1.5, -1.5, 0), (3, 3, 6), (0, 0, 6)),
        ("tail3", "tail2", (-1, -1, 0), (2, 2, 6), (0, 0, 6)),
    ]
    adult += legs(3.5, 12, (3, 5, 4), (2, 6, 2), (2, 1, 4), -3.5,
                  arm=((3, 11.5, -4.5), (1, 3, 1), (1, 2, 1), None))
    alpha = [
        ("a_spike_1", "body", (-0.5, -2, -1), (1, 2, 2), (0, -3.5, -3)),
        ("a_spike_2", "body", (-0.5, -2, -1), (1, 2, 2), (0, -3.5, 1)),
        ("a_spike_3", "tail1", (-0.5, -2, -1), (1, 2, 2), (0, -2, 2)),
        ("a_horn_r", "head", (-0.5, -3, -0.5), (1, 3, 1), (-1.5, -3, -2)),
        ("a_horn_l", "head", (-0.5, -3, -0.5), (1, 3, 1), (1.5, -3, -2)),
    ]
    baby = [
        ("b_body", "baby", (-2, -2, -3), (4, 4, 6), (0, 18, 0)),
        ("b_head", "baby", (-2.5, -4, -3.5), (5, 4, 5), (0, 16.5, -2.5)),
        ("b_snout", "b_head", (-1.5, -2, -7.5), (3, 2, 4), (0, 0, 0)),
        ("b_crest_r", "b_head", (-0.5, -2, -1), (1, 2, 3), (-1, -4, -1)),
        ("b_crest_l", "b_head", (-0.5, -2, -1), (1, 2, 3), (1, -4, -1)),
        ("b_tail", "baby", (-1, -1, 0), (2, 2, 6), (0, 17.5, 3)),
        ("b_leg_r", "baby", (-1, 0, -1), (2, 4, 2), (-1.5, 20, 0.5)),
        ("b_leg_l", "baby", (-1, 0, -1), (2, 4, 2), (1.5, 20, 0.5)),
    ]
    return adult + alpha, baby


# =========================================================================== adolescente (derivado)
TEEN_F = {
    "head": (1.25, 1.25, 1.2), "snout": (1.0, 1.15, 0.8), "jaw": (1.0, 1.0, 0.8), "brow": (1.2, 1.2, 1.0),
    "body": (0.9, 0.9, 0.85), "neck": (0.95, 0.9, 0.95), "neck2": (0.95, 0.9, 0.95),
    "tail1": (0.9, 0.9, 0.8), "tail2": (0.9, 0.9, 0.8), "tail3": (0.9, 0.9, 0.8),
    "thigh": (0.9, 1.05, 0.9), "lower": (0.9, 1.15, 0.9), "foot": (1.0, 1.0, 0.95),
    "arm": (1.1, 1.1, 1.1), "hand": (1.1, 1.1, 1.1), "plume": (1.0, 1.0, 0.8),
    "crest": (1.0, 0.8, 0.8), "frill": (0.8, 0.8, 0.8),
}


def base_role(name):
    n = name
    for pre in ("t_", "b_"):
        if n.startswith(pre):
            n = n[2:]
            break
    n = re.sub(r"_[rl]$", "", n)
    n = re.sub(r"_\d+$", "", n)
    return n


def teenify(adult):
    def fac(name):
        return TEEN_F.get(base_role(name), (1, 1, 1))

    sizes = {}
    for name, parent, o, s, pv in adult:
        f_ = fac(name)
        sizes[name] = tuple(max(1, round(s[i] * f_[i])) for i in range(3))
    thigh = next(p for p in adult if p[0] == "thigh_r")
    legsum = sizes["thigh_r"][1] + sizes["lower_r"][1] + sizes["foot_r"][1]
    delta = (24 - legsum) - thigh[4][1]
    body_f = TEEN_F["body"]
    out = []
    for name, parent, o, s, pv in adult:
        f_ = fac(name)
        ns = sizes[name]
        no = tuple(o[i] * f_[i] for i in range(3))
        if parent == "adult":
            npv = (pv[0] * body_f[0], pv[1] + delta, pv[2] * body_f[2])
        else:
            pf = fac(parent)
            npv = (pv[0] * pf[0], pv[1] * pf[1], pv[2] * pf[2])
        role = base_role(name)
        if role in ("lower", "foot"):
            npv = (npv[0], sizes[parent][1], npv[2])
        out.append(("t_" + name, "teen" if parent == "adult" else "t_" + parent, no, ns, npv))
    return out


SPECS = {
    "VELOCIRAPTOR": dict(id="velociraptor", build=raptor, stride=0.9, swing=1.0, neck1=0.6, neck2=-0.5,
                         head=0.1, crouch=5.0),
    "TREX": dict(id="tyrannosaurus", build=rex, stride=0.55, swing=0.7, neck1=1.2, neck2=-0.6,
                 head=-0.6, crouch=6.0),
    "DILOPHOSAURUS": dict(id="dilophosaurus", build=dilo, stride=0.85, swing=0.95, neck1=0.5, neck2=-0.4,
                          head=0.15, crouch=5.0),
}
for _k, _sp in SPECS.items():
    _adult, _baby = _sp["build"]()
    _sp["adult"], _sp["baby"] = _adult, _baby
    _sp["teen"] = teenify(_adult)


def box_size(w, h, d):
    return 2 * d + 2 * w, d + h


def pack(parts):
    for tw, th in ((128, 64), (128, 128), (256, 128), (256, 256)):
        items = sorted(parts, key=lambda p: -box_size(*p[3])[1])
        uv, x, y, row_h = {}, 0, 0, 0
        for p in items:
            bw, bh = box_size(*p[3])
            if x + bw > tw:
                x, y, row_h = 0, y + row_h, 0
            uv[p[0]] = (x, y)
            x += bw
            row_h = max(row_h, bh)
        if y + row_h <= th:
            return uv, (tw, th)
    raise AssertionError("las piezas no caben en 256x256")


def fl(v):
    return f"{float(v):g}f"


def write_java():
    out = ["// ARCHIVO GENERADO por tools/theropods.py - NO EDITAR A MANO.",
           "package com.prehistoricsurvival.client.model;", "",
           "/** Geometria y coordenadas de textura de los teropodos (adulto, adolescente y bebe). */",
           "public final class TheropodSpecs {",
           "    public static final class Part {",
           "        public final String name, parent;",
           "        public final int u, v, w, h, d;",
           "        public final float ox, oy, oz, px, py, pz;",
           "        Part(String name, String parent, int u, int v, float ox, float oy, float oz, int w, int h, int d,",
           "             float px, float py, float pz) {",
           "            this.name = name; this.parent = parent; this.u = u; this.v = v;",
           "            this.ox = ox; this.oy = oy; this.oz = oz; this.w = w; this.h = h; this.d = d;",
           "            this.px = px; this.py = py; this.pz = pz;",
           "        }",
           "    }", "",
           "    public static final class Spec {",
           "        public final int texW, texH;",
           "        public final float stride, swingSpeed, neck1, neck2, headRest, crouchY;",
           "        public final Part[] adult, teen, baby;",
           "        Spec(int texW, int texH, float stride, float swingSpeed, float neck1, float neck2, float headRest,",
           "             float crouchY, Part[] adult, Part[] teen, Part[] baby) {",
           "            this.texW = texW; this.texH = texH; this.stride = stride; this.swingSpeed = swingSpeed;",
           "            this.neck1 = neck1; this.neck2 = neck2; this.headRest = headRest; this.crouchY = crouchY;",
           "            this.adult = adult; this.teen = teen; this.baby = baby;",
           "        }",
           "    }", ""]
    for key, sp in SPECS.items():
        allparts = sp["adult"] + sp["teen"] + sp["baby"]
        uv, size = pack(allparts)
        sp["uv"], sp["tex"] = uv, size

        def fmt(p):
            n, par, o, s, pv = p
            u, v = uv[n]
            return (f'            new Part("{n}", "{par}", {u}, {v}, {fl(o[0])}, {fl(o[1])}, {fl(o[2])}, '
                    f'{s[0]}, {s[1]}, {s[2]}, {fl(pv[0])}, {fl(pv[1])}, {fl(pv[2])})')
        out.append(f"    public static final Spec {key} = new Spec({size[0]}, {size[1]}, "
                   f"{fl(sp['stride'])}, {fl(sp['swing'])}, {fl(sp['neck1'])}, {fl(sp['neck2'])}, {fl(sp['head'])}, "
                   f"{fl(sp['crouch'])},")
        for group in ("adult", "teen", "baby"):
            out.append("        new Part[] {")
            out.append(",\n".join(fmt(p) for p in sp[group]))
            out.append("        }" + ("," if group != "baby" else ");"))
        out.append("")
    out.append("    private TheropodSpecs() {}")
    out.append("}")
    with open(os.path.join(JAVA_DIR, "TheropodSpecs.java"), "w", encoding="utf-8") as fh:
        fh.write("\n".join(out) + "\n")


# =========================================================================== vista previa 3D (solo para verificar)
def _rot(rx, ry, rz):
    cx, sx, cy, sy, cz, sz = math.cos(rx), math.sin(rx), math.cos(ry), math.sin(ry), math.cos(rz), math.sin(rz)
    rxm = np.array([[1, 0, 0], [0, cx, -sx], [0, sx, cx]])
    rym = np.array([[cy, 0, sy], [0, 1, 0], [-sy, 0, cy]])
    rzm = np.array([[cz, -sz, 0], [sz, cz, 0], [0, 0, 1]])
    m = np.eye(4)
    m[:3, :3] = rzm @ rym @ rxm
    return m


def _tr(x, y, z):
    m = np.eye(4)
    m[:3, 3] = (x, y, z)
    return m


def rest_pose(key, group):
    sp = SPECS[key]
    pose = {}
    pre = {"adult": "", "teen": "t_", "baby": "b_"}[group]
    if group == "baby":
        return pose
    pose[pre + "neck"] = (sp["neck1"], 0, 0)
    pose[pre + "neck2"] = (sp["neck2"], 0, 0)
    pose[pre + "head"] = (sp["head"], 0, 0)
    pose[pre + "thigh_r"] = pose[pre + "thigh_l"] = (-0.3, 0, 0)
    pose[pre + "lower_r"] = pose[pre + "lower_l"] = (0.6, 0, 0)
    pose[pre + "foot_r"] = pose[pre + "foot_l"] = (-0.3, 0, 0)
    pose[pre + "arm_r"] = pose[pre + "arm_l"] = (-0.5, 0, 0)
    pose[pre + "tail1"] = (-0.08, 0, 0)
    pose[pre + "frill_r"] = (0, 1.45, 0)
    pose[pre + "frill_l"] = (0, -1.45, 0)
    return pose


def render_side(key, group, colors, alpha=False, scale=9, pose_override=None, front=False):
    parts = SPECS[key][group]
    pose = rest_pose(key, group)
    if pose_override:
        pose.update(pose_override)
    mats, polys = {}, []
    root = np.eye(4)
    maxy = 0
    for name, parent, o, s, pv in parts:
        if base_role(name) in ("a_spike", "a_horn") and not alpha:
            continue
        pm = mats.get(parent, root)
        rx, ry, rz = pose.get(name, (0, 0, 0))
        m = pm @ _tr(*pv) @ _rot(rx, ry, rz)
        mats[name] = m
        x0, y0, z0 = o
        w, h, d = s
        corners = np.array([[x0 + a * w, y0 + b * h, z0 + c * d, 1] for a in (0, 1) for b in (0, 1) for c in (0, 1)]).T
        wc = (m @ corners)[:3].T
        maxy = max(maxy, wc[:, 1].max())
        col = colors.get(base_role(name), (150, 150, 150))
        faces = [([0, 1, 3, 2], (-1, 0, 0), 0.78), ([4, 5, 7, 6], (1, 0, 0), 1.0), ([0, 1, 5, 4], (0, 0, -1), 0.88),
                 ([2, 3, 7, 6], (0, 0, 1), 0.88), ([0, 2, 6, 4], (0, -1, 0), 1.1), ([1, 3, 7, 5], (0, 1, 0), 0.7)]
        for idx, normal, sh in faces:
            n = (m[:3, :3] @ np.array(normal))
            if front:
                if n[2] > 0.05:
                    continue
                pts = wc[idx]
                polys.append((-pts[:, 2].mean(), [(p[0], p[1]) for p in pts], tuple(min(255, int(c * sh)) for c in col)))
                continue
            if n[0] < -0.05:
                continue
            pts = wc[idx]
            polys.append((pts[:, 0].mean(), [(p[2], p[1]) for p in pts], tuple(min(255, int(c * sh)) for c in col)))
    polys.sort(key=lambda t: t[0])
    zs = [p[0] for _, pts, _ in polys for p in pts]
    ys = [p[1] for _, pts, _ in polys for p in pts]
    minz, maxz, miny = min(zs), max(zs), min(min(ys), 0)
    W, H = int((90) * scale), int((30 - miny) * scale)
    img = Image.new("RGB", (W, H), (214, 226, 214))
    dr = ImageDraw.Draw(img)
    ox = W / 2 - (minz + maxz) / 2 * scale
    oy = -miny * scale + 2 * scale
    dr.line([(0, oy + 24 * scale), (W, oy + 24 * scale)], fill=(90, 120, 90), width=2)
    for _, pts, col in polys:
        dr.polygon([(ox + z * scale, oy + y * scale) for z, y in pts], fill=col, outline=(40, 40, 40))
    return img, maxy


def colors_for(key, variant, alpha=False):
    pal = palette(SPECS[key]["id"], variant, False, alpha)
    return {"body": pal["body"], "neck": pal["body"], "neck2": pal["body"], "head": pal["head"],
            "snout": pal["head"], "jaw": pal["belly"], "brow": shade(pal["head"], 0.8), "tail1": pal["body"],
            "tail2": pal["body"], "tail3": pal["accent"], "thigh": pal["body"], "lower": shade(pal["body"], 0.9),
            "foot": pal["foot"], "claw": pal["bone"], "arm": pal["accent"], "hand": pal["foot"],
            "plume": pal["accent"], "crest": pal["accent"], "frill": pal["accent"], "a_spike": pal["bone"],
            "a_horn": pal["bone"], "leg": pal["foot"], "body_b": pal["body"]}


def previews():
    os.makedirs(PREVIEW_DIR, exist_ok=True)
    for key, sp in SPECS.items():
        variant = VARIANTS[sp["id"]][0]
        tiles = []
        for group, alpha in (("adult", False), ("adult", True), ("teen", False), ("baby", False)):
            tile, _ = render_side(key, group, colors_for(key, variant, alpha), alpha, scale=10)
            tiles.append(tile)
        sheet = Image.new("RGB", (sum(t.width for t in tiles), max(t.height for t in tiles)), (214, 226, 214))
        x = 0
        for t in tiles:
            sheet.paste(t, (x, sheet.height - t.height))
            x += t.width
        sheet.save(os.path.join(PREVIEW_DIR, f"{sp['id']}_side.png"))
        if sp["id"] == "dilophosaurus":
            deploy = {"frill_r": (0, 0.1, 0), "frill_l": (0, -0.1, 0)}
            render_side(key, "adult", colors_for(key, variant), False, scale=10, pose_override=deploy)[0].save(
                os.path.join(PREVIEW_DIR, "dilophosaurus_frill.png"))
            render_side(key, "adult", colors_for(key, variant), False, scale=14, pose_override=deploy, front=True)[0].save(
                os.path.join(PREVIEW_DIR, "dilophosaurus_front.png"))


# =========================================================================== paletas y pintura
def shade(c, f_):
    return g.shade(c, f_)


def mixc(a, b, t):
    return g.mix(a, b, t)


PAL = {
    "velociraptor": {
        "olive": dict(body=(98, 112, 66), belly=(192, 182, 132), stripe=(50, 60, 34), accent=(206, 92, 52)),
        "ember": dict(body=(152, 86, 52), belly=(216, 186, 142), stripe=(72, 36, 22), accent=(232, 172, 52)),
        "slate": dict(body=(86, 100, 118), belly=(178, 188, 192), stripe=(38, 46, 60), accent=(196, 62, 72)),
    },
    "tyrannosaurus": {
        "forest": dict(body=(80, 98, 62), belly=(178, 170, 128), stripe=(40, 52, 34), accent=(150, 70, 52)),
        "dusk": dict(body=(120, 88, 64), belly=(198, 172, 132), stripe=(62, 40, 30), accent=(160, 84, 50)),
        "ash": dict(body=(110, 114, 100), belly=(192, 188, 166), stripe=(64, 66, 58), accent=(140, 96, 80)),
    },
    "dilophosaurus": {
        "viridian": dict(body=(70, 120, 84), belly=(196, 206, 160), stripe=(30, 66, 48), accent=(232, 132, 40)),
        "amber": dict(body=(150, 120, 60), belly=(218, 200, 140), stripe=(84, 60, 28), accent=(70, 170, 140)),
        "teal": dict(body=(60, 110, 120), belly=(190, 208, 196), stripe=(24, 56, 66), accent=(214, 70, 110)),
    },
}
VARIANTS = {"velociraptor": ["olive", "ember", "slate"], "tyrannosaurus": ["forest", "dusk", "ash"],
            "dilophosaurus": ["viridian", "amber", "teal"]}


def palette(species, variant, female, alpha):
    if alpha:
        base = {"velociraptor": dict(body=(34, 32, 38), belly=(98, 56, 48), stripe=(150, 24, 24), accent=(206, 164, 40)),
                "tyrannosaurus": dict(body=(32, 30, 36), belly=(92, 54, 46), stripe=(132, 26, 26), accent=(204, 162, 42)),
                "dilophosaurus": dict(body=(30, 40, 38), belly=(80, 96, 70), stripe=(40, 150, 60), accent=(120, 255, 70))}
        p = dict(base[species])
        eye = (255, 60, 40) if species != "dilophosaurus" else (150, 255, 60)
    else:
        p = dict(PAL[species][variant])
        eye = (232, 200, 48)
        if female:
            p["accent"] = mixc(p["body"], p["belly"], 0.35)
    p["eye"] = eye
    p["head"] = mixc(p["body"], p["belly"], 0.12)
    p["foot"] = (62, 56, 48)
    p["bone"] = (232, 226, 204)
    p["tooth"] = (238, 234, 218)
    return p


def stripe_cols(img, x, y, w, h, color, step, rng):
    px = img.load()
    for i in range(w):
        if i % step == 0:
            for j in range(h):
                if rng.random() < 0.8:
                    px[x + i, y + j] = color + (255,)


def cells(img, x, y, w, h, rng, amp=0.09, cell=2):
    """Escamas: celdas con brillo aleatorio y un reflejo en una esquina."""
    px = img.load()
    for cy in range(0, h, cell):
        for cx in range(0, w, cell):
            k = 1 + rng.uniform(-amp, amp)
            for j in range(cy, min(h, cy + cell)):
                for i in range(cx, min(w, cx + cell)):
                    r, gg, b, a = px[x + i, y + j]
                    if a == 0:
                        continue
                    hl = 1.08 if (i == cx and j == cy) else (0.93 if (i == cx + cell - 1 and j == cy + cell - 1) else 1.0)
                    px[x + i, y + j] = (g.clamp(r * k * hl), g.clamp(gg * k * hl), g.clamp(b * k * hl), a)


def stage_of(name):
    return "teen" if name.startswith("t_") else "baby" if name.startswith("b_") else "adult"


GLOW = []


def paint_part(img, rng, species, part, uv, pal, female, alpha):
    name, _, _, (w, h, d), _ = part
    u, v = uv[name]
    stage = stage_of(name)
    role = base_role(name)
    body, belly, stripe, accent = pal["body"], pal["belly"], pal["stripe"], pal["accent"]
    px = img.load()
    big = species == "tyrannosaurus"
    down = (226, 214, 176)
    if stage == "teen":
        body = mixc(body, down, 0.14)
        accent = mixc(accent, body, 0.5)
    fluff = mixc(body, down, 0.55)

    def eyes():
        for cx in (d // 2 - 1, d // 2):
            for (ex, ey) in ((u + cx, v + d + 1), (u + d + w + cx, v + d + 1)):
                px[ex, ey] = pal["eye"] + (255,)
                if alpha:
                    GLOW.append((ex, ey, pal["eye"]))

    if stage == "baby":
        if role in ("body", "tail"):
            g.box(img, rng, u, v, w, h, d, top=fluff, side=fluff, bottom=down, front=fluff, back=fluff, noise=12)
            stripe_cols(img, u + d, v, w, d, mixc(stripe, fluff, 0.4), 2, rng)
        elif role == "head":
            g.box(img, rng, u, v, w, h, d, top=shade(fluff, 1.05), side=fluff, bottom=down, noise=12)
            eyes()
        elif role == "snout":
            g.box(img, rng, u, v, w, h, d, top=fluff, side=fluff, bottom=down, front=shade(fluff, 0.8), noise=8)
        elif role == "crest":
            g.box(img, rng, u, v, w, h, d, top=accent, side=mixc(accent, fluff, 0.4), bottom=fluff, noise=6)
        else:
            g.box(img, rng, u, v, w, h, d, top=pal["foot"], side=mixc(pal["foot"], fluff, 0.3),
                  bottom=shade(pal["foot"], 0.7), noise=5)
        return

    full = (2 * d + 2 * w, d + h)
    if role == "body":
        g.box(img, rng, u, v, w, h, d, top=shade(body, 1.08), side=body, bottom=belly, front=belly, back=shade(body, 0.9))
        stripe_cols(img, u + d, v, w, d, stripe, 2 if not big else 3, rng)
        stripe_cols(img, u, v + d, d, h, stripe, 3, rng)
        stripe_cols(img, u + d + w, v + d, d, h, stripe, 3, rng)
        for j in range(0, d, 2):
            for i in range(w):
                px[u + d + w + i, v + j] = shade(belly, 0.86) + (255,)
        cells(img, u, v, *full, rng)
    elif role in ("tail1", "tail2"):
        g.box(img, rng, u, v, w, h, d, top=shade(body, 1.05), side=body, bottom=belly, front=body, back=shade(body, 0.85))
        stripe_cols(img, u + d, v, w, d, stripe, 2, rng)
        stripe_cols(img, u, v + d, d, h, stripe, 3, rng)
        stripe_cols(img, u + d + w, v + d, d, h, stripe, 3, rng)
        cells(img, u, v, *full, rng)
    elif role == "tail3":
        if species == "velociraptor":
            g.box(img, rng, u, v, w, h, d, top=accent, side=shade(accent, 0.85), bottom=shade(accent, 0.7), noise=8)
            stripe_cols(img, u + d, v, w, d, stripe, 2, rng)
        else:
            g.box(img, rng, u, v, w, h, d, top=shade(body, 1.02), side=shade(body, 0.95), bottom=belly)
            stripe_cols(img, u + d, v, w, d, stripe, 2, rng)
    elif role in ("neck", "neck2"):
        g.box(img, rng, u, v, w, h, d, top=shade(body, 1.05), side=body, bottom=belly)
        cells(img, u, v, *full, rng)
        if role == "neck2" and stage == "adult" and not female and not alpha and species != "dilophosaurus":
            for i in range(w):
                px[u + d + i, v + d + h - 1] = accent + (255,)
    elif role == "head":
        g.box(img, rng, u, v, w, h, d, top=shade(pal["head"], 1.08), side=pal["head"], bottom=belly)
        cells(img, u, v, *full, rng, amp=0.06)
        if (not female or alpha) and stage == "adult" and species != "dilophosaurus":
            for i in range(w):
                px[u + d + i, v] = accent + (255,)
                px[u + d + i, v + 1] = accent + (255,)
        eyes()
    elif role == "brow":
        g.box(img, rng, u, v, w, h, d, top=shade(pal["head"], 0.8), side=shade(pal["head"], 0.75),
              bottom=shade(pal["head"], 0.6), noise=5)
    elif role == "snout":
        g.box(img, rng, u, v, w, h, d, top=shade(pal["head"], 1.05), side=pal["head"], bottom=belly,
              front=shade(pal["head"], 0.75), noise=6)
        for x0 in (u, u + d + w):
            for i in range(0, d, 2):
                px[x0 + i, v + d + h - 1] = pal["tooth"] + (255,)
        px[u + d + w // 2, v + d + h - 1] = shade(pal["head"], 0.5) + (255,)
    elif role == "jaw":
        g.box(img, rng, u, v, w, h, d, top=shade(belly, 0.8), side=belly, bottom=shade(belly, 0.9),
              front=shade(belly, 0.8), noise=6)
        for x0 in (u, u + d + w):
            for i in range(1, d, 2):
                px[x0 + i, v + d] = pal["tooth"] + (255,)
    elif role == "crest":
        g.box(img, rng, u, v, w, h, d, top=accent, side=accent, bottom=shade(accent, 0.7), noise=6)
        stripe_cols(img, u, v + d, d, h, shade(accent, 0.6), 2, rng)
        stripe_cols(img, u + d + w, v + d, d, h, shade(accent, 0.6), 2, rng)
        if alpha:
            for i in range(d):
                GLOW.append((u + i, v + d + 1, accent))
    elif role == "frill":
        g.box(img, rng, u, v, w, h, d, top=shade(accent, 0.8), side=shade(accent, 0.7), bottom=shade(accent, 0.7), noise=5)
        for (fx, fy, fw, fh) in ((u + d, v + d, w, h), (u + d + w + d, v + d, w, h)):
            for j in range(fh):
                for i in range(fw):
                    dist = abs((i - fw / 2) / (fw / 2)) + abs((j - fh / 2) / (fh / 2))
                    c = accent if int(dist * 3) % 2 == 0 else mixc(accent, stripe, 0.7)
                    px[fx + i, fy + j] = tuple(g.clamp(x + rng.randint(-8, 8)) for x in c) + (255,)
            px[fx + fw // 2, fy + fh // 2] = (250, 240, 180, 255)
            px[fx + fw // 2 - 1, fy + fh // 2] = (30, 20, 20, 255)
            if alpha:
                GLOW.append((fx + fw // 2, fy + fh // 2, (200, 255, 120)))
    elif role == "thigh":
        g.box(img, rng, u, v, w, h, d, top=body, side=shade(body, 0.95), bottom=shade(body, 0.8))
        stripe_cols(img, u, v + d, d, h, stripe, 3, rng)
        stripe_cols(img, u + d + w, v + d, d, h, stripe, 3, rng)
        cells(img, u, v, *full, rng)
    elif role == "lower":
        g.box(img, rng, u, v, w, h, d, top=shade(body, 0.9), side=shade(body, 0.85), bottom=pal["foot"])
        cells(img, u, v, *full, rng, amp=0.07)
    elif role == "foot":
        g.box(img, rng, u, v, w, h, d, top=pal["foot"], side=pal["foot"], bottom=shade(pal["foot"], 0.7), noise=5)
    elif role == "claw":
        g.box(img, rng, u, v, w, h, d, top=pal["bone"], side=pal["bone"], bottom=shade(pal["bone"], 0.8), noise=4)
    elif role == "arm":
        col = accent if (species == "velociraptor" and stage == "adult" and (not female or alpha)) else body
        g.box(img, rng, u, v, w, h, d, top=col, side=col, bottom=shade(col, 0.8), noise=6)
    elif role == "hand":
        g.box(img, rng, u, v, w, h, d, top=pal["foot"], side=pal["foot"], bottom=pal["bone"], noise=4)
    elif role == "plume":
        g.box(img, rng, u, v, w, h, d, top=accent, side=shade(accent, 0.9), bottom=shade(accent, 0.7), noise=8)
        stripe_cols(img, u, v + d, d, h, stripe, 2, rng)
    elif role in ("a_spike", "a_horn"):
        g.box(img, rng, u, v, w, h, d, top=pal["bone"], side=shade(pal["bone"], 0.85), bottom=shade(pal["bone"], 0.7), noise=4)
        if alpha:
            GLOW.append((u + d, v + d, shade(accent, 1.2)))


def paint_theropod(key, variant, female, alpha, seed):
    sp = SPECS[key]
    rng = random.Random(seed)
    pal = palette(sp["id"], variant, female, alpha)
    img = Image.new("RGBA", sp["tex"], (0, 0, 0, 0))
    GLOW.clear()
    for part in sp["adult"] + sp["teen"] + sp["baby"]:
        paint_part(img, rng, sp["id"], part, sp["uv"], pal, female, alpha)
    glow = Image.new("RGBA", sp["tex"], (0, 0, 0, 0))
    gp = glow.load()
    for (x, y, c) in GLOW:
        gp[x, y] = tuple(c) + (255,)
    return img, glow


# =========================================================================== punto de entrada
def run():
    write_java()
    for key, sp in SPECS.items():
        species = sp["id"]
        folder = os.path.join(g.ASSETS, "textures", "entity", species)
        os.makedirs(folder, exist_ok=True)
        for variant in VARIANTS[species]:
            for female in (False, True):
                name = f"{species}_{variant}_{'female' if female else 'male'}.png"
                img, _ = paint_theropod(key, variant, female, False, zlib.crc32(name.encode()))
                img.save(os.path.join(folder, name))
        img, glow = paint_theropod(key, VARIANTS[species][0], False, True, zlib.crc32((species + "alpha").encode()))
        img.save(os.path.join(folder, f"{species}_alpha.png"))
        glow.save(os.path.join(folder, f"{species}_alpha_glow.png"))
    previews()
