#!/usr/bin/env python3
"""Huevos (4 especies), nidos (3 tipos), iconos de objetos y arte del libro-guía."""
import json
import math
import os
import random

from PIL import Image, ImageDraw, ImageFilter

import generate_assets as g

TEX_BLOCK = os.path.join(g.ASSETS, "textures", "block")
TEX_GUI = os.path.join(g.ASSETS, "textures", "gui")
MODELS_BLOCK = os.path.join(g.ASSETS, "models", "block")
MODELS_ITEM = os.path.join(g.ASSETS, "models", "item")
STATES = os.path.join(g.ASSETS, "blockstates")
for d in (TEX_BLOCK, TEX_GUI, MODELS_BLOCK, MODELS_ITEM, STATES):
    os.makedirs(d, exist_ok=True)


def wj(path, obj):
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(obj, fh, indent=2)


def clamp(v):
    return g.clamp(v)


def noisy(img, rng, amp):
    px = img.load()
    for j in range(img.height):
        for i in range(img.width):
            r, gg, b, a = px[i, j]
            if a:
                n = rng.randint(-amp, amp)
                px[i, j] = (clamp(r + n), clamp(gg + n), clamp(b + n), a)


# =========================================================================== huevos
# kind: (color base, color de motas, modo de motas, cajas del modelo)
EGG = {
    0: ((232, 220, 190), (150, 110, 70), "dots",
        [(5, 0, 5, 11, 3, 11), (4, 3, 4, 12, 8, 12), (5, 8, 5, 11, 11, 11), (6, 11, 6, 10, 12, 10)]),
    1: ((152, 170, 112), (60, 82, 50), "dots",
        [(6, 0, 6, 10, 2, 10), (5, 2, 5, 11, 6, 11), (6, 6, 6, 10, 8, 10), (7, 8, 7, 9, 9, 9)]),
    2: ((208, 202, 186), (88, 80, 72), "blotch",
        [(4, 0, 4, 12, 3, 12), (3, 3, 3, 13, 9, 13), (4, 9, 4, 12, 12, 12), (5, 12, 5, 11, 13, 11)]),
    3: ((170, 200, 176), (70, 120, 84), "bands",
        [(5, 0, 5, 11, 2, 11), (4, 2, 4, 12, 7, 12), (5, 7, 5, 11, 9, 11), (6, 9, 6, 10, 10, 10)]),
}
NEST_DROP = 4   # el huevo "anidado" se hunde 4 px en la cavidad del nido


def egg_texture(kind, stage, rng):
    base, speck, mode, _ = EGG[kind]
    img = Image.new("RGBA", (16, 16), base + (255,))
    px = img.load()
    for j in range(16):
        for i in range(16):
            n = rng.randint(-8, 8)
            shade_k = 1.0 - 0.06 * (j / 15)
            px[i, j] = tuple(clamp(c * shade_k + n) for c in base) + (255,)
    if mode == "dots":
        for _ in range(22):
            px[rng.randint(0, 15), rng.randint(0, 15)] = speck + (255,)
    elif mode == "blotch":
        for _ in range(12):
            x, y = rng.randint(0, 14), rng.randint(0, 15)
            px[x, y] = speck + (255,)
            px[x + 1, y] = speck + (255,)
    else:   # bandas onduladas
        for j in range(16):
            if j % 4 == 1:
                for i in range(16):
                    px[i, j] = tuple(clamp(c) for c in speck) + (255,)
                    if (i + j) % 3 == 0:
                        px[i, j] = tuple(clamp(c * 1.2) for c in speck) + (255,)
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


def faces(texture, uv=None):
    f = {}
    for k in ("down", "up", "north", "south", "west", "east"):
        f[k] = {"texture": texture}
        if uv:
            f[k]["uv"] = uv
    return f


def make_eggs():
    variants = {}
    for kind in range(4):
        for stage in range(3):
            egg_texture(kind, stage, random.Random(kind * 10 + stage)).save(
                os.path.join(TEX_BLOCK, f"dino_egg_{kind}_{stage}.png"))
            tex = f"prehistoricsurvival:block/dino_egg_{kind}_{stage}"
            for nested in (0, 1):
                dy = -NEST_DROP if nested else 0
                elements = [{"from": [b[0], b[1] + dy, b[2]], "to": [b[3], b[4] + dy, b[5]],
                             "faces": faces("#egg", [0, 0, 16, 16])} for b in EGG[kind][3]]
                name = f"dino_egg_{kind}_{stage}_{'nested' if nested else 'ground'}"
                wj(os.path.join(MODELS_BLOCK, name + ".json"),
                   {"parent": "minecraft:block/block", "textures": {"egg": tex, "particle": tex}, "elements": elements})
                variants[f"hatch={stage},kind={kind},nested={'true' if nested else 'false'}"] = {
                    "model": f"prehistoricsurvival:block/{name}"}
    wj(os.path.join(STATES, "dino_egg.json"), {"variants": variants})
    for kind, name in ((0, "dodo_egg"), (1, "velociraptor_egg"), (2, "tyrannosaurus_egg"), (3, "dilophosaurus_egg")):
        wj(os.path.join(MODELS_ITEM, name + ".json"),
           {"parent": f"prehistoricsurvival:block/dino_egg_{kind}_0_ground"})


