#!/usr/bin/env python3
"""
Voces de las criaturas, 100% sintetizadas (originales, sin muestras de terceros).

Modelo: fuente glotal (diente de sierra asimétrico con jitter y subarmónico) -> banco de filtros de formantes
(dos "vocales" con transición) + soplido -> rugosidad (modulación) -> saturación suave -> reverberación.
Es mucho más natural que la suma de senos anterior, pero sigue siendo síntesis: no son grabaciones.
"""
import os
import random
import subprocess
import tempfile

import numpy as np
from scipy.io import wavfile
from scipy.signal import butter, fftconvolve, sosfilt

import generate_assets as g

SR = 44100


# =========================================================================== utilidades
def keys(*vals):
    xs = np.linspace(0, 1, len(vals))
    return lambda t: np.interp(t, xs, vals)


def smooth_noise(n, rate_hz, rng):
    k = max(3, int(n / SR * rate_hz) + 2)
    pts = np.array([rng.gauss(0, 1) for _ in range(k)])
    return np.interp(np.linspace(0, k - 1, n), np.arange(k), pts)


def bandpass(fc, q):
    lo = max(20.0, fc * (1 - 0.5 / q))
    hi = min(SR * 0.45, fc * (1 + 0.5 / q))
    return butter(2, [lo, hi], btype="band", fs=SR, output="sos")


def bank(sig, formants):
    out = np.zeros_like(sig)
    for fc, q, gain in formants:
        out += gain * sosfilt(bandpass(fc, q), sig)
    return out


def glottal(f, rng, jitter, sub):
    n = len(f)
    ff = f * (1 + smooth_noise(n, 45, rng) * jitter)
    ph = np.cumsum(ff) / SR
    saw = 2 * (ph % 1.0) - 1
    s = saw - 0.45 * np.abs(saw)            # asimetria -> armónicos pares, timbre más orgánico
    if sub:
        s = s + sub * (2 * ((ph * 0.5) % 1.0) - 1)
    return s


def norm(y, peak=0.85):
    return y / (np.max(np.abs(y)) + 1e-9) * peak


def reverb(y, rng, rt=0.6, wet=0.2):
    m = int(SR * min(rt, 2.5))
    t = np.arange(m) / SR
    ir = np.array([rng.gauss(0, 1) for _ in range(m)]) * np.exp(-t * 6.9 / rt)
    ir = sosfilt(butter(2, 3500, btype="low", fs=SR, output="sos"), ir)
    tail = fftconvolve(y, ir)
    out = np.zeros(len(tail))
    out[:len(y)] += y
    out += wet * tail / (np.max(np.abs(tail)) + 1e-9) * np.max(np.abs(y))
    return out


def voice(dur, f0, vowels, rng, vmix=None, jitter=0.012, sub=0.0, breath=0.05, rasp=0.0, trem=(0, 0),
          env=None, attack=0.02, release=0.1, drive=1.0, rt=0.0, wet=0.0, pitch=1.0, detune=0.0):
    n = int(dur * SR)
    t = np.linspace(0, 1, n)
    ts = np.linspace(0, dur, n)
    f = f0(t) * pitch
    src = glottal(f, rng, jitter, sub)
    if detune:                               # segunda voz levemente desafinada: da cuerpo y aspereza
        src = 0.6 * src + 0.5 * glottal(f * (1 + detune), rng, jitter * 1.4, sub)
    ys = [bank(src, v) for v in vowels]
    if len(ys) > 1:
        w = vmix(t) if vmix else t
        y = (1 - w) * ys[0] + w * ys[1]
    else:
        y = ys[0]
    if breath:
        nz = np.array([rng.gauss(0, 1) for _ in range(n)])
        y = y + breath * bank(nz, vowels[0]) * (np.max(np.abs(y)) / (np.max(np.abs(bank(nz, vowels[0]))) + 1e-9))
    if trem[0] > 0:
        y = y * (1 - trem[1] + trem[1] * (0.5 + 0.5 * np.sin(2 * np.pi * trem[0] * ts)))
    if rasp:
        y = y * (1 + rasp * smooth_noise(n, 110, rng))
    e = np.ones(n)
    a, r = max(1, int(attack * SR)), max(1, int(release * SR))
    e[:a] = np.linspace(0, 1, a)
    e[-r:] = np.minimum(e[-r:], np.linspace(1, 0, r))
    if env is not None:
        e = e * env(t)
    y = y * e
    y = sosfilt(butter(2, 25, btype="high", fs=SR, output="sos"), y)
    y = np.tanh(drive * y / (np.max(np.abs(y)) + 1e-9))
    if rt > 0:
        y = reverb(y, rng, rt, wet)
    return norm(y)


