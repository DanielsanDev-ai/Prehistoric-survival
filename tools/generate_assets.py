#!/usr/bin/env python3
"""
Genera las texturas y los sonidos del mod de forma procedural (todo original).

Uso:   python3 tools/generate_assets.py
Requiere: Pillow, numpy, scipy y ffmpeg (con libvorbis).

IMPORTANTE: las coordenadas de las cajas (BOXES) deben coincidir con los texOffs de
client/model/DodoModel.java. Si cambias el modelo, cambia aqui las cajas.
"""
import os
import random
import subprocess
import tempfile
import zlib

import numpy as np
from PIL import Image, ImageDraw
from scipy.io import wavfile

HERE = os.path.dirname(os.path.abspath(__file__))
ASSETS = os.path.normpath(os.path.join(HERE, "..", "src", "main", "resources", "assets", "prehistoricsurvival"))
TEX_DODO = os.path.join(ASSETS, "textures", "entity", "dodo")
TEX_ITEM = os.path.join(ASSETS, "textures", "item")
SND_DODO = os.path.join(ASSETS, "sounds", "dodo")
for d in (TEX_DODO, TEX_ITEM, SND_DODO):
    os.makedirs(d, exist_ok=True)


# --------------------------------------------------------------------------- utilidades de color
def clamp(v):
    return max(0, min(255, int(v)))


def shade(c, f):
    return (clamp(c[0] * f), clamp(c[1] * f), clamp(c[2] * f))


def mix(a, b, t):
    return tuple(clamp(a[i] * (1 - t) + b[i] * t) for i in range(3))


def fill(img, x, y, w, h, color, rng, noise=9, vgrad=0.0):
    px = img.load()
    for j in range(h):
        for i in range(w):
            f = 1.0 + vgrad * (j / max(1, h - 1) - 0.5)
            n = rng.randint(-noise, noise)
            r, g, b = (clamp(color[k] * f + n) for k in range(3))
            px[x + i, y + j] = (r, g, b, 255)


def box(img, rng, u, v, w, h, d, top, side, bottom, front=None, back=None, noise=9):
    """Pinta las 6 caras de una caja con la disposicion estandar de Minecraft."""
    front = front or side
    back = back or side
    fill(img, u + d, v, w, d, top, rng, noise)                    # arriba
    fill(img, u + d + w, v, w, d, bottom, rng, noise)             # abajo
    fill(img, u, v + d, d, h, shade(side, 0.9), rng, noise, 0.25)       # lado derecho
    fill(img, u + d, v + d, w, h, front, rng, noise, 0.25)        # frente (-Z)
    fill(img, u + d + w, v + d, d, h, shade(side, 0.9), rng, noise, 0.25)  # lado izquierdo
    fill(img, u + 2 * d + w, v + d, w, h, back, rng, noise, 0.25)  # atras


def streaks(img, rng, x, y, w, h, color, count):
    px = img.load()
    for _ in range(count):
        j = rng.randint(0, h - 1)
        i0 = rng.randint(0, max(0, w - 3))
        for i in range(i0, min(w, i0 + rng.randint(2, 4))):
            px[x + i, y + j] = color + (255,)


# --------------------------------------------------------------------------- paletas
BASE = {
    "brown": dict(body=(128, 94, 62), belly=(176, 146, 108), wing=(98, 70, 46), tail=(214, 204, 186)),
    "grey":  dict(body=(120, 124, 128), belly=(176, 178, 176), wing=(86, 90, 98), tail=(226, 226, 220)),
    "sand":  dict(body=(176, 148, 100), belly=(214, 194, 150), wing=(138, 110, 70), tail=(238, 228, 200)),
}


def palette(variant, female, alpha):
    if alpha:
        return dict(body=(58, 62, 76), belly=(104, 108, 120), wing=(36, 38, 50), tail=(200, 170, 80),
                    head=(150, 154, 166), beak=(214, 168, 40), beak_tip=(120, 84, 20),
                    leg=(190, 120, 40), eye=(220, 40, 30), accent=(214, 168, 40))
    p = dict(BASE[variant])
    if female:   # hembra: cabeza mas clara, pico apagado, tonos mas suaves
        p.update(head=mix(p["belly"], (200, 200, 196), 0.4), beak=(168, 172, 84), beak_tip=(90, 90, 40),
                 leg=(190, 160, 90), eye=(20, 16, 16), accent=p["belly"])
    else:        # macho: cabeza gris azulada, pico amarillo vivo, parche dorado en el ala
        p.update(head=(122, 132, 146), beak=(222, 196, 64), beak_tip=(110, 84, 24),
                 leg=(214, 150, 50), eye=(20, 16, 16), accent=(210, 170, 70))
    return p


