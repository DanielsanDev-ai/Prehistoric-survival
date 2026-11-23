#!/usr/bin/env python3
"""
Velociraptor y Tyrannosaurus: definicion UNICA de las piezas del modelo.
De aqui salen (1) el archivo Java con la geometria y las coordenadas de textura,
(2) las texturas pintadas, (3) huevos, nido e iconos y (4) los sonidos.
Se ejecuta desde generate_assets.py.
"""
import os
import random
import subprocess
import tempfile
import zlib

import numpy as np
from PIL import Image, ImageDraw
from scipy.io import wavfile
from scipy.signal import butter, sosfilt

import generate_assets as g

JAVA_DIR = os.path.normpath(os.path.join(g.HERE, "..", "src", "main", "java", "com", "prehistoricsurvival",
                                         "client", "model"))
TEX_BLOCK = os.path.join(g.ASSETS, "textures", "block")
MODELS_BLOCK = os.path.join(g.ASSETS, "models", "block")
STATES = os.path.join(g.ASSETS, "blockstates")
for d in (TEX_BLOCK, MODELS_BLOCK, STATES):
    os.makedirs(d, exist_ok=True)


# =========================================================================== definicion de piezas
# pieza = (nombre, padre, (ox,oy,oz) origen de la caja, (w,h,d) tamano, (px,py,pz) pivote)

def raptor_parts():
    adult = [
        ("body", "adult", (-2.5, -3, -5), (5, 6, 10), (0, 12, 0)),
        ("neck", "adult", (-1.5, -6, -1.5), (3, 6, 3), (0, 10, -4.5)),
        ("head", "neck", (-2, -3, -2), (4, 3, 4), (0, -6, 0)),
        ("snout", "head", (-1.5, -2.5, -6), (3, 2, 4), (0, 0, 0)),
        ("jaw", "head", (-1.5, 0, -4), (3, 1, 4), (0, -0.5, -2)),
        ("tail1", "adult", (-1.5, -1.5, 0), (3, 3, 7), (0, 11.5, 5)),
        ("tail2", "tail1", (-1, -1, 0), (2, 2, 7), (0, 0, 7)),
    ]
    for side, sx in (("r", -1), ("l", 1)):
        adult += [
            (f"thigh_{side}", "adult", (-1.5, 0, -2), (3, 5, 4), (3 * sx, 13, 1)),
            (f"lower_{side}", f"thigh_{side}", (-1, 0, -1), (2, 5, 2), (0, 5, 1)),
            (f"foot_{side}", f"lower_{side}", (-1, 0, -3), (2, 1, 4), (0, 5, 0)),
            (f"claw_{side}", f"foot_{side}", (-0.5, -1, -3.5), (1, 2, 1), (0, 0, 0)),
            (f"arm_{side}", "adult", (-0.5, 0, -0.5), (1, 4, 1), (3 * sx, 12, -3.5)),
        ]
    baby = [
        ("b_body", "baby", (-2, -2, -3), (4, 4, 6), (0, 18, 0)),
        ("b_head", "baby", (-2.5, -4, -3.5), (5, 4, 5), (0, 16.5, -2.5)),
        ("b_snout", "b_head", (-1.5, -2, -6.5), (3, 2, 3), (0, 0, 0)),
        ("b_tail", "baby", (-1, -1, 0), (2, 2, 6), (0, 17.5, 3)),
        ("b_leg_r", "baby", (-1, 0, -1), (2, 4, 2), (-1.5, 20, 0.5)),
        ("b_leg_l", "baby", (-1, 0, -1), (2, 4, 2), (1.5, 20, 0.5)),
    ]
    return adult, baby