def hiss(dur, lo, hi, rng, attack=0.08, release=0.3, peak=0.7):
    n = int(dur * SR)
    nz = np.array([rng.gauss(0, 1) for _ in range(n)])
    y = sosfilt(butter(4, [lo, hi], btype="band", fs=SR, output="sos"), nz)
    e = np.ones(n)
    a, r = int(attack * SR), int(release * SR)
    e[:a] = np.linspace(0, 1, a)
    e[-r:] = np.linspace(1, 0, r)
    t = np.linspace(0, 1, n)
    e *= 0.75 + 0.25 * np.sin(np.pi * t)
    return norm(y * e, peak)


def mix(*sigs, peak=0.85):
    n = max(len(s) for s in sigs)
    out = np.zeros(n)
    for s in sigs:
        out[:len(s)] += s
    return norm(out, peak)


def seq(*parts, gap=0.05):
    z = np.zeros(int(gap * SR))
    out = []
    for p in parts:
        out += [p, z]
    return np.concatenate(out[:-1])


def click_burst(dur, lo, hi, rng, decay=40, peak=0.8):
    n = int(dur * SR)
    t = np.arange(n) / SR
    nz = np.array([rng.gauss(0, 1) for _ in range(n)])
    y = sosfilt(butter(3, [lo, hi], btype="band", fs=SR, output="sos"), nz) * np.exp(-t * decay)
    return norm(y, peak)


def thump(rng, f0, f1, dur=0.3, decay=12, peak=0.9, grit=0.5):
    n = int(SR * dur)
    t = np.arange(n) / SR
    body = np.sin(2 * np.pi * np.cumsum(np.linspace(f0, f1, n)) / SR) * np.exp(-t * decay)
    nz = np.array([rng.gauss(0, 1) for _ in range(n)])
    body += grit * sosfilt(butter(2, 400, btype="low", fs=SR, output="sos"), nz) * np.exp(-t * decay * 2.5)
    return norm(body, peak)


def write_ogg(folder, name, sig, peak=0.85):
    os.makedirs(folder, exist_ok=True)
    sig = norm(sig, peak)
    with tempfile.TemporaryDirectory() as tmp:
        wav = os.path.join(tmp, "x.wav")
        wavfile.write(wav, SR, (np.clip(sig, -1, 1) * 32767).astype(np.int16))
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-ac", "1", "-c:a", "libvorbis",
                        "-q:a", "5", os.path.join(folder, name + ".ogg")], check=True)


# =========================================================================== formantes
V = {
    "open": [(680, 7, 1.0), (1150, 8, 0.7), (2550, 9, 0.25)],
    "nasal": [(480, 7, 1.0), (1450, 9, 0.9), (2400, 9, 0.3)],
    "screech": [(1300, 9, 1.0), (2600, 10, 0.7), (3900, 10, 0.4)],
    "chirp": [(900, 8, 1.0), (2200, 9, 0.6), (3300, 9, 0.3)],
    "low": [(250, 5, 1.0), (560, 6, 0.8), (1050, 7, 0.45)],
    "roar": [(340, 5, 1.0), (850, 6, 0.9), (1700, 7, 0.5)],
    "growl": [(420, 5, 1.0), (900, 6, 0.6), (1500, 7, 0.3)],
    "croak": [(600, 7, 1.0), (1800, 9, 0.8), (2800, 9, 0.4)],
    "peep": [(2800, 8, 1.0), (4000, 8, 0.6)],
    "peep_low": [(1700, 8, 1.0), (3000, 8, 0.5)],
}


