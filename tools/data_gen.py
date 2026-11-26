#!/usr/bin/env python3
"""
Datos del mod a partir de definiciones únicas:
 - sounds.json y ModSounds.java se derivan de los .ogg que EXISTEN (no se pueden desincronizar)
 - idiomas es/en, tablas de botín, aparición por biomas, etiquetas y recetas.
"""
import glob
import json
import os
import re

import generate_assets as g

A = g.ASSETS
R = os.path.normpath(os.path.join(A, "..", ".."))                      # src/main/resources
D = os.path.join(R, "data", "prehistoricsurvival")
JAVA_REG = os.path.normpath(os.path.join(g.HERE, "..", "src", "main", "java", "com", "prehistoricsurvival", "registry"))

SPECIES_PREFIX = {"dodo": "DODO", "velociraptor": "RAPTOR", "tyrannosaurus": "REX", "dilophosaurus": "DILO"}
NAMES = {
    "es": {"dodo": "El dodo", "velociraptor": "El velociraptor", "tyrannosaurus": "El T. rex",
           "dilophosaurus": "El dilophosaurus"},
    "en": {"dodo": "The dodo", "velociraptor": "The velociraptor", "tyrannosaurus": "The T. rex",
           "dilophosaurus": "The dilophosaurus"},
}
VERBS = {
    "es": {"ambient": "{n} vocaliza", "warn": "{n} advierte", "attack": "{n} ataca", "hurt": "{n} chilla de dolor",
           "death": "{n} muere", "call": "{n} llama a la manada", "chick": "Una cría suena", "alert": "{n} da la alarma",
           "step": "{n} pisa", "spit": "{n} escupe veneno", "frill": "{n} sacude la gorguera"},
    "en": {"ambient": "{n} calls", "warn": "{n} warns", "attack": "{n} attacks", "hurt": "{n} cries in pain",
           "death": "{n} dies", "call": "{n} calls its pack", "chick": "A hatchling peeps", "alert": "{n} raises the alarm",
           "step": "{n} steps", "spit": "{n} spits venom", "frill": "{n} rattles its frill"},
}