# =========================================================================== nidos
def straw_side(rng):
    img = Image.new("RGBA", (16, 16), (196, 156, 76, 255))
    px = img.load()
    for j in range(16):
        for i in range(16):
            n = rng.randint(-16, 16)
            px[i, j] = tuple(clamp(c + n) for c in (196, 156, 76)) + (255,)
    for _ in range(46):
        x, y = rng.randint(0, 13), rng.randint(0, 15)
        c = rng.choice([(140, 104, 44), (226, 194, 108), (120, 88, 40), (170, 130, 60)])
        for k in range(rng.randint(3, 6)):
            if x + k < 16:
                px[x + k, y] = c + (255,)
    return img


def straw_top(rng):
    img = straw_side(rng)
    px = img.load()
    for j in range(16):
        for i in range(16):
            r, gg, b, a = px[i, j]
            px[i, j] = (clamp(r * 0.78), clamp(gg * 0.74), clamp(b * 0.7), 255)
    for _ in range(5):                                   # cascaras y plumitas
        x, y = rng.randint(1, 13), rng.randint(1, 13)
        px[x, y] = (240, 232, 214, 255)
        px[x + 1, y] = (214, 204, 184, 255)
    return img


def mud_side(rng):
    img = Image.new("RGBA", (16, 16), (104, 76, 52, 255))
    px = img.load()
    for j in range(16):
        for i in range(16):
            n = rng.randint(-14, 14)
            px[i, j] = tuple(clamp(c + n) for c in (104, 76, 52)) + (255,)
    for _ in range(14):                                   # guijarros
        x, y = rng.randint(0, 14), rng.randint(0, 14)
        c = rng.choice([(150, 140, 126), (120, 112, 100)])
        px[x, y] = c + (255,)
        px[x + 1, y] = tuple(clamp(v * 0.8) for v in c) + (255,)
    for _ in range(4):                                    # grietas de barro seco
        x, y = rng.randint(1, 13), rng.randint(0, 12)
        for k in range(4):
            px[x + (k % 2), y + k] = (64, 44, 30, 255)
    return img


def mud_top(rng):
    img = mud_side(rng)
    px = img.load()
    for j in range(16):
        for i in range(16):
            r, gg, b, a = px[i, j]
            px[i, j] = (clamp(r * 0.7), clamp(gg * 0.7), clamp(b * 0.72), 255)
    for _ in range(9):                                    # hojas secas
        x, y = rng.randint(1, 13), rng.randint(1, 13)
        px[x, y] = (96, 118, 56, 255)
        px[x + 1, y] = (74, 96, 44, 255)
    return img


def mound_side(rng):
    img = Image.new("RGBA", (16, 16), (92, 66, 44, 255))
    px = img.load()
    for j in range(16):
        for i in range(16):
            n = rng.randint(-12, 12)
            px[i, j] = tuple(clamp(c + n) for c in (92, 66, 44)) + (255,)
    for row in range(0, 16, 4):                          # ramas apiladas con barro entre ellas
        col = rng.choice([(122, 88, 56), (104, 76, 48), (136, 100, 64)])
        for j in range(row, row + 3):
            for i in range(16):
                shade_k = 1.12 if j == row else (0.85 if j == row + 2 else 1.0)
                px[i, j] = tuple(clamp(c * shade_k + rng.randint(-8, 8)) for c in col) + (255,)
        px[rng.randint(2, 13), row + 1] = (70, 50, 32, 255)
    return img


def mound_top(rng):
    img = mound_side(rng)
    px = img.load()
    for j in range(16):
        for i in range(16):
            r, gg, b, a = px[i, j]
            px[i, j] = (clamp(r * 0.66), clamp(gg * 0.64), clamp(b * 0.62), 255)
    for _ in range(10):
        x, y = rng.randint(1, 13), rng.randint(1, 13)
        px[x, y] = (84, 110, 52, 255)
    return img