def rg(seed):
    return random.Random(seed)


# alfa: voz mas grave, mas larga, con mas cuerpo y reverberacion
def alpha_kw(extra=None):
    kw = dict(pitch=0.72, drive=1.8, rt=1.0, wet=0.28, sub=0.35, detune=0.012)
    if extra:
        kw.update(extra)
    return kw


# =========================================================================== DODO
def dodo():
    d = os.path.join(g.ASSETS, "sounds", "dodo")
    ambient = [(1, [190, 265, 230, 165], 0.62, 0.25), (2, [170, 240, 280, 200], 0.8, 0.3), (3, [215, 290, 240], 0.5, 0.2)]
    for i, f0k, dur, sub in ambient:
        r = rg(100 + i)
        honk = lambda t: 0.45 + 0.55 * np.abs(np.sin(np.pi * t * (1.6 + 0.4 * i)))
        write_ogg(d, f"ambient{i}", voice(dur, keys(*f0k), [V["open"], V["nasal"]], r, jitter=0.02, sub=sub, breath=0.05,
                                           env=honk, rasp=0.15, drive=1.3, rt=0.25, wet=0.1))
    for i, f0k, dur in ((1, [190, 255, 215, 150], 1.0), (2, [170, 230, 260, 140], 1.3)):
        r = rg(110 + i)
        write_ogg(d, f"ambient_alpha{i}", voice(dur, keys(*f0k), [V["open"], V["nasal"]], r, jitter=0.02,
                                                 env=lambda t: 0.5 + 0.5 * np.abs(np.sin(np.pi * t * 2.2)),
                                                 **alpha_kw(dict(release=0.3))))
    for i, f0k in ((1, [520, 720, 430]), (2, [480, 650, 380])):
        r = rg(120 + i)
        write_ogg(d, f"hurt{i}", voice(0.3, keys(*f0k), [V["nasal"]], r, jitter=0.03, rasp=0.25, drive=1.8, release=0.08))
    write_ogg(d, "hurt_alpha1", voice(0.5, keys(380, 520, 300), [V["nasal"]], rg(125), jitter=0.03, rasp=0.3,
                                      **alpha_kw(dict(release=0.15))))
    write_ogg(d, "death1", voice(1.0, keys(420, 320, 210, 120), [V["open"]], rg(130), jitter=0.03, rasp=0.3,
                                 trem=(7, 0.3), release=0.35, drive=1.5, rt=0.3, wet=0.12))
    write_ogg(d, "death_alpha1", voice(1.6, keys(300, 230, 150, 85), [V["open"]], rg(131), jitter=0.03, rasp=0.35,
                                       trem=(6, 0.3), **alpha_kw(dict(release=0.6))))
    for i, base in ((1, 330), (2, 370)):
        r = rg(140 + i)
        h = [voice(0.17, keys(base, base * 1.28, base * 0.9), [V["nasal"]], r, jitter=0.03, rasp=0.2, drive=1.8,
                   attack=0.01, release=0.05) for _ in range(3)]
        write_ogg(d, f"alert{i}", seq(*h, gap=0.07))
    write_ogg(d, "alert_alpha1", seq(*[voice(0.28, keys(240, 310, 210), [V["nasal"]], rg(150 + k), jitter=0.03, rasp=0.25,
                                             **alpha_kw(dict(attack=0.01, release=0.1))) for k in range(3)], gap=0.1))
    for i, base in ((1, 1800), (2, 2100)):
        r = rg(160 + i)
        p = [voice(0.09, keys(base, base * 1.25, base * 1.05), [V["peep"]], r, jitter=0.02, breath=0.02, attack=0.005,
                   release=0.03) for _ in range(3)]
        write_ogg(d, f"chick{i}", seq(p[0], p[1], p[2], gap=0.06))
    for i in (1, 2):                         # pisadas: patitas ligeras
        r = rg(170 + i)
        write_ogg(d, f"step{i}", mix(click_burst(0.07, 900, 2600, r, 60), thump(r, 160, 90, 0.08, 40, 0.5), peak=0.6),
                  peak=0.6)


