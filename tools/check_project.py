#!/usr/bin/env python3
"""
Comprobaciones estáticas (no sustituyen a compilar, pero detectan casi todos los descuidos de recursos y referencias):
JSON válido, modelos/texturas/sonidos referenciados, ModSounds/ModItems/ModEntities/ModBlocks usados existen,
imports internos, equilibrio de llaves, claves de idioma, estados de bloque, botín, entradas del libro.
"""
import glob
import itertools
import json
import os
import re
import sys

ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
RES = os.path.join(ROOT, "src/main/resources")
A = os.path.join(RES, "assets/prehistoricsurvival")
D = os.path.join(RES, "data/prehistoricsurvival")
J = os.path.join(ROOT, "src/main/java")
errors = []


def err(msg):
    errors.append(msg)


def load(path):
    try:
        with open(path, encoding="utf-8") as fh:
            return json.load(fh)
    except Exception as e:
        err(f"JSON invalido {os.path.relpath(path, ROOT)}: {e}")
        return None


# ---- 1) JSON valido
for f in glob.glob(RES + "/**/*.json", recursive=True) + [RES + "/pack.mcmeta"]:
    load(f)


def tex_ok(ref):
    ns, path = ref.split(":") if ":" in ref else ("minecraft", ref)
    return ns != "prehistoricsurvival" or os.path.exists(f"{A}/textures/{path}.png")


def model_ok(ref):
    ns, path = ref.split(":")
    return ns != "prehistoricsurvival" or os.path.exists(f"{A}/models/{path}.json")


# ---- 2) modelos y estados
for f in glob.glob(A + "/models/**/*.json", recursive=True):
    d = load(f)
    if not d:
        continue
    for k, v in d.get("textures", {}).items():
        if not v.startswith("#") and not tex_ok(v):
            err(f"{os.path.relpath(f, ROOT)}: falta textura {v}")
    if "parent" in d and not model_ok(d["parent"]):
        err(f"{os.path.relpath(f, ROOT)}: falta modelo padre {d['parent']}")
for f in glob.glob(A + "/blockstates/*.json"):
    d = load(f)
    for k, v in (d or {}).get("variants", {}).items():
        if not model_ok(v["model"]):
            err(f"{os.path.basename(f)}: falta modelo {v['model']}")
egg = load(A + "/blockstates/dino_egg.json")
if egg:
    for h, k, n in itertools.product(range(3), range(4), ("true", "false")):
        if f"hatch={h},kind={k},nested={n}" not in egg["variants"]:
            err(f"dino_egg: falta variante hatch={h},kind={k},nested={n}")

# ---- 3) sonidos
snd = load(A + "/sounds.json") or {}
for ev, d in snd.items():
    for s in d["sounds"]:
        if not os.path.exists(f"{A}/sounds/{s.split(':')[1]}.ogg"):
            err(f"falta ogg {s}")
java = {f: open(f, encoding="utf-8").read() for f in glob.glob(J + "/**/*.java", recursive=True)}
alljava = "\n".join(java.values())
modsounds = open(J + "/com/prehistoricsurvival/registry/ModSounds.java", encoding="utf-8").read()
defined_sounds = set(re.findall(r"RegistryObject<SoundEvent> (\w+) =", modsounds))
for name in set(re.findall(r"ModSounds\.(\w+)", alljava)):
    if name not in defined_sounds and name != "SOUNDS":
        err(f"ModSounds.{name} usado pero no existe")
for ev in re.findall(r'register\("([^"]+)"\)', modsounds):
    if ev not in snd:
        err(f"evento {ev} sin entrada en sounds.json")

# ---- 4) registros usados existen
for reg, pattern in (("ModItems", r"RegistryObject<Item> (\w+) ="), ("ModEntities", r"RegistryObject<EntityType<\w+>> (\w+) ="),
                     ("ModBlocks", r"RegistryObject<Block> (\w+) =")):
    src = open(J + f"/com/prehistoricsurvival/registry/{reg}.java", encoding="utf-8").read()
    defined = set(re.findall(pattern, src))
    for name in set(re.findall(reg + r"\.([A-Z_]+)\b", alljava)):
        if name not in defined and name not in ("SOUNDS", "ITEMS", "ENTITIES", "BLOCKS", "BLOCK_ENTITIES", "TABS"):
            err(f"{reg}.{name} usado pero no existe")

# ---- 5) items / entidades / bloques: modelo, idioma, botín
lang = {c: load(f"{A}/lang/{f}.json") or {} for c, f in (("es", "es_es"), ("en", "en_us"))}
items = open(J + "/com/prehistoricsurvival/registry/ModItems.java", encoding="utf-8").read()
for name in re.findall(r'ITEMS\.register\("([^"]+)"', items):
    if not os.path.exists(f"{A}/models/item/{name}.json"):
        err(f"item {name} sin modelo")
    for c, L in lang.items():
        if f"item.prehistoricsurvival.{name}" not in L:
            err(f"item {name} sin nombre ({c})")