def paint_dodo(variant, female, alpha, seed):
    rng = random.Random(seed)
    p = palette(variant, female, alpha)
    img = Image.new("RGBA", (64, 96), (0, 0, 0, 0))

    # ---- ADULTO (mismos valores que DodoModel: texOffs y tamanos de caja)
    box(img, rng, 0, 0, 8, 8, 10, top=shade(p["body"], 1.05), side=p["body"], bottom=p["belly"],
        front=p["belly"], back=shade(p["body"], 0.9))                      # cuerpo
    streaks(img, rng, 0, 10, 10, 8, shade(p["body"], 0.75), 10)            # plumas laterales
    streaks(img, rng, 18, 10, 10, 8, shade(p["body"], 0.75), 10)
    box(img, rng, 36, 0, 6, 4, 3, top=p["tail"], side=p["tail"], bottom=shade(p["tail"], 0.85))    # cola
    box(img, rng, 0, 18, 3, 5, 3, top=p["head"], side=shade(p["head"], 0.95), bottom=shade(p["head"], 0.8))  # cuello
    box(img, rng, 0, 28, 5, 4, 5, top=shade(p["head"], 1.1), side=p["head"], bottom=shade(p["head"], 0.85))  # cabeza
    box(img, rng, 20, 28, 3, 3, 4, top=p["beak"], side=p["beak"], bottom=shade(p["beak"], 0.8),
        front=p["beak_tip"], back=p["beak"], noise=5)                       # pico
    box(img, rng, 0, 40, 2, 6, 2, top=p["leg"], side=p["leg"], bottom=shade(p["leg"], 0.7), noise=5)   # pata dcha
    box(img, rng, 8, 40, 2, 6, 2, top=p["leg"], side=p["leg"], bottom=shade(p["leg"], 0.7), noise=5)   # pata izq
    box(img, rng, 0, 50, 1, 5, 6, top=p["wing"], side=p["wing"], bottom=p["wing"])                     # ala dcha
    box(img, rng, 14, 50, 1, 5, 6, top=p["wing"], side=p["wing"], bottom=p["wing"])                    # ala izq
    px = img.load()
    # ojos (centro de las caras laterales de la cabeza: simetricos por si el espejo cambia)
    for (x, y) in ((2, 34), (12, 34)):
        px[x, y] = p["eye"] + (255,)
    # puntas de ala de color (macho/alfa): 2 filas inferiores de las caras laterales, simetrico
    if alpha or not female:
        for x0, x1 in ((0, 6), (7, 13), (14, 20), (21, 27)):
            for y in (59, 60):
                for x in range(x0, x1):
                    px[x, y] = p["accent"] + (255,)

    # ---- BEBE
    down = (236, 226, 190)  # plumon
    fluff = mix(p["belly"], down, 0.6)
    box(img, rng, 34, 36, 7, 7, 8, top=fluff, side=fluff, bottom=down, front=down, back=fluff, noise=12)   # cuerpo
    box(img, rng, 34, 52, 6, 6, 6, top=shade(fluff, 1.05), side=fluff, bottom=down, noise=12)              # cabeza
    box(img, rng, 22, 36, 2, 2, 3, top=p["beak"], side=p["beak"], bottom=shade(p["beak"], 0.8),
        front=p["beak_tip"], back=p["beak"], noise=4)                                                     # pico
    box(img, rng, 16, 42, 2, 4, 2, top=p["leg"], side=p["leg"], bottom=shade(p["leg"], 0.7), noise=4)     # patas
    box(img, rng, 24, 42, 2, 4, 2, top=p["leg"], side=p["leg"], bottom=shade(p["leg"], 0.7), noise=4)
    box(img, rng, 54, 0, 1, 3, 4, top=fluff, side=fluff, bottom=fluff, noise=12)                          # alas
    box(img, rng, 36, 8, 1, 3, 4, top=fluff, side=fluff, bottom=fluff, noise=12)
    for (x, y) in ((36, 60), (37, 60), (48, 60), (49, 60)):
        px[x, y] = p["eye"] + (255,)

    # ---- ADOLESCENTE (region y >= 64; coincide con DodoModel: grupo "teen")
    tb = mix(p["body"], down, 0.2)
    tbelly = mix(p["belly"], down, 0.3)
    box(img, rng, 0, 64, 7, 7, 9, top=shade(tb, 1.05), side=tb, bottom=tbelly, front=tbelly, back=shade(tb, 0.9))   # cuerpo
    streaks(img, rng, 0, 73, 9, 7, shade(tb, 0.75), 8)
    streaks(img, rng, 16, 73, 9, 7, shade(tb, 0.75), 8)
    box(img, rng, 32, 64, 3, 6, 3, top=p["head"], side=shade(p["head"], 0.95), bottom=shade(p["head"], 0.8))        # cuello
    box(img, rng, 44, 64, 5, 5, 5, top=shade(p["head"], 1.1), side=p["head"], bottom=shade(p["head"], 0.85))       # cabeza
    box(img, rng, 0, 80, 3, 3, 4, top=p["beak"], side=p["beak"], bottom=shade(p["beak"], 0.8),
        front=p["beak_tip"], back=p["beak"], noise=5)                                                             # pico
    box(img, rng, 14, 80, 2, 7, 2, top=p["leg"], side=p["leg"], bottom=shade(p["leg"], 0.7), noise=5)             # patas
    box(img, rng, 22, 80, 2, 7, 2, top=p["leg"], side=p["leg"], bottom=shade(p["leg"], 0.7), noise=5)
    box(img, rng, 30, 80, 1, 4, 5, top=shade(tb, 0.85), side=shade(tb, 0.85), bottom=shade(tb, 0.85))             # alas
    box(img, rng, 42, 80, 1, 4, 5, top=shade(tb, 0.85), side=shade(tb, 0.85), bottom=shade(tb, 0.85))
    box(img, rng, 0, 89, 5, 3, 2, top=p["tail"], side=p["tail"], bottom=shade(p["tail"], 0.85))                    # cola
    # cresta de plumas (solo la muestran los alfas): adulto y adolescente comparten esta region
    box(img, rng, 50, 89, 3, 3, 2, top=p["accent"], side=shade(p["accent"], 0.9), bottom=shade(p["accent"], 0.7), noise=6)
    for (x, y) in ((46, 70), (56, 70)):
        px[x, y] = p["eye"] + (255,)
    return img