def make_nests():
    for old in ("nest.png",):
        p = os.path.join(TEX_BLOCK, old)
        if os.path.exists(p):
            os.remove(p)
    rng = random.Random(77)
    kinds = {"nest": (straw_side, straw_top, 12, 16, 3, False),
             "mud_nest": (mud_side, mud_top, 12, 16, 2, True),
             "mound_nest": (mound_side, mound_top, 12, 19, 4, True)}
    for name, (side_fn, top_fn, floor, rim_top, wall, twigs) in kinds.items():
        side_fn(rng).save(os.path.join(TEX_BLOCK, f"{name}_side.png"))
        top_fn(rng).save(os.path.join(TEX_BLOCK, f"{name}_top.png"))
        side, top = f"prehistoricsurvival:block/{name}_side", f"prehistoricsurvival:block/{name}_top"
        base_faces = faces("#side", [0, 0, 16, 16])
        base_faces["up"] = {"texture": "#top", "uv": [0, 0, 16, 16]}
        el = [{"from": [0, 0, 0], "to": [16, floor, 16], "faces": base_faces},
              {"from": [0, floor, 0], "to": [16, rim_top, wall], "faces": faces("#side")},
              {"from": [0, floor, 16 - wall], "to": [16, rim_top, 16], "faces": faces("#side")},
              {"from": [0, floor, wall], "to": [wall, rim_top, 16 - wall], "faces": faces("#side")},
              {"from": [16 - wall, floor, wall], "to": [16, rim_top, 16 - wall], "faces": faces("#side")}]
        if twigs:   # ramitas que sobresalen del borde
            el += [{"from": [2, rim_top, 1], "to": [3, rim_top + 2, 5], "faces": faces("#side")},
                   {"from": [12, rim_top, 10], "to": [14, rim_top + 1, 11], "faces": faces("#side")},
                   {"from": [6, rim_top, 13], "to": [7, rim_top + 2, 16], "faces": faces("#side")}]
        wj(os.path.join(MODELS_BLOCK, name + ".json"),
           {"parent": "minecraft:block/block", "textures": {"side": side, "top": top, "particle": side}, "elements": el})
        wj(os.path.join(STATES, name + ".json"), {"variants": {"": {"model": f"prehistoricsurvival:block/{name}"}}})
        wj(os.path.join(MODELS_ITEM, name + ".json"), {"parent": f"prehistoricsurvival:block/{name}"})


# =========================================================================== iconos
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
    d.point([(13, 8), (14, 10), (12, 11)], fill=(120, 220, 140))
    return img


def icon_gland():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([3, 4, 12, 13], fill=(98, 160, 70), outline=(40, 80, 34))
    d.ellipse([5, 5, 9, 8], fill=(170, 230, 120))
    d.line([(7, 13), (6, 15)], fill=(120, 60, 80))
    d.line([(9, 13), (10, 15)], fill=(120, 60, 80))
    d.point([(11, 7), (10, 10)], fill=(60, 120, 50))
    d.point([(8, 2), (9, 1), (11, 2)], fill=(150, 255, 90))   # gotas de veneno
    return img


def icon_glob():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([4, 4, 12, 12], fill=(110, 200, 60), outline=(50, 110, 30))
    d.ellipse([5, 5, 8, 8], fill=(200, 255, 150))
    d.point([(2, 7), (13, 10), (7, 13), (11, 3)], fill=(130, 230, 70))
    return img


def icon_guide():
    """Libro de explorador paleontologico: cuero marron, esquinas y cierre dorados, emblema de amonites."""
    S = 16
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    px = img.load()
    leather, dark, gold, bone = (112, 68, 38), (62, 36, 20), (222, 178, 76), (236, 226, 196)
    for j in range(1, 15):
        for i in range(2, 14):
            px[i, j] = leather + (255,)
    for j in range(1, 15):                       # lomo
        for i in range(2, 4):
            px[i, j] = dark + (255,)
    for i in range(2, 14):
        px[i, 1] = dark + (255,)
        px[i, 14] = dark + (255,)
    for j in range(1, 15):
        px[13, j] = dark + (255,)
    for (x, y) in ((4, 2), (12, 2), (4, 13), (12, 13), (5, 2), (4, 3), (11, 2), (12, 3), (5, 13), (4, 12), (11, 13), (12, 12)):
        px[x, y] = gold + (255,)                 # esquinas
    for j in range(4, 12):                       # cierre
        px[13, j] = gold + (255,)
    # amonite
    spiral = [(8, 8), (9, 8), (9, 7), (8, 6), (7, 6), (6, 7), (6, 9), (7, 10), (9, 10), (10, 9)]
    for (x, y) in spiral:
        px[x, y] = bone + (255,)
    px[8, 8] = gold + (255,)
    for (x, y) in ((10, 8), (10, 7), (9, 6)):
        px[x, y] = gold + (255,)
    return img