# =========================================================================== VELOCIRAPTOR
def raptor():
    d = os.path.join(g.ASSETS, "sounds", "velociraptor")
    for i, base in ((1, 820), (2, 940), (3, 720)):                   # gorjeo entrecortado
        r = rg(200 + i)
        chirps = [voice(0.08 + 0.02 * k, keys(base * (0.9 + 0.1 * k), base * (1.5 - 0.1 * k)), [V["chirp"]], r,
                        jitter=0.03, breath=0.04, attack=0.008, release=0.03, rasp=0.1) for k in range(3 + i % 2)]
        write_ogg(d, f"ambient{i}", seq(*chirps, gap=0.04))
    for i, base in ((1, 640), (2, 720)):
        r = rg(210 + i)
        chirps = [voice(0.13, keys(base * 0.9, base * 1.6, base * 1.1), [V["chirp"]], r, jitter=0.035, breath=0.05,
                        drive=1.5, **{"pitch": 0.72, "sub": 0.3, "rt": 0.7, "wet": 0.25}) for _ in range(3)]
        write_ogg(d, f"ambient_alpha{i}", seq(*chirps, gap=0.06))
    for i, dur in ((1, 0.95), (2, 1.15)):
        r = rg(220 + i)
        h = hiss(dur, 2800, 8500, r, 0.08, 0.4)
        gr = voice(dur, keys(115, 90), [V["growl"]], r, sub=0.5, rasp=0.5, trem=(24, 0.5), breath=0.1, drive=1.6)
        write_ogg(d, f"warn{i}", mix(h, gr * 0.75))
    h = hiss(1.4, 2200, 7500, rg(225), 0.1, 0.6)
    gr = voice(1.4, keys(82, 62), [V["growl"]], rg(226), sub=0.6, rasp=0.5, trem=(20, 0.5), breath=0.12, drive=2.0,
               rt=0.9, wet=0.3)
    write_ogg(d, "warn_alpha1", mix(h, gr * 0.9))
    for i, base in ((1, 650), (2, 720)):
        write_ogg(d, f"attack{i}", voice(0.5, keys(base, base * 2.7, base * 1.9), [V["screech"]], rg(230 + i),
                                         jitter=0.04, rasp=0.3, breath=0.12, drive=1.9, release=0.15))
    write_ogg(d, "attack_alpha1", voice(0.8, keys(520, 1400, 900), [V["screech"]], rg(235), jitter=0.04, rasp=0.35,
                                        breath=0.12, **alpha_kw(dict(release=0.3))))
    for i, base in ((1, 1250), (2, 1400)):
        write_ogg(d, f"hurt{i}", voice(0.3, keys(base, base * 0.5), [V["chirp"]], rg(240 + i), jitter=0.05, rasp=0.25,
                                       drive=1.7, release=0.08))
    write_ogg(d, "hurt_alpha1", voice(0.5, keys(900, 450), [V["chirp"]], rg(245), jitter=0.05, rasp=0.3,
                                      **alpha_kw(dict(release=0.2))))
    write_ogg(d, "death1", voice(1.0, keys(1050, 620, 260), [V["screech"], V["chirp"]], rg(250), jitter=0.05,
                                 trem=(6, 0.3), rasp=0.3, release=0.35, rt=0.4, wet=0.15))
    write_ogg(d, "death_alpha1", voice(1.7, keys(800, 480, 180), [V["screech"], V["chirp"]], rg(251), jitter=0.05,
                                       trem=(5, 0.3), rasp=0.35, **alpha_kw(dict(release=0.7))))
    for i, base in ((1, 520), (2, 600)):                              # llamada a la manada
        r = rg(260 + i)
        c = [voice(0.17, keys(base * (1 + 0.1 * k), base * (2 + 0.1 * k)), [V["chirp"]], r, jitter=0.03, breath=0.03,
                   attack=0.01, release=0.05) for k in range(3)]
        write_ogg(d, f"call{i}", seq(*c, gap=0.07))
    for i, base in ((1, 1900), (2, 2200)):
        r = rg(270 + i)
        p = [voice(0.08, keys(base, base * 1.2), [V["peep"]], r, jitter=0.02, attack=0.005, release=0.03) for _ in range(3)]
        write_ogg(d, f"chick{i}", seq(*p, gap=0.06))
    for i in (1, 2):                                                  # garras que repiquetean
        r = rg(280 + i)
        write_ogg(d, f"step{i}", mix(click_burst(0.05, 2200, 6500, r, 90), thump(r, 140, 80, 0.07, 45, 0.5), peak=0.6),
                  peak=0.6)