def rex_parts():
    adult = [
        ("body", "adult", (-4, -4.5, -7.5), (8, 9, 15), (0, 7, 0)),
        ("neck", "adult", (-3, -6, -3), (6, 6, 6), (0, 3.5, -7)),
        ("head", "neck", (-3, -5, -3), (6, 5, 6), (0, -6, 0)),
        ("snout", "head", (-2.5, -4, -10), (5, 3, 7), (0, 0, 0)),
        ("jaw", "head", (-2.5, 0, -6), (5, 2, 6), (0, -1.5, -3)),
        ("tail1", "adult", (-3, -3, 0), (6, 6, 12), (0, 6, 7.5)),
        ("tail2", "tail1", (-2, -2, 0), (4, 4, 12), (0, 0, 12)),
    ]
    for side, sx in (("r", -1), ("l", 1)):
        adult += [
            (f"thigh_{side}", "adult", (-2.5, 0, -3), (5, 8, 6), (5.5 * sx, 7, 1)),
            (f"lower_{side}", f"thigh_{side}", (-2, 0, -2), (4, 7, 4), (0, 8, 2)),
            (f"foot_{side}", f"lower_{side}", (-2, 0, -5), (4, 2, 7), (0, 7, 0)),
            (f"claw_{side}", f"foot_{side}", (-0.5, -1, -5.5), (1, 2, 1), (0, 0, 0)),
            (f"arm_{side}", "adult", (-1, 0, -1), (2, 4, 2), (4.5 * sx, 6, -6)),
        ]
    baby = [
        ("b_body", "baby", (-3, -3, -4.5), (6, 6, 9), (0, 17, 0)),
        ("b_head", "baby", (-3.5, -5, -4), (7, 5, 6), (0, 15.5, -4)),
        ("b_snout", "b_head", (-2.5, -3, -8), (5, 3, 4), (0, 0, 0)),
        ("b_tail", "baby", (-1.5, -1.5, 0), (3, 3, 9), (0, 16.5, 4.5)),
        ("b_leg_r", "baby", (-1.5, 0, -1.5), (3, 4, 3), (-2.5, 20, 0.5)),
        ("b_leg_l", "baby", (-1.5, 0, -1.5), (3, 4, 3), (2.5, 20, 0.5)),
    ]
    return adult, baby


# stride, swing_speed, neck_lean, crouch_y
SPECS = {
    "VELOCIRAPTOR": dict(id="velociraptor", tex=(128, 64), parts=raptor_parts(), stride=0.9, swing=1.0,
                         lean=0.5, crouch=5.0),
    "TREX": dict(id="tyrannosaurus", tex=(128, 128), parts=rex_parts(), stride=0.55, swing=0.7,
                 lean=0.8, crouch=6.0),
}


def box_size(w, h, d):
    return 2 * d + 2 * w, d + h


def pack(parts, tex_w, tex_h):
    """Empaquetado por filas: devuelve {nombre: (u, v)}."""
    items = sorted(parts, key=lambda p: -box_size(*p[3])[1])
    uv = {}
    x = y = row_h = 0
    for p in items:
        bw, bh = box_size(*p[3])
        if x + bw > tex_w:
            x = 0
            y += row_h
            row_h = 0
        uv[p[0]] = (x, y)
        x += bw
        row_h = max(row_h, bh)
    assert y + row_h <= tex_h, f"No caben las piezas en {tex_w}x{tex_h} (necesita {y + row_h})"
    return uv


def f(v):
    return f"{float(v):g}f"