def wj(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(obj, fh, indent=2, ensure_ascii=False)


# =========================================================================== sonidos
def sounds():
    events = {}
    out = {}
    for folder in sorted(glob.glob(os.path.join(A, "sounds", "*", ""))):
        species = os.path.basename(os.path.normpath(folder))
        groups = {}
        for fn in sorted(os.listdir(folder)):
            if fn.endswith(".ogg"):
                stem = fn[:-4]
                groups.setdefault(re.sub(r"\d+$", "", stem), []).append(f"prehistoricsurvival:{species}/{stem}")
        for group, files in groups.items():
            ev = f"entity.{species}.{group}"
            out[ev] = {"subtitle": f"subtitles.prehistoricsurvival.{ev}", "sounds": files}
            events[ev] = (species, group)
    wj(os.path.join(A, "sounds.json"), out)

    lines = ["// ARCHIVO GENERADO por tools/data_gen.py a partir de los .ogg existentes - NO EDITAR A MANO.",
             "package com.prehistoricsurvival.registry;", "",
             "import com.prehistoricsurvival.PrehistoricSurvival;",
             "import net.minecraft.resources.ResourceLocation;",
             "import net.minecraft.sounds.SoundEvent;",
             "import net.minecraftforge.registries.DeferredRegister;",
             "import net.minecraftforge.registries.ForgeRegistries;",
             "import net.minecraftforge.registries.RegistryObject;", "",
             "public final class ModSounds {",
             "    public static final DeferredRegister<SoundEvent> SOUNDS =",
             "            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PrehistoricSurvival.MOD_ID);", ""]
    for ev, (species, group) in events.items():
        const = f"{SPECIES_PREFIX[species]}_{group.upper()}"
        lines.append(f'    public static final RegistryObject<SoundEvent> {const} = register("{ev}");')
    lines += ["", "    private static RegistryObject<SoundEvent> register(String name) {",
              "        return SOUNDS.register(name, () ->",
              "                SoundEvent.createVariableRangeEvent(new ResourceLocation(PrehistoricSurvival.MOD_ID, name)));",
              "    }", "", "    private ModSounds() {}", "}"]
    with open(os.path.join(JAVA_REG, "ModSounds.java"), "w", encoding="utf-8") as fh:
        fh.write("\n".join(lines) + "\n")
    return events


# =========================================================================== idiomas
LANG = {
    "es": {
        "itemGroup.prehistoricsurvival": "Prehistoric Survival",
        "entity.prehistoricsurvival.dodo": "Dodo",
        "entity.prehistoricsurvival.velociraptor": "Velociraptor",
        "entity.prehistoricsurvival.tyrannosaurus": "Tyrannosaurus rex",
        "entity.prehistoricsurvival.dilophosaurus": "Dilophosaurus",
        "entity.prehistoricsurvival.venom_spit": "Escupitajo de veneno",
        "block.prehistoricsurvival.dino_egg": "Huevo de dinosaurio",
        "block.prehistoricsurvival.nest": "Nido de paja",
        "block.prehistoricsurvival.mud_nest": "Nido de barro",
        "block.prehistoricsurvival.mound_nest": "Montículo de nido",
        "item.prehistoricsurvival.nest": "Nido de paja",
        "item.prehistoricsurvival.mud_nest": "Nido de barro",
        "item.prehistoricsurvival.mound_nest": "Montículo de nido",
        "item.prehistoricsurvival.explorer_guide": "Guía del Explorador",
        "item.prehistoricsurvival.raw_dino_meat": "Carne de dinosaurio cruda",
        "item.prehistoricsurvival.cooked_dino_meat": "Carne de dinosaurio asada",
        "item.prehistoricsurvival.sedative_meat": "Carne sedante",
        "item.prehistoricsurvival.dodo_feather": "Pluma de dodo",
        "item.prehistoricsurvival.raptor_claw": "Garra de raptor",
        "item.prehistoricsurvival.rex_tooth": "Diente de T. rex",
        "item.prehistoricsurvival.venom_gland": "Glándula de veneno",
        "item.prehistoricsurvival.alpha_essence": "Esencia alfa",
        "item.prehistoricsurvival.venom_glob": "Glóbulo de veneno",
        "item.prehistoricsurvival.dodo_egg": "Huevo de dodo",
        "item.prehistoricsurvival.velociraptor_egg": "Huevo de velociraptor",
        "item.prehistoricsurvival.dilophosaurus_egg": "Huevo de dilophosaurus",
        "item.prehistoricsurvival.tyrannosaurus_egg": "Huevo de tyrannosaurus",
        "item.prehistoricsurvival.dodo_spawn_egg": "Huevo generador de dodo",
        "item.prehistoricsurvival.velociraptor_spawn_egg": "Huevo generador de velociraptor",
        "item.prehistoricsurvival.dilophosaurus_spawn_egg": "Huevo generador de dilophosaurus",
        "item.prehistoricsurvival.tyrannosaurus_spawn_egg": "Huevo generador de tyrannosaurus",
        "prehistoricsurvival.label.alpha": "Alfa",
        "prehistoricsurvival.label.taming": "Domesticando %s%%",
        "prehistoricsurvival.stage.baby": "Cría",
        "prehistoricsurvival.stage.teen": "Adolescente",
        "prehistoricsurvival.stage.adult": "Adulto",
        "prehistoricsurvival.message.needs_sedation": "Está despierto y furioso: primero hay que sedarlo con carne sedante.",
        "prehistoricsurvival.message.too_wary": "Desconfía de ti. Acércate agachado y sin prisas.",
        "prehistoricsurvival.message.too_soon": "Aún no confía en ti. Vuelve en %s s.",
        "prehistoricsurvival.tooltip.egg": "Nivel %s · Incubación %s%%",
        "prehistoricsurvival.guide.title": "Guía del Explorador",
        "prehistoricsurvival.guide.cover1": "Prehistoric",
        "prehistoricsurvival.guide.cover2": "Survival",
        "prehistoricsurvival.guide.subtitle": "Guía del Explorador Paleontólogo",
        "prehistoricsurvival.guide.open": "Haz clic para abrir",
        "prehistoricsurvival.guide.index": "Índice",
        "prehistoricsurvival.guide.added": "Añadido en v%s",
        "prehistoricsurvival.guide.new": "NUEVO",
        "prehistoricsurvival.guide.page": "%s/%s",
        "prehistoricsurvival.guide.back": "Índice",
        "prehistoricsurvival.guide.received": "Has recibido la Guía del Explorador. Úsala (clic derecho) para abrirla.",
    },
    "en": {
        "itemGroup.prehistoricsurvival": "Prehistoric Survival",
        "entity.prehistoricsurvival.dodo": "Dodo",
        "entity.prehistoricsurvival.velociraptor": "Velociraptor",
        "entity.prehistoricsurvival.tyrannosaurus": "Tyrannosaurus Rex",
        "entity.prehistoricsurvival.dilophosaurus": "Dilophosaurus",
        "entity.prehistoricsurvival.venom_spit": "Venom Spit",
        "block.prehistoricsurvival.dino_egg": "Dinosaur Egg",
        "block.prehistoricsurvival.nest": "Straw Nest",
        "block.prehistoricsurvival.mud_nest": "Mud Nest",
        "block.prehistoricsurvival.mound_nest": "Nest Mound",
        "item.prehistoricsurvival.nest": "Straw Nest",
        "item.prehistoricsurvival.mud_nest": "Mud Nest",
        "item.prehistoricsurvival.mound_nest": "Nest Mound",
        "item.prehistoricsurvival.explorer_guide": "Explorer's Guide",
        "item.prehistoricsurvival.raw_dino_meat": "Raw Dino Meat",
        "item.prehistoricsurvival.cooked_dino_meat": "Cooked Dino Meat",
        "item.prehistoricsurvival.sedative_meat": "Sedative Meat",
        "item.prehistoricsurvival.dodo_feather": "Dodo Feather",
        "item.prehistoricsurvival.raptor_claw": "Raptor Claw",
        "item.prehistoricsurvival.rex_tooth": "Rex Tooth",
        "item.prehistoricsurvival.venom_gland": "Venom Gland",
        "item.prehistoricsurvival.alpha_essence": "Alpha Essence",
        "item.prehistoricsurvival.venom_glob": "Venom Glob",
        "item.prehistoricsurvival.dodo_egg": "Dodo Egg",
        "item.prehistoricsurvival.velociraptor_egg": "Velociraptor Egg",
        "item.prehistoricsurvival.dilophosaurus_egg": "Dilophosaurus Egg",
        "item.prehistoricsurvival.tyrannosaurus_egg": "Tyrannosaurus Egg",
        "item.prehistoricsurvival.dodo_spawn_egg": "Dodo Spawn Egg",
        "item.prehistoricsurvival.velociraptor_spawn_egg": "Velociraptor Spawn Egg",
        "item.prehistoricsurvival.dilophosaurus_spawn_egg": "Dilophosaurus Spawn Egg",
        "item.prehistoricsurvival.tyrannosaurus_spawn_egg": "Tyrannosaurus Spawn Egg",
        "prehistoricsurvival.label.alpha": "Alpha",
        "prehistoricsurvival.label.taming": "Taming %s%%",
        "prehistoricsurvival.stage.baby": "Baby",
        "prehistoricsurvival.stage.teen": "Teen",
        "prehistoricsurvival.stage.adult": "Adult",
        "prehistoricsurvival.message.needs_sedation": "It is awake and furious: sedate it with sedative meat first.",
        "prehistoricsurvival.message.too_wary": "It distrusts you. Approach slowly and crouching.",
        "prehistoricsurvival.message.too_soon": "It does not trust you yet. Come back in %s s.",
        "prehistoricsurvival.tooltip.egg": "Level %s · Incubation %s%%",
        "prehistoricsurvival.guide.title": "Explorer's Guide",
        "prehistoricsurvival.guide.cover1": "Prehistoric",
        "prehistoricsurvival.guide.cover2": "Survival",
        "prehistoricsurvival.guide.subtitle": "Paleontologist's Field Guide",
        "prehistoricsurvival.guide.open": "Click to open",
        "prehistoricsurvival.guide.index": "Contents",
        "prehistoricsurvival.guide.added": "Added in v%s",
        "prehistoricsurvival.guide.new": "NEW",
        "prehistoricsurvival.guide.page": "%s/%s",
        "prehistoricsurvival.guide.back": "Contents",
        "prehistoricsurvival.guide.received": "You received the Explorer's Guide. Right-click it to open it.",
    },
}


def lang(events):
    for code, fname in (("es", "es_es"), ("en", "en_us")):
        data = dict(LANG[code])
        for ev, (species, group) in events.items():
            base = group.replace("_alpha", "")
            text = VERBS[code].get(base, "{n}").replace("{n}", NAMES[code][species])
            if group.endswith("_alpha"):
                text = ("Alfa: " if code == "es" else "Alpha: ") + text
            data[f"subtitles.prehistoricsurvival.{ev}"] = text
        wj(os.path.join(A, "lang", fname + ".json"), data)


# =========================================================================== datos de juego
def pool(item, lo, hi, smelt=False, looting=(0, 1)):
    fn = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": float(lo), "max": float(hi)}}]
    if smelt:
        fn.append({"function": "minecraft:furnace_smelt", "conditions": [
            {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"flags": {"is_on_fire": True}}}]})
    fn.append({"function": "minecraft:looting_enchant", "count": {"min": float(looting[0]), "max": float(looting[1])}})
    return {"rolls": 1.0, "entries": [{"type": "minecraft:item", "name": item, "functions": fn}]}