# =========================================================================== arte del libro (GUI)
def leather_texture(w, h, base, rng, grain=14):
    img = Image.new("RGBA", (w, h), base + (255,))
    px = img.load()
    for j in range(h):
        for i in range(w):
            n = rng.randint(-grain, grain)
            px[i, j] = tuple(clamp(c + n) for c in base) + (255,)
    for _ in range(w * h // 18):               # poros del cuero
        x, y = rng.randint(0, w - 1), rng.randint(0, h - 1)
        r, gg, b, a = px[x, y]
        px[x, y] = (clamp(r * 0.82), clamp(gg * 0.82), clamp(b * 0.82), 255)
    return img


def make_cover():
    W, H = 146, 180
    rng = random.Random(5)
    img = leather_texture(W, H, (100, 58, 32), rng)
    px = img.load()
    # viñeta: bordes mas oscuros
    for j in range(H):
        for i in range(W):
            d = min(i, j, W - 1 - i, H - 1 - j)
            k = 0.72 + 0.28 * min(1.0, d / 20)
            r, gg, b, a = px[i, j]
            px[i, j] = (clamp(r * k), clamp(gg * k), clamp(b * k), 255)
    dr = ImageDraw.Draw(img)
    gold, gold_d, gold_l = (214, 170, 78), (140, 100, 40), (246, 214, 130)
    # lomo con nervios dorados
    dr.rectangle([0, 0, 11, H], fill=(70, 38, 20))
    for y in (14, 44, 90, 136, 166):
        dr.rectangle([0, y, 11, y + 2], fill=gold_d)
        dr.line([(0, y), (11, y)], fill=gold)
    for y in range(8, H, 12):                  # puntadas
        dr.line([(12, y), (12, y + 5)], fill=(190, 150, 90))
    # marco dorado doble
    dr.rectangle([19, 9, W - 9, H - 10], outline=gold_d)
    dr.rectangle([22, 12, W - 12, H - 13], outline=gold)
    for (cx, cy) in ((22, 12), (W - 12, 12), (22, H - 13), (W - 12, H - 13)):      # esquinas
        dr.polygon([(cx, cy - 4), (cx + 4, cy), (cx, cy + 4), (cx - 4, cy)], fill=gold_l, outline=gold_d)
    # placa del titulo
    dr.rectangle([28, 18, W - 18, 58], fill=(58, 32, 18), outline=gold)
    dr.rectangle([30, 20, W - 20, 56], outline=gold_d)
    # huesos cruzados
    cx, cy = 80, 104
    bone, bone_d = (234, 224, 190), (150, 136, 100)
    for ang in (35, -35):
        a = math.radians(ang)
        dx, dy = math.cos(a) * 38, math.sin(a) * 38
        dr.line([(cx - dx, cy - dy), (cx + dx, cy + dy)], fill=bone_d, width=6)
        dr.line([(cx - dx, cy - dy), (cx + dx, cy + dy)], fill=bone, width=4)
        for sgn in (-1, 1):
            ex, ey = cx + sgn * dx, cy + sgn * dy
            for off in (-3, 3):
                px_, py_ = ex + off * math.sin(a), ey - off * math.cos(a)
                dr.ellipse([px_ - 3, py_ - 3, px_ + 3, py_ + 3], fill=bone, outline=bone_d)
    # amonite (fosil en espiral) sobre un medallon
    dr.ellipse([cx - 29, cy - 29, cx + 29, cy + 29], fill=(66, 38, 22), outline=gold)
    dr.ellipse([cx - 26, cy - 26, cx + 26, cy + 26], outline=gold_d)
    pts = []
    turns = 3.1
    steps = 240
    for k in range(steps):
        t = k / steps * turns * 2 * math.pi
        r = 2.2 + t * 1.15
        pts.append((cx + r * math.cos(t), cy + r * math.sin(t)))
    for k in range(1, len(pts)):
        w = 1 + int(k / steps * 4)
        dr.line([pts[k - 1], pts[k]], fill=(222, 190, 112), width=w)
    for k in range(10, steps, 7):              # costillas del amonite
        t = k / steps * turns * 2 * math.pi
        r0, r1 = 2.2 + t * 1.15, 2.2 + t * 1.15 + 3.2 + k / steps * 3
        dr.line([(cx + r0 * math.cos(t), cy + r0 * math.sin(t)),
                 (cx + r1 * math.cos(t + 0.15), cy + r1 * math.sin(t + 0.15))], fill=(150, 108, 54), width=1)
    dr.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], fill=gold_l)
    # cierre de cuero
    dr.rectangle([W - 10, 84, W - 1, 100], fill=(70, 38, 20), outline=gold_d)
    dr.ellipse([W - 8, 87, W - 3, 97], fill=gold, outline=gold_d)
    img = img.filter(ImageFilter.UnsharpMask(radius=1, percent=60, threshold=2))
    img.save(os.path.join(TEX_GUI, "guide_cover.png"))