# =========================================================================== TYRANNOSAURUS
def rex():
    d = os.path.join(g.ASSETS, "sounds", "tyrannosaurus")
    for i, (base, dur) in enumerate(((58, 1.5), (68, 1.3), (52, 1.7)), start=1):
        r = rg(300 + i)
        infra = np.sin(2 * np.pi * 38 * np.linspace(0, dur, int(dur * SR))) * 0.25
        y = voice(dur, keys(base, base * 1.3, base * 0.9), [V["low"]], r, jitter=0.03, sub=0.5, rasp=0.4, breath=0.1,
                  trem=(6, 0.3), attack=0.2, release=0.45, drive=1.5, rt=1.1, wet=0.28, detune=0.01,
                  env=lambda t: 0.6 + 0.4 * np.sin(np.pi * t))
        write_ogg(d, f"ambient{i}", mix(y, np.concatenate([infra, np.zeros(len(y) - len(infra))])[:len(y)] if len(y) >= len(infra) else y))
    for i, dur in ((1, 1.9), (2, 2.4)):
        write_ogg(d, f"ambient_alpha{i}", voice(dur, keys(44, 58, 40), [V["low"]], rg(310 + i), jitter=0.03, sub=0.6, rasp=0.45,
                                                 breath=0.12, trem=(5, 0.35), attack=0.3, release=0.7, drive=1.8, rt=1.8,
                                                 wet=0.4, detune=0.012, env=lambda t: 0.6 + 0.4 * np.sin(np.pi * t)))
    for i, (dur, peak) in ((1, (1.9, 190)), (2, (2.2, 170))):          # rugido de advertencia
        write_ogg(d, f"warn{i}", voice(dur, keys(75, peak, peak * 0.85, 90), [V["low"], V["roar"]], rg(320 + i),
                                       vmix=keys(0, 0.6, 1.0, 0.7), jitter=0.035, sub=0.4, rasp=0.5, breath=0.2,
                                       attack=0.08, release=0.6, drive=2.1, rt=1.5, wet=0.35, detune=0.012,
                                       env=lambda t: 0.45 + 0.55 * np.sin(np.pi * np.clip(t * 1.15, 0, 1))))
    write_ogg(d, "warn_alpha1", voice(2.8, keys(52, 130, 110, 60), [V["low"], V["roar"]], rg(325), vmix=keys(0, 0.6, 1.0, 0.7),
                                      jitter=0.035, sub=0.6, rasp=0.55, breath=0.22, attack=0.1, release=0.9, drive=2.4,
                                      rt=2.2, wet=0.5, detune=0.014, env=lambda t: 0.45 + 0.55 * np.sin(np.pi * np.clip(t * 1.1, 0, 1))))
    for i, dur in ((1, 0.95), (2, 1.05)):
        write_ogg(d, f"attack{i}", voice(dur, keys(125, 270, 180), [V["low"], V["roar"]], rg(330 + i), vmix=keys(0.3, 1, 0.8),
                                         jitter=0.04, sub=0.3, rasp=0.5, breath=0.2, attack=0.03, release=0.3, drive=2.3,
                                         rt=1.0, wet=0.25))
    write_ogg(d, "attack_alpha1", voice(1.5, keys(85, 190, 120), [V["low"], V["roar"]], rg(335), vmix=keys(0.3, 1, 0.8),
                                        jitter=0.04, sub=0.5, rasp=0.55, breath=0.22, attack=0.04, release=0.6, drive=2.6,
                                        rt=1.8, wet=0.4, detune=0.012))
    for i, base in ((1, 200), (2, 230)):
        write_ogg(d, f"hurt{i}", voice(0.65, keys(base, base * 0.55), [V["roar"]], rg(340 + i), jitter=0.04, sub=0.3, rasp=0.4,
                                       breath=0.15, drive=2.0, release=0.2, rt=0.8, wet=0.2))
    write_ogg(d, "hurt_alpha1", voice(1.0, keys(140, 70), [V["roar"]], rg(345), jitter=0.04, rasp=0.45, breath=0.18,
                                      **alpha_kw(dict(release=0.4, rt=1.5, wet=0.35))))
    write_ogg(d, "death1", voice(2.4, keys(170, 120, 75, 45), [V["roar"], V["low"]], rg(350), jitter=0.04, sub=0.4, rasp=0.45,
                                 breath=0.15, trem=(5, 0.3), release=0.9, drive=1.9, rt=1.6, wet=0.32))
    write_ogg(d, "death_alpha1", voice(3.4, keys(120, 85, 52, 34), [V["roar"], V["low"]], rg(351), jitter=0.04, rasp=0.5,
                                       breath=0.18, trem=(4, 0.35), **alpha_kw(dict(release=1.4, rt=2.2, wet=0.45))))
    for i, (f0, f1, decay) in ((1, (70, 32, 11)), (2, (62, 28, 10))):   # pisadas pesadas
        r = rg(360 + i)
        write_ogg(d, f"step{i}", mix(thump(r, f0, f1, 0.45, decay, 0.95, 0.8), click_burst(0.12, 150, 700, r, 25, 0.5)),
                  peak=0.95)
    for i, base in ((1, 1100), (2, 1350)):
        r = rg(370 + i)
        p = [voice(0.12, keys(base, base * 1.3), [V["peep_low"]], r, jitter=0.03, attack=0.006, release=0.05) for _ in range(2)]
        write_ogg(d, f"chick{i}", seq(*p, gap=0.08))