def write_java():
    out = ["// ARCHIVO GENERADO por tools/theropods.py - NO EDITAR A MANO.",
           "package com.prehistoricsurvival.client.model;", "",
           "/** Geometria y coordenadas de textura de los terópodos (generadas a partir de una unica definicion). */",
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
           "        public final float stride, swingSpeed, neckLean, crouchY;",
           "        public final Part[] adult, baby;",
           "        Spec(int texW, int texH, float stride, float swingSpeed, float neckLean, float crouchY,",
           "             Part[] adult, Part[] baby) {",
           "            this.texW = texW; this.texH = texH; this.stride = stride; this.swingSpeed = swingSpeed;",
           "            this.neckLean = neckLean; this.crouchY = crouchY; this.adult = adult; this.baby = baby;",
           "        }",
           "    }", ""]
    for key, sp in SPECS.items():
        adult, baby = sp["parts"]
        uv = pack(adult + baby, *sp["tex"])
        sp["uv"] = uv

        def fmt(p):
            n, par, o, s, pv = p
            u, v = uv[n]
            return (f'            new Part("{n}", "{par}", {u}, {v}, {f(o[0])}, {f(o[1])}, {f(o[2])}, '
                    f'{s[0]}, {s[1]}, {s[2]}, {f(pv[0])}, {f(pv[1])}, {f(pv[2])})')
        out.append(f"    public static final Spec {key} = new Spec({sp['tex'][0]}, {sp['tex'][1]}, "
                   f"{f(sp['stride'])}, {f(sp['swing'])}, {f(sp['lean'])}, {f(sp['crouch'])},")
        out.append("        new Part[] {")
        out.append(",\n".join(fmt(p) for p in adult))
        out.append("        },")
        out.append("        new Part[] {")
        out.append(",\n".join(fmt(p) for p in baby))
        out.append("        });")
        out.append("")
    out.append("    private TheropodSpecs() {}")
    out.append("}")
    with open(os.path.join(JAVA_DIR, "TheropodSpecs.java"), "w", encoding="utf-8") as fh:
        fh.write("\n".join(out) + "\n")


# =========================================================================== paletas y pintura
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
}
VARIANTS = {"velociraptor": ["olive", "ember", "slate"], "tyrannosaurus": ["forest", "dusk", "ash"]}


def palette(species, variant, female, alpha):
    if alpha:
        p = dict(body=(36, 34, 40), belly=(96, 58, 50), stripe=(132, 26, 26), accent=(204, 162, 42))
        eye = (220, 40, 30)
    else:
        p = dict(PAL[species][variant])
        eye = (232, 200, 48)
        if female:     # hembra: colores mas apagados, sin acento llamativo
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


def role_of(name):
    n = name
    if n.endswith("_r") or n.endswith("_l"):
        n = n[:-2]
    return n