def make_pages():
    W, H = 292, 180
    rng = random.Random(6)
    img = leather_texture(W, H, (84, 48, 28), rng, 10)
    px = img.load()
    page = (236, 222, 184)
    for j in range(5, H - 5):
        for i in range(5, W - 5):
            n = rng.randint(-7, 7)
            k = 1.0
            # viñeta del papel y sombra del lomo central
            ed = min(i - 5, W - 6 - i, j - 5, H - 6 - j)
            k *= 0.88 + 0.12 * min(1.0, ed / 14)
            cd = abs(i - W / 2)
            if cd < 16:
                k *= 0.78 + 0.22 * (cd / 16)
            px[i, j] = tuple(clamp(c * k + n) for c in page) + (255,)
    # fibras del papel
    for _ in range(260):
        x, y = rng.randint(7, W - 12), rng.randint(7, H - 8)
        L = rng.randint(3, 9)
        for k in range(L):
            r, gg, b, a = px[x + k, y]
            px[x + k, y] = (clamp(r * 0.95), clamp(gg * 0.94), clamp(b * 0.9), 255)
    dr = ImageDraw.Draw(img)
    # canto de las paginas apiladas
    for k in range(4):
        dr.line([(5 + k, 6 + k), (5 + k, H - 7 - k)], fill=(190, 172, 130))
        dr.line([(W - 6 - k, 6 + k), (W - 6 - k, H - 7 - k)], fill=(190, 172, 130))
    dr.line([(W // 2, 5), (W // 2, H - 6)], fill=(120, 94, 58))
    dr.line([(W // 2 - 1, 5), (W // 2 - 1, H - 6)], fill=(156, 128, 84))
    # filete decorativo
    for x0, x1 in ((16, 134), (160, 278)):
        dr.line([(x0, 15), (x1, 15)], fill=(150, 112, 62))
        dr.line([(x0, 163), (x1, 163)], fill=(190, 156, 100))
    # esquinas doradas del cuero
    for (cx, cy) in ((2, 2), (W - 3, 2), (2, H - 3), (W - 3, H - 3)):
        dr.rectangle([cx - 2, cy - 2, cx + 2, cy + 2], fill=(214, 170, 78))
    # marca de agua: amonite tenue en la esquina inferior derecha
    cx, cy = W - 44, H - 40
    for k in range(1, 160):
        t = k / 160 * 2.6 * math.pi * 2
        r = 1.5 + t * 0.95
        x, y = int(cx + r * math.cos(t)), int(cy + r * math.sin(t))
        if 0 <= x < W and 0 <= y < H:
            r_, g_, b_, a = px[x, y]
            px[x, y] = (clamp(r_ * 0.9), clamp(g_ * 0.88), clamp(b_ * 0.82), 255)
    img.save(os.path.join(TEX_GUI, "guide_pages.png"))


def run():
    make_eggs()
    make_nests()
    for name, fn in (("raptor_claw", icon_claw), ("rex_tooth", icon_tooth), ("alpha_essence", icon_alpha),
                     ("sedative_meat", icon_sedative), ("venom_gland", icon_gland), ("venom_glob", icon_glob),
                     ("explorer_guide", icon_guide)):
        fn().save(os.path.join(g.TEX_ITEM, name + ".png"))
        wj(os.path.join(MODELS_ITEM, name + ".json"),
           {"parent": "minecraft:item/generated", "textures": {"layer0": f"prehistoricsurvival:item/{name}"}})
    for name in ("velociraptor_spawn_egg", "tyrannosaurus_spawn_egg", "dilophosaurus_spawn_egg"):
        wj(os.path.join(MODELS_ITEM, name + ".json"), {"parent": "minecraft:item/template_spawn_egg"})
    make_cover()
    make_pages()