# =========================================================================== DILOPHOSAURUS
def dilo():
    d = os.path.join(g.ASSETS, "sounds", "dilophosaurus")
    for i, (base, dur) in enumerate(((260, 0.5), (230, 0.62), (290, 0.45)), start=1):   # croar con cascabeleo
        r = rg(400 + i)
        write_ogg(d, f"ambient{i}", voice(dur, keys(base, base * 0.7, base * 1.25), [V["croak"]], r, jitter=0.04, breath=0.1,
                                           trem=(38, 0.55), rasp=0.25, drive=1.5, rt=0.3, wet=0.12))
    for i, dur in ((1, 0.85), (2, 1.0)):
        write_ogg(d, f"ambient_alpha{i}", voice(dur, keys(190, 130, 220), [V["croak"]], rg(410 + i), jitter=0.04, breath=0.12,
                                                 trem=(30, 0.6), rasp=0.3, **alpha_kw(dict(release=0.3))))
    for i, dur in ((1, 1.0), (2, 1.2)):                              # siseo de cobra + cascabel
        r = rg(420 + i)
        h = hiss(dur, 2000, 9000, r, 0.06, 0.4)
        rat = voice(dur, keys(210, 160), [V["croak"]], r, trem=(32, 0.7), jitter=0.05, breath=0.15, drive=1.6)
        write_ogg(d, f"warn{i}", mix(h, rat * 0.6))
    h = hiss(1.5, 1800, 8000, rg(425), 0.08, 0.6)
    rat = voice(1.5, keys(150, 110), [V["croak"]], rg(426), trem=(26, 0.7), jitter=0.05, breath=0.18, drive=2.0, rt=1.0,
                wet=0.3, sub=0.4)
    write_ogg(d, "warn_alpha1", mix(h, rat * 0.8))
    for i, base in ((1, 520), (2, 600)):
        r = rg(430 + i)
        write_ogg(d, f"attack{i}", mix(voice(0.45, keys(base, base * 2.8, base * 1.9), [V["screech"]], r, jitter=0.05, rasp=0.3,
                                             breath=0.15, drive=1.9), hiss(0.45, 3000, 9000, r, 0.02, 0.2, 0.5) * 0.6))
    write_ogg(d, "attack_alpha1", voice(0.75, keys(400, 1200, 800), [V["screech"]], rg(435), jitter=0.05, rasp=0.35,
                                        breath=0.15, **alpha_kw(dict(release=0.3))))
    for i, base in ((1, 950), (2, 1100)):
        write_ogg(d, f"hurt{i}", voice(0.3, keys(base, base * 0.5), [V["croak"]], rg(440 + i), jitter=0.05, rasp=0.3,
                                       trem=(30, 0.4), drive=1.7, release=0.08))
    write_ogg(d, "hurt_alpha1", voice(0.5, keys(700, 350), [V["croak"]], rg(445), jitter=0.05, rasp=0.3, trem=(26, 0.4),
                                      **alpha_kw(dict(release=0.2))))
    write_ogg(d, "death1", voice(1.0, keys(720, 330, 130), [V["croak"], V["screech"]], rg(450), jitter=0.05, trem=(30, 0.5),
                                 rasp=0.3, release=0.4, rt=0.4, wet=0.15))
    write_ogg(d, "death_alpha1", voice(1.7, keys(540, 250, 95), [V["croak"], V["screech"]], rg(451), jitter=0.05, trem=(24, 0.5),
                                       rasp=0.35, **alpha_kw(dict(release=0.8))))
    for i in (1, 2):
        r = rg(460 + i)
        write_ogg(d, f"step{i}", mix(click_burst(0.06, 1500, 5000, r, 70), thump(r, 120, 70, 0.08, 40, 0.5), peak=0.6), peak=0.6)
    for i, base in ((1, 1700), (2, 2000)):
        r = rg(470 + i)
        p = [voice(0.09, keys(base, base * 1.2), [V["peep"]], r, jitter=0.03, attack=0.005, release=0.03) for _ in range(3)]
        write_ogg(d, f"chick{i}", seq(*p, gap=0.06))
    for i in (1, 2):                                                  # escupitajo: chasquido humedo + salpicadura
        r = rg(480 + i)
        splash = hiss(0.28, 900, 4200, r, 0.01, 0.2, 0.8) * np.exp(-np.linspace(0, 5, int(0.28 * SR)))
        gurgle = voice(0.22, keys(190, 120), [V["croak"]], r, trem=(34, 0.7), jitter=0.06, breath=0.2, drive=1.4, release=0.08)
        write_ogg(d, f"spit{i}", mix(splash, np.concatenate([np.zeros(int(0.04 * SR)), gurgle]) * 0.7))
    r = rg(490)                                                       # gorguera: repiqueteo de chasquidos que se acelera
    n = int(0.7 * SR)
    track = np.zeros(n)
    t_pos = 0.0
    gap = 0.07
    while t_pos < 0.62:
        c = click_burst(0.025, 2500, 7000, r, 120, 0.9)
        i0 = int(t_pos * SR)
        track[i0:i0 + len(c)] += c[:max(0, n - i0)]
        t_pos += gap
        gap = max(0.018, gap * 0.9)
    write_ogg(d, "frill1", mix(track, hiss(0.7, 3000, 9000, r, 0.05, 0.3, 0.35)))


def run():
    dodo()
    raptor()
    rex()
    dilo()