def dodo_glow(accent=(214, 168, 40), eye=(220, 40, 30)):
    """Brillo del dodo alfa: ojos (adulto, cria y adolescente) y plumas de la cresta."""
    glow = Image.new("RGBA", (64, 96), (0, 0, 0, 0))
    gp = glow.load()
    for (x, y) in ((2, 34), (12, 34), (36, 60), (37, 60), (48, 60), (49, 60), (46, 70), (56, 70)):
        gp[x, y] = eye + (255,)
    for x in range(52, 55):
        for y in range(91, 94):
            gp[x, y] = accent + (255,)
    return glow


# --------------------------------------------------------------------------- iconos de objetos (16x16)
def icon_meat(cooked):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    base = (150, 84, 40) if cooked else (214, 96, 96)
    dark = shade(base, 0.7)
    light = shade(base, 1.25)
    d.ellipse([2, 4, 12, 13], fill=base, outline=dark)
    d.ellipse([4, 5, 9, 9], fill=light)
    if cooked:
        d.line([(5, 11), (10, 6)], fill=dark)
        d.line([(7, 12), (11, 8)], fill=dark)
    else:
        d.line([(4, 10), (9, 6)], fill=(240, 190, 190))
    d.rectangle([11, 2, 14, 5], fill=(236, 230, 214), outline=(170, 160, 140))   # hueso
    d.point([(10, 1), (15, 1), (10, 6), (15, 6)], fill=(236, 230, 214))
    return img


def icon_feather():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    vane = (150, 112, 76)
    vane_d = (104, 74, 48)
    d.polygon([(13, 2), (14, 3), (9, 9), (3, 13), (2, 12), (4, 6), (8, 3)], fill=vane, outline=vane_d)
    d.line([(14, 2), (2, 14)], fill=(236, 226, 200), width=1)
    for k in range(3):
        d.line([(10 - k * 2, 4 + k * 2), (8 - k * 2, 6 + k * 2 - 1)], fill=vane_d)
    d.point([(1, 15), (2, 15)], fill=(236, 226, 200))
    return img


# --------------------------------------------------------------------------- sonido
SR = 44100