def gamedata():
    meat = "prehistoricsurvival:raw_dino_meat"
    loot = {
        "dodo": [pool(meat, 1, 2, True), pool("prehistoricsurvival:dodo_feather", 0, 2)],
        "velociraptor": [pool(meat, 1, 3, True), pool("prehistoricsurvival:raptor_claw", 0, 2), pool("minecraft:leather", 0, 1)],
        "tyrannosaurus": [pool(meat, 4, 8, True, (1, 2)), pool("prehistoricsurvival:rex_tooth", 1, 3), pool("minecraft:leather", 2, 4, looting=(0, 2))],
        "dilophosaurus": [pool(meat, 1, 3, True), pool("prehistoricsurvival:venom_gland", 0, 2), pool("minecraft:leather", 0, 1)],
    }
    for name, pools in loot.items():
        wj(os.path.join(D, "loot_tables", "entities", name + ".json"), {"type": "minecraft:entity", "pools": pools})
    for nest in ("nest", "mud_nest", "mound_nest"):
        wj(os.path.join(D, "loot_tables", "blocks", nest + ".json"), {"type": "minecraft:block", "pools": [{
            "rolls": 1.0, "entries": [{"type": "minecraft:item", "name": f"prehistoricsurvival:{nest}"}],
            "conditions": [{"condition": "minecraft:survives_explosion"}]}]})

    habitats = {
        "dodo": (["#minecraft:is_jungle", "minecraft:plains", "minecraft:sunflower_plains", "minecraft:forest",
                  "minecraft:flower_forest", "minecraft:savanna"], 8, 3, 6),
        "velociraptor": (["#minecraft:is_jungle", "minecraft:forest", "minecraft:flower_forest", "minecraft:savanna",
                          "minecraft:plains"], 4, 2, 4),
        "tyrannosaurus": (["#minecraft:is_jungle", "minecraft:savanna", "minecraft:forest"], 1, 1, 1),
        "dilophosaurus": (["#minecraft:is_jungle", "minecraft:swamp", "minecraft:mangrove_swamp", "minecraft:forest"], 3, 1, 2),
    }
    for name, (biomes, weight, lo, hi) in habitats.items():
        wj(os.path.join(D, "tags", "worldgen", "biome", f"{name}_habitat.json"), {"replace": False, "values": biomes})
        wj(os.path.join(D, "forge", "biome_modifier", f"{name}_spawns.json"), {
            "type": "forge:add_spawns", "biomes": f"#prehistoricsurvival:{name}_habitat",
            "spawners": {"type": f"prehistoricsurvival:{name}", "weight": weight, "minCount": lo, "maxCount": hi}})

    tags = D + "/tags/entity_types/"
    wj(tags + "predators.json", {"replace": False, "values": [
        "minecraft:wolf", "minecraft:fox", "minecraft:ocelot", "prehistoricsurvival:velociraptor",
        "prehistoricsurvival:tyrannosaurus", "prehistoricsurvival:dilophosaurus",
        {"id": "prehistoricsurvival:carnotaurus", "required": False},
        {"id": "prehistoricsurvival:spinosaurus", "required": False},
        {"id": "prehistoricsurvival:compsognathus", "required": False},
        {"id": "prehistoricsurvival:titanoboa", "required": False}]})
    wj(tags + "prey_small.json", {"replace": False, "values": [
        "prehistoricsurvival:dodo", "minecraft:chicken", "minecraft:rabbit", "minecraft:sheep", "minecraft:pig"]})
    wj(tags + "prey_large.json", {"replace": False, "values": [
        "prehistoricsurvival:dodo", "prehistoricsurvival:velociraptor", "prehistoricsurvival:dilophosaurus",
        "minecraft:cow", "minecraft:sheep", "minecraft:pig", "minecraft:horse", "minecraft:donkey",
        "minecraft:llama", "minecraft:goat", "minecraft:chicken", "minecraft:rabbit"]})
    wj(tags + "apex_predators.json", {"replace": False, "values": ["prehistoricsurvival:tyrannosaurus"]})

    def cook(kind, time, name):
        wj(os.path.join(D, "recipes", name + ".json"), {
            "type": f"minecraft:{kind}", "category": "food", "ingredient": {"item": "prehistoricsurvival:raw_dino_meat"},
            "result": "prehistoricsurvival:cooked_dino_meat", "experience": 0.35, "cookingtime": time})
    cook("smelting", 200, "cooked_dino_meat_from_smelting")
    cook("smoking", 100, "cooked_dino_meat_from_smoking")
    cook("campfire_cooking", 600, "cooked_dino_meat_from_campfire")

    def shaped(name, pattern, key, result):
        wj(os.path.join(D, "recipes", name + ".json"), {
            "type": "minecraft:crafting_shaped", "category": "misc", "pattern": pattern, "key": key,
            "result": {"item": f"prehistoricsurvival:{result}"}})
    shaped("nest", ["S S", "WWW"], {"S": {"item": "minecraft:stick"}, "W": {"item": "minecraft:wheat"}}, "nest")
    shaped("mud_nest", ["S S", "MMM"], {"S": {"item": "minecraft:stick"}, "M": {"item": "minecraft:mud"}}, "mud_nest")
    shaped("mound_nest", ["L L", "MMM", "MMM"], {"L": {"tag": "minecraft:logs"}, "M": {"item": "minecraft:mud"}}, "mound_nest")
    wj(os.path.join(D, "recipes", "sedative_meat.json"), {
        "type": "minecraft:crafting_shapeless", "category": "misc",
        "ingredients": [{"item": "prehistoricsurvival:raw_dino_meat"}, {"item": "minecraft:fermented_spider_eye"},
                        {"item": "minecraft:sweet_berries"}],
        "result": {"item": "prehistoricsurvival:sedative_meat"}})
    wj(os.path.join(D, "recipes", "explorer_guide.json"), {
        "type": "minecraft:crafting_shapeless", "category": "misc",
        "ingredients": [{"item": "minecraft:book"}, {"item": "minecraft:leather"}],
        "result": {"item": "prehistoricsurvival:explorer_guide"}})


def run():
    events = sounds()
    lang(events)
    gamedata()