for name in re.findall(r'BLOCKS\.register\("([^"]+)"', open(J + "/com/prehistoricsurvival/registry/ModBlocks.java").read()):
    if not os.path.exists(f"{A}/blockstates/{name}.json"):
        err(f"bloque {name} sin blockstate")
    for c, L in lang.items():
        if f"block.prehistoricsurvival.{name}" not in L:
            err(f"bloque {name} sin nombre ({c})")
ents = open(J + "/com/prehistoricsurvival/registry/ModEntities.java", encoding="utf-8").read()
for name in re.findall(r'ENTITIES\.register\("([^"]+)"', ents):
    for c, L in lang.items():
        if f"entity.prehistoricsurvival.{name}" not in L:
            err(f"entidad {name} sin nombre ({c})")
    if name != "venom_spit":
        for sub in ("loot_tables/entities/%s.json", "forge/biome_modifier/%s_spawns.json", "tags/worldgen/biome/%s_habitat.json"):
            if not os.path.exists(f"{D}/{sub % name}"):
                err(f"entidad {name}: falta {sub % name}")
for k in set(re.findall(r'"(prehistoricsurvival\.[a-z_.0-9]+)"', alljava)):
    for c, L in lang.items():
        if k not in L:
            err(f"clave de idioma sin traducir ({c}): {k}")

# ---- 6) texturas de entidad
for species, variants in (("dodo", ["brown", "grey", "sand"]), ("velociraptor", ["olive", "ember", "slate"]),
                          ("tyrannosaurus", ["forest", "dusk", "ash"]), ("dilophosaurus", ["viridian", "amber", "teal"])):
    names = [f"{species}_alpha", f"{species}_alpha_glow"] + [f"{species}_{v}_{g}" for v in variants for g in ("male", "female")]
    for n in names:
        if not os.path.exists(f"{A}/textures/entity/{species}/{n}.png"):
            err(f"falta textura entity/{species}/{n}.png")

# ---- 7) libro-guia
sections = (load(A + "/guide/sections.json") or {}).get("sections", [])
sec_ids = {s["id"] for s in sections}
entry_ids = set()
for f in glob.glob(A + "/guide/entries/*.json"):
    d = load(f)
    if not d:
        continue
    if d["id"] in entry_ids:
        err(f"entrada duplicada {d['id']}")
    entry_ids.add(d["id"])
    if d["section"] not in sec_ids:
        err(f"entrada {d['id']}: seccion inexistente {d['section']}")
    if "preview" in d and d["preview"]["entity"].split(":")[1] not in ents:
        err(f"entrada {d['id']}: entidad de vista previa inexistente")
    for k in ("title", "text"):
        if not d.get(k):
            err(f"entrada {d['id']}: falta {k}")
for tex in ("guide_cover", "guide_pages"):
    if not os.path.exists(f"{A}/textures/gui/{tex}.png"):
        err(f"falta textura gui/{tex}.png")

# ---- 8) Java: imports, llaves, usos sin importar
classes = {}
for f, s in java.items():
    pkg = re.search(r"package ([\w.]+);", s).group(1)
    classes[pkg + "." + os.path.basename(f)[:-5]] = f
short = {k.split(".")[-1]: k for k in classes}
for f, s in java.items():
    name = os.path.basename(f)
    pkg = re.search(r"package ([\w.]+);", s).group(1)
    for imp in re.findall(r"import (com\.prehistoricsurvival[\w.]+);", s):
        if imp not in classes:
            err(f"{name}: import inexistente {imp}")
    t = re.sub(r'"(\\.|[^"\\])*"', '""', s)
    t = re.sub(r"//.*", "", t)
    t = re.sub(r"/\*.*?\*/", "", t, flags=re.S)
    for a, b in ("{}", "()", "[]"):
        if t.count(a) != t.count(b):
            err(f"{name}: {a}{b} desequilibrados")
    imported = set(re.findall(r"import [\w.]*\.(\w+);", s))
    body = re.sub(r"^import .*$", "", t, flags=re.M)
    for cls in set(re.findall(r"\b([A-Z]\w+)\b", body)):
        if cls in short and cls != name[:-5] and short[cls].rsplit(".", 1)[0] != pkg and cls not in imported:
            if "com.prehistoricsurvival." + ".".join(short[cls].split(".")[2:]) not in body:
                err(f"{name}: usa {cls} sin importarlo")
    # imports no usados de clases del mod (aviso)
    for imp in re.findall(r"import (com\.prehistoricsurvival[\w.]+);", s):
        if not re.search(r"\b" + imp.split(".")[-1] + r"\b", body):
            print(f"  aviso: {name}: import sin usar {imp.split('.')[-1]}")

print("ERRORES:" if errors else "Sin errores en las comprobaciones estaticas")
for e in errors:
    print(" -", e)
print(f"java: {len(java)} | json: {len(glob.glob(RES + '/**/*.json', recursive=True))} | "
      f"png: {len(glob.glob(RES + '/**/*.png', recursive=True))} | ogg: {len(glob.glob(RES + '/**/*.ogg', recursive=True))}")
sys.exit(1 if errors else 0)