def synth(f0, dur, formants, rng, noise=0.04, vib=(5.5, 0.012), attack=0.012, release=0.08, env=None):
    """Voz sintetica: fuente armonica con trazo de tono f0(t01) filtrada por formantes."""
    n = int(SR * dur)
    t = np.linspace(0, dur, n, endpoint=False)
    t01 = t / dur
    f = f0(t01) * (1 + vib[1] * np.sin(2 * np.pi * vib[0] * t + rng.random() * 6.28))
    phase = 2 * np.pi * np.cumsum(f) / SR
    sig = np.zeros(n)
    for h in range(1, 40):
        fh = f * h
        amp = 0.04 + sum(w * np.exp(-0.5 * ((fh - fc) / bw) ** 2) for fc, bw, w in formants)
        amp = amp / (h ** 0.6)
        amp = np.where(fh < SR / 2 - 500, amp, 0)
        sig += amp * np.sin(h * phase)
    sig += noise * np.random.default_rng(rng.randint(0, 10**6)).standard_normal(n) * np.hanning(n)
    e = np.ones(n)
    a, r = int(attack * SR), int(release * SR)
    e[:a] = np.linspace(0, 1, a)
    e[-r:] = np.linspace(1, 0, r)
    if env is not None:
        e = e * env(t01)
    sig = sig * e
    sig = sig / (np.max(np.abs(sig)) + 1e-9) * 0.8
    return sig


def write_ogg(name, sig):
    with tempfile.TemporaryDirectory() as tmp:
        wav = os.path.join(tmp, "x.wav")
        wavfile.write(wav, SR, (sig * 32767).astype(np.int16))
        out = os.path.join(SND_DODO, name + ".ogg")
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-ac", "1", "-c:a", "libvorbis", "-q:a", "4", out],
                       check=True)


def glide(*points):
    xs = np.linspace(0, 1, len(points))
    return lambda t: np.interp(t, xs, points)


def make_sounds():
    rng = random.Random(7)
    open_vowel = [(650, 180, 1.0), (1150, 220, 0.7), (2500, 300, 0.25)]
    nasal = [(450, 120, 1.0), (1700, 260, 0.8), (2700, 300, 0.3)]

    # Graznido ambiental: "uuh-OOP" grave y comico, 3 variantes
    for i, (base, dur) in enumerate(((190, 0.62), (170, 0.78), (215, 0.55)), start=1):
        sig = synth(glide(base * 1.0, base * 1.35, base * 0.8), dur, open_vowel, rng,
                    env=lambda t: 0.6 + 0.4 * np.sin(np.pi * np.clip(t * 1.4, 0, 1)))
        write_ogg(f"ambient{i}", sig)
    # Dolor: chillido corto y agudo
    for i, base in enumerate((420, 480), start=1):
        write_ogg(f"hurt{i}", synth(glide(base, base * 1.5, base * 0.9), 0.32, nasal, rng, noise=0.08, release=0.06))
    # Muerte: tono que cae, largo
    write_ogg("death1", synth(glide(330, 260, 150, 90), 1.1, open_vowel, rng, noise=0.06, release=0.3,
                              env=lambda t: 1 - 0.5 * t))
    # Alarma: dos graznidos fuertes seguidos
    for i, base in enumerate((300, 340), start=1):
        a = synth(glide(base, base * 1.4), 0.34, nasal, rng, noise=0.07)
        b = synth(glide(base * 1.1, base * 1.55), 0.38, nasal, rng, noise=0.07)
        gap = np.zeros(int(0.07 * SR))
        write_ogg(f"alert{i}", np.concatenate([a, gap, b]))
    # Cria: pios agudos
    for i, base in enumerate((1500, 1750), start=1):
        a = synth(glide(base, base * 1.25), 0.09, [(2400, 400, 1.0), (3800, 500, 0.5)], rng, attack=0.005, release=0.03)
        gap = np.zeros(int(0.06 * SR))
        write_ogg(f"chick{i}", np.concatenate([a, gap, a * 0.8, gap, a * 0.6]))


def main():
    """Genera TODOS los recursos del mod. Cada modulo es independiente y se puede ejecutar por separado."""
    import shutil
    snd = os.path.join(ASSETS, "sounds")
    if os.path.isdir(snd):
        shutil.rmtree(snd)          # evita sonidos antiguos huerfanos
    os.makedirs(SND_DODO, exist_ok=True)

    for variant in BASE:
        for female in (False, True):
            name = f"dodo_{variant}_{'female' if female else 'male'}.png"
            paint_dodo(variant, female, False, zlib.crc32(f'{variant}{female}'.encode())).save(os.path.join(TEX_DODO, name))
    paint_dodo("brown", False, True, 4242).save(os.path.join(TEX_DODO, "dodo_alpha.png"))
    dodo_glow().save(os.path.join(TEX_DODO, "dodo_alpha_glow.png"))

    import theropods
    theropods.run()
    import blocks_items
    blocks_items.run()
    import voices
    voices.run()
    import guide_content
    guide_content.run()
    import data_gen
    data_gen.run()
    print("Recursos generados en", ASSETS)


if __name__ == "__main__":
    main()