def paint_part(img, rng, species, part, uv, pal, female, alpha):
    name, _, _, (w, h, d), _ = part
    u, v = uv[name]
    role = role_of(name)
    body, belly, stripe, accent = pal["body"], pal["belly"], pal["stripe"], pal["accent"]
    px = img.load()
    big = species == "tyrannosaurus"
    down = (226, 214, 176)
    fluff = mixc(body, down, 0.55)

    if role.startswith("b_"):
        base = role[2:]
        if base in ("body", "tail"):
            g.box(img, rng, u, v, w, h, d, top=fluff, side=fluff, bottom=down, front=fluff, back=fluff, noise=12)
            stripe_cols(img, u + d, v, w, d, mixc(stripe, fluff, 0.4), 2, rng)
        elif base == "head":
            g.box(img, rng, u, v, w, h, d, top=shade(fluff, 1.05), side=fluff, bottom=down, noise=12)
            for cx in (d // 2 - 1, d // 2):
                px[u + cx, v + d + 1] = pal["eye"] + (255,)
                px[u + d + w + cx, v + d + 1] = pal["eye"] + (255,)
        elif base == "snout":
            g.box(img, rng, u, v, w, h, d, top=fluff, side=fluff, bottom=down, front=shade(fluff, 0.8), noise=8)
        else:  # patas
            g.box(img, rng, u, v, w, h, d, top=pal["foot"], side=mixc(pal["foot"], fluff, 0.3),
                  bottom=shade(pal["foot"], 0.7), noise=5)
        return

    if role == "body":
        g.box(img, rng, u, v, w, h, d, top=shade(body, 1.08), side=body, bottom=belly, front=belly, back=shade(body, 0.9))
        stripe_cols(img, u + d, v, w, d, stripe, 2 if not big else 3, rng)
        stripe_cols(img, u, v + d, d, h, stripe, 3, rng)
        stripe_cols(img, u + d + w, v + d, d, h, stripe, 3, rng)
    elif role in ("tail1", "tail2"):
        g.box(img, rng, u, v, w, h, d, top=shade(body, 1.05), side=body, bottom=belly, front=body, back=shade(body, 0.85))
        stripe_cols(img, u + d, v, w, d, stripe, 2, rng)
        stripe_cols(img, u, v + d, d, h, stripe, 3, rng)
        stripe_cols(img, u + d + w, v + d, d, h, stripe, 3, rng)
        if role == "tail2" and not female:
            stripe_cols(img, u + d, v, w, d, accent, 2, rng)  # punta de cola llamativa en machos
    elif role == "neck":
        g.box(img, rng, u, v, w, h, d, top=shade(body, 1.05), side=body, bottom=belly)
        if not female and not alpha:
            for i in range(w):                                 # garganta de color (macho)
                px[u + d + i, v + d + h - 1] = accent + (255,)
    elif role == "head":
        g.box(img, rng, u, v, w, h, d, top=shade(pal["head"], 1.08), side=pal["head"], bottom=belly)
        if not female or alpha:                                # cresta de color (macho y alfa)
            for i in range(w):
                px[u + d + i, v + 0] = accent + (255,)
                px[u + d + i, v + 1] = accent + (255,)
        for cx in (d // 2 - 1, d // 2):                        # ojos (simetricos)
            px[u + cx, v + d + 1] = pal["eye"] + (255,)
            px[u + d + w + cx, v + d + 1] = pal["eye"] + (255,)
    elif role == "snout":
        g.box(img, rng, u, v, w, h, d, top=shade(pal["head"], 1.05), side=pal["head"], bottom=belly,
              front=shade(pal["head"], 0.75), noise=6)
        for x0 in (u, u + d + w):                              # dientes: ultima fila de las caras laterales
            for i in range(0, d, 2):
                px[x0 + i, v + d + h - 1] = pal["tooth"] + (255,)
    elif role == "jaw":
        g.box(img, rng, u, v, w, h, d, top=shade(belly, 0.8), side=belly, bottom=shade(belly, 0.9),
              front=shade(belly, 0.8), noise=6)
        for x0 in (u, u + d + w):
            for i in range(1, d, 2):
                px[x0 + i, v + d] = pal["tooth"] + (255,)
    elif role == "thigh":
        g.box(img, rng, u, v, w, h, d, top=body, side=shade(body, 0.95), bottom=shade(body, 0.8))
        stripe_cols(img, u, v + d, d, h, stripe, 3, rng)
        stripe_cols(img, u + d + w, v + d, d, h, stripe, 3, rng)
    elif role == "lower":
        g.box(img, rng, u, v, w, h, d, top=shade(body, 0.9), side=shade(body, 0.85), bottom=pal["foot"])
    elif role == "foot":
        g.box(img, rng, u, v, w, h, d, top=pal["foot"], side=pal["foot"], bottom=shade(pal["foot"], 0.7), noise=5)
    elif role == "claw":
        g.box(img, rng, u, v, w, h, d, top=pal["bone"], side=pal["bone"], bottom=shade(pal["bone"], 0.8), noise=4)
    elif role == "arm":
        col = accent if (not female and not alpha and not big) else body
        g.box(img, rng, u, v, w, h, d, top=col, side=col, bottom=shade(col, 0.8), noise=6)


def shade(c, f_):
    return g.shade(c, f_)


def paint_theropod(key, variant, female, alpha, seed):
    sp = SPECS[key]
    species = sp["id"]
    adult, baby = sp["parts"]
    uv = sp["uv"]
    rng = random.Random(seed)
    pal = palette(species, variant, female, alpha)
    img = Image.new("RGBA", sp["tex"], (0, 0, 0, 0))
    for part in adult + baby:
        paint_part(img, rng, species, part, uv, pal, female, alpha)
    return img


# =========================================================================== huevos, nido, iconos
EGG = {  # kind: (color base, color de motas, cajas del modelo)
    0: ((232, 220, 190), (150, 110, 70),
        [(5, 0, 5, 11, 3, 11), (4, 3, 4, 12, 8, 12), (5, 8, 5, 11, 11, 11), (6, 11, 6, 10, 12, 10)]),
    1: ((152, 170, 112), (60, 82, 50),
        [(6, 0, 6, 10, 2, 10), (5, 2, 5, 11, 6, 11), (6, 6, 6, 10, 8, 10), (7, 8, 7, 9, 9, 9)]),
    2: ((208, 202, 186), (88, 80, 72),
        [(4, 0, 4, 12, 3, 12), (3, 3, 3, 13, 9, 13), (4, 9, 4, 12, 12, 12), (5, 12, 5, 11, 13, 11)]),
}


def egg_texture(kind, stage, rng):
    base, speck, _ = EGG[kind]
    img = Image.new("RGBA", (16, 16), base + (255,))
    px = img.load()
    for j in range(16):
        for i in range(16):
            n = rng.randint(-8, 8)
            px[i, j] = tuple(g.clamp(c + n) for c in base) + (255,)
    for _ in range(22 if kind != 2 else 14):
        x, y = rng.randint(0, 15), rng.randint(0, 15)
        px[x, y] = speck + (255,)
        if kind == 2 and x < 15:
            px[x + 1, y] = speck + (255,)
    crack = (60, 48, 40, 255)
    if stage >= 1:
        for (x, y) in ((3, 2), (4, 3), (4, 4), (5, 5), (6, 5), (7, 6), (9, 8), (10, 9), (10, 10), (12, 11)):
            px[x, y] = crack
    if stage >= 2:
        for (x, y) in ((8, 3), (8, 4), (9, 5), (9, 6), (10, 6), (6, 9), (5, 10), (5, 11), (4, 12), (2, 9)):
            px[x, y] = crack
        for j in range(6, 10):
            for i in range(6, 10):
                px[i, j] = (44, 34, 28, 255)
    return img


def nest_texture(rng):
    img = Image.new("RGBA", (16, 16), (190, 150, 70, 255))
    px = img.load()
    for j in range(16):
        for i in range(16):
            n = rng.randint(-18, 18)
            px[i, j] = tuple(g.clamp(c + n) for c in (196, 156, 76)) + (255,)
    for _ in range(40):
        x, y = rng.randint(0, 13), rng.randint(0, 15)
        c = rng.choice([(140, 104, 44), (222, 190, 104), (120, 88, 40)])
        for k in range(rng.randint(3, 5)):
            if x + k < 16:
                px[x + k, y] = c + (255,)
    return img


def face(texture="#egg"):
    return {k: {"uv": [0, 0, 16, 16], "texture": texture} for k in ("down", "up", "north", "south", "west", "east")}


def write_json(path, obj):
    import json
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(obj, fh, indent=2)


def make_blocks():
    rng = random.Random(99)
    variants = {}
    for kind in range(3):
        for stage in range(3):
            egg_texture(kind, stage, random.Random(kind * 10 + stage)).save(
                os.path.join(TEX_BLOCK, f"dino_egg_{kind}_{stage}.png"))
            tex = f"prehistoricsurvival:block/dino_egg_{kind}_{stage}"
            elements = [{"from": [b[0], b[1], b[2]], "to": [b[3], b[4], b[5]], "faces": face()} for b in EGG[kind][2]]
            write_json(os.path.join(MODELS_BLOCK, f"dino_egg_{kind}_{stage}.json"),
                       {"parent": "minecraft:block/block", "textures": {"egg": tex, "particle": tex},
                        "elements": elements})
            variants[f"hatch={stage},kind={kind}"] = {"model": f"prehistoricsurvival:block/dino_egg_{kind}_{stage}"}
    write_json(os.path.join(STATES, "dino_egg.json"), {"variants": variants})

    nest_texture(rng).save(os.path.join(TEX_BLOCK, "nest.png"))
    tex = "prehistoricsurvival:block/nest"
    el = [
        {"from": [0, 0, 0], "to": [16, 2, 16], "faces": face("#straw")},
        {"from": [0, 2, 0], "to": [16, 5, 2], "faces": face("#straw")},
        {"from": [0, 2, 14], "to": [16, 5, 16], "faces": face("#straw")},
        {"from": [0, 2, 2], "to": [2, 5, 14], "faces": face("#straw")},
        {"from": [14, 2, 2], "to": [16, 5, 14], "faces": face("#straw")},
    ]
    write_json(os.path.join(MODELS_BLOCK, "nest.json"),
               {"parent": "minecraft:block/block", "textures": {"straw": tex, "particle": tex}, "elements": el})
    write_json(os.path.join(STATES, "nest.json"), {"variants": {"": {"model": "prehistoricsurvival:block/nest"}}})

    items = os.path.join(g.ASSETS, "models", "item")
    write_json(os.path.join(items, "nest.json"), {"parent": "prehistoricsurvival:block/nest"})
    for kind, name in ((0, "dodo_egg"), (1, "velociraptor_egg"), (2, "tyrannosaurus_egg")):
        write_json(os.path.join(items, name + ".json"), {"parent": f"prehistoricsurvival:block/dino_egg_{kind}_0"})
    for name in ("raptor_claw", "rex_tooth", "alpha_essence", "sedative_meat"):
        write_json(os.path.join(items, name + ".json"),
                   {"parent": "minecraft:item/generated", "textures": {"layer0": f"prehistoricsurvival:item/{name}"}})
    for name in ("velociraptor_spawn_egg", "tyrannosaurus_spawn_egg"):
        write_json(os.path.join(items, name + ".json"), {"parent": "minecraft:item/template_spawn_egg"})


def icon_claw():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    bone, dark = (236, 230, 208), (150, 138, 110)
    d.polygon([(3, 13), (4, 8), (7, 4), (12, 2), (13, 3), (11, 6), (9, 10), (6, 13)], fill=bone, outline=dark)
    d.line([(6, 11), (11, 4)], fill=(255, 252, 240))
    d.rectangle([2, 12, 6, 14], fill=(130, 90, 60), outline=(80, 56, 36))
    return img


def icon_tooth():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    bone, dark = (240, 236, 218), (156, 146, 120)
    d.polygon([(4, 2), (12, 2), (13, 5), (10, 14), (8, 15), (6, 14), (3, 5)], fill=bone, outline=dark)
    d.line([(6, 4), (7, 11)], fill=(255, 255, 250))
    d.line([(4, 3), (11, 3)], fill=(214, 204, 180))
    return img


def icon_alpha():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([3, 3, 12, 12], fill=(160, 36, 36), outline=(70, 12, 12))
    d.ellipse([5, 5, 10, 10], fill=(232, 120, 60))
    d.ellipse([6, 6, 8, 8], fill=(255, 230, 150))
    d.point([(2, 8), (13, 8), (8, 2), (8, 13)], fill=(214, 168, 40))
    return img


def icon_sedative():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([2, 4, 12, 13], fill=(112, 90, 140), outline=(56, 40, 80))
    d.ellipse([4, 5, 9, 9], fill=(150, 126, 180))
    d.line([(4, 11), (9, 7)], fill=(190, 170, 214))
    d.rectangle([11, 2, 14, 5], fill=(236, 230, 214), outline=(170, 160, 140))
    d.point([(13, 8), (14, 10), (12, 11)], fill=(120, 220, 140))   # gotas del sedante
    return img


# =========================================================================== sonidos
def write_ogg(folder, name, sig):
    os.makedirs(folder, exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        wav = os.path.join(tmp, "x.wav")
        wavfile.write(wav, g.SR, (np.clip(sig, -1, 1) * 32767).astype(np.int16))
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-ac", "1", "-c:a", "libvorbis",
                        "-q:a", "4", os.path.join(folder, name + ".ogg")], check=True)


def band_noise(dur, lo, hi, rng, attack=0.05, release=0.2):
    n = int(g.SR * dur)
    noise = np.random.default_rng(rng.randint(0, 10 ** 6)).standard_normal(n)
    sos = butter(4, [lo, hi], btype="band", fs=g.SR, output="sos")
    sig = sosfilt(sos, noise)
    e = np.ones(n)
    a, r = int(attack * g.SR), int(release * g.SR)
    e[:a] = np.linspace(0, 1, a)
    e[-r:] = np.linspace(1, 0, r)
    sig = sig * e
    return sig / (np.max(np.abs(sig)) + 1e-9) * 0.7


def thud(rng, f0=70, f1=32, dur=0.4):
    n = int(g.SR * dur)
    t = np.linspace(0, dur, n, endpoint=False)
    freq = np.linspace(f0, f1, n)
    body = np.sin(2 * np.pi * np.cumsum(freq) / g.SR) * np.exp(-t * 11)
    noise = np.random.default_rng(rng.randint(0, 10 ** 6)).standard_normal(n)
    sos = butter(2, 220, btype="low", fs=g.SR, output="sos")
    body += sosfilt(sos, noise) * np.exp(-t * 28) * 0.9
    return body / (np.max(np.abs(body)) + 1e-9) * 0.9


def mix_sigs(*sigs):
    n = max(len(s) for s in sigs)
    out = np.zeros(n)
    for s in sigs:
        out[:len(s)] += s
    return out / (np.max(np.abs(out)) + 1e-9) * 0.85


def make_sounds():
    rng = random.Random(21)
    syn, glide = g.synth, g.glide

    # ------------------------------- VELOCIRAPTOR: chasquidos, siseos, chillidos agudos
    d = os.path.join(g.ASSETS, "sounds", "velociraptor")
    chit = [(900, 200, 1.0), (2300, 300, 0.6)]
    for i, (base, dur) in enumerate(((720, 0.34), (860, 0.46), (640, 0.4)), start=1):
        write_ogg(d, f"ambient{i}", syn(glide(base * 0.9, base * 1.3, base), dur, chit, rng, vib=(26, 0.06),
                                        env=lambda t: 0.45 + 0.55 * np.abs(np.sin(2 * np.pi * 6 * t))))
    for i, dur in enumerate((0.9, 1.1), start=1):
        hiss = band_noise(dur, 3000, 7500, rng, 0.08, 0.35)
        growl = syn(glide(120, 90), dur, [(500, 150, 1.0), (900, 200, 0.5)], rng, noise=0.05, vib=(22, 0.08),
                    env=lambda t: 0.6 + 0.4 * np.sin(2 * np.pi * 9 * t))
        write_ogg(d, f"warn{i}", mix_sigs(hiss, growl * 0.7))
    for i, base in enumerate((620, 700), start=1):
        write_ogg(d, f"attack{i}", syn(glide(base, base * 2.2, base * 1.6), 0.5, [(1400, 300, 1.0), (2800, 400, 0.6)],
                                       rng, noise=0.12, vib=(18, 0.04)))
    for i, base in enumerate((1100, 1250), start=1):
        write_ogg(d, f"hurt{i}", syn(glide(base, base * 0.55), 0.3, [(1500, 300, 1.0), (2700, 400, 0.5)], rng,
                                     noise=0.1, release=0.06))
    write_ogg(d, "death1", syn(glide(1000, 520, 200), 1.0, [(1300, 300, 1.0), (2400, 400, 0.5)], rng, noise=0.1,
                               release=0.3, env=lambda t: 1 - 0.5 * t))
    for i, base in enumerate((520, 600), start=1):
        a = syn(glide(base, base * 2), 0.16, [(1800, 350, 1.0), (3000, 400, 0.5)], rng, attack=0.01, release=0.05)
        gap = np.zeros(int(0.07 * g.SR))
        write_ogg(d, f"call{i}", np.concatenate([a, gap, a * 0.9, gap, a * 1.0]))
    for i, base in enumerate((1900, 2200), start=1):
        a = syn(glide(base, base * 1.2), 0.08, [(2800, 400, 1.0), (4200, 500, 0.5)], rng, attack=0.005, release=0.03)
        gap = np.zeros(int(0.06 * g.SR))
        write_ogg(d, f"chick{i}", np.concatenate([a, gap, a * 0.8, gap, a * 0.6]))

    # ------------------------------- TYRANNOSAURUS: rugidos graves, retumbos y pisadas
    d = os.path.join(g.ASSETS, "sounds", "tyrannosaurus")
    low = [(250, 120, 1.0), (520, 160, 0.7), (1000, 220, 0.3)]
    for i, (base, dur) in enumerate(((62, 1.4), (70, 1.2), (56, 1.6)), start=1):
        write_ogg(d, f"ambient{i}", syn(glide(base, base * 1.3, base * 0.9), dur, low, rng, noise=0.08, vib=(7, 0.03),
                                        attack=0.15, release=0.4,
                                        env=lambda t: 0.6 + 0.4 * np.sin(np.pi * t)))
    roar = [(350, 150, 1.0), (800, 200, 0.8), (1600, 300, 0.4)]
    for i, dur in enumerate((1.7, 1.9), start=1):
        write_ogg(d, f"warn{i}", syn(glide(90, 210, 150, 95), dur, roar, rng, noise=0.16, vib=(12, 0.05),
                                     attack=0.08, release=0.5, env=lambda t: 0.5 + 0.5 * np.sin(np.pi * np.clip(t * 1.2, 0, 1))))
    for i, dur in enumerate((0.9, 1.0), start=1):
        write_ogg(d, f"attack{i}", syn(glide(130, 300, 170), dur, roar, rng, noise=0.2, vib=(15, 0.05),
                                       attack=0.03, release=0.3))
    for i, base in enumerate((190, 220), start=1):
        write_ogg(d, f"hurt{i}", syn(glide(base, base * 0.55), 0.6, roar, rng, noise=0.12, release=0.2))
    write_ogg(d, "death1", syn(glide(170, 120, 70, 45), 2.4, roar, rng, noise=0.14, release=0.9,
                               env=lambda t: 1 - 0.6 * t))
    for i, (f0, f1) in enumerate(((70, 32), (62, 28)), start=1):
        write_ogg(d, f"step{i}", thud(rng, f0, f1))
    for i, base in enumerate((1100, 1350), start=1):
        a = syn(glide(base, base * 1.3), 0.12, [(1600, 300, 1.0), (2800, 400, 0.5)], rng, attack=0.006, release=0.05)
        gap = np.zeros(int(0.08 * g.SR))
        write_ogg(d, f"chick{i}", np.concatenate([a, gap, a * 0.8]))


# =========================================================================== punto de entrada
def run():
    tex_raptor = os.path.join(g.ASSETS, "textures", "entity", "velociraptor")
    tex_rex = os.path.join(g.ASSETS, "textures", "entity", "tyrannosaurus")
    os.makedirs(tex_raptor, exist_ok=True)
    os.makedirs(tex_rex, exist_ok=True)

    write_java()
    for key, species, folder in (("VELOCIRAPTOR", "velociraptor", tex_raptor), ("TREX", "tyrannosaurus", tex_rex)):
        for variant in VARIANTS[species]:
            for female in (False, True):
                name = f"{species}_{variant}_{'female' if female else 'male'}.png"
                paint_theropod(key, variant, female, False, zlib.crc32(name.encode())).save(os.path.join(folder, name))
        paint_theropod(key, VARIANTS[species][0], False, True, zlib.crc32((species + "alpha").encode())).save(
            os.path.join(folder, f"{species}_alpha.png"))

    icon_claw().save(os.path.join(g.TEX_ITEM, "raptor_claw.png"))
    icon_tooth().save(os.path.join(g.TEX_ITEM, "rex_tooth.png"))
    icon_alpha().save(os.path.join(g.TEX_ITEM, "alpha_essence.png"))
    icon_sedative().save(os.path.join(g.TEX_ITEM, "sedative_meat.png"))
    make_blocks()
    make_sounds()
