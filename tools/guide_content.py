#!/usr/bin/env python3
"""
Contenido del libro-enciclopedia. Cada entrada es un archivo JSON en
src/main/resources/assets/prehistoricsurvival/guide/entries/. Este script los genera a partir de las
definiciones de abajo; para añadir contenido en una versión futura basta con añadir una llamada a E(...)
(o crear un JSON a mano siguiendo guide/README.txt) y poner "since" con la versión nueva.

REGLA DE MANTENIMIENTO: cuando cambie un valor del juego (SpeciesData, recetas, tiempos), actualiza aquí la
entrada que lo menciona. Las cifras de este archivo se copiaron de SpeciesData de la v0.3.0.
"""
import json
import os

import generate_assets as g

GUIDE = os.path.join(g.ASSETS, "guide")
ENTRIES = os.path.join(GUIDE, "entries")
VERSION = "0.3.0"

SECTIONS = [
    ("intro", 1, "Bienvenida", "Welcome"),
    ("creatures", 2, "Criaturas", "Creatures"),
    ("life", 3, "Vida y crianza", "Life and nurturing"),
    ("taming", 4, "Domesticación", "Taming"),
    ("behavior", 5, "Comportamiento", "Behavior"),
    ("items", 6, "Objetos y recursos", "Items and resources"),
    ("tips", 7, "Consejos", "Tips"),
    ("news", 8, "Novedades", "What's new"),
]


def tr(es, en=None):
    d = {"es": es}
    if en:
        d["en"] = en
    return d


def E(entry_id, section, order, title_es, title_en, text, facts=(), preview=None, since=VERSION):
    data = {"id": entry_id, "section": section, "order": order, "since": since,
            "title": tr(title_es, title_en),
            "facts": [tr(f) for f in facts],
            "text": [tr(p) for p in text]}
    if preview:
        data["preview"] = preview
    with open(os.path.join(ENTRIES, entry_id + ".json"), "w", encoding="utf-8") as fh:
        json.dump(data, fh, indent=2, ensure_ascii=False)


def prev(entity, stages=("adult",), scale=None):
    p = {"entity": f"prehistoricsurvival:{entity}", "stage": stages[0] if stages[0] != "alpha" else "adult",
         "alpha": stages[0] == "alpha"}
    if len(stages) > 1:
        p["stages"] = list(stages)
    if scale:
        p["scale"] = scale
    return p


def run():
    os.makedirs(ENTRIES, exist_ok=True)
    for f in os.listdir(ENTRIES):
        os.remove(os.path.join(ENTRIES, f))
    with open(os.path.join(GUIDE, "sections.json"), "w", encoding="utf-8") as fh:
        json.dump({"sections": [{"id": i, "order": o, "title": tr(es, en)} for i, o, es, en in SECTIONS]},
                  fh, indent=2, ensure_ascii=False)
    with open(os.path.join(GUIDE, "README.txt"), "w", encoding="utf-8") as fh:
        fh.write(README)

    # ------------------------------------------------------------------ BIENVENIDA
    E("welcome", "intro", 1, "Bienvenido, explorador", "Welcome, explorer", [
        "Este libro es la enciclopedia de Prehistoric Survival. Reúne todo lo que se sabe de las criaturas "
        "prehistóricas que habitan el mundo y de cómo sobrevivir entre ellas.",
        "Vuelve al Índice con el botón de abajo a la izquierda y elige una sección. Con las flechas < > (o las "
        "teclas del teclado) pasas las páginas de cada entrada.",
        "El libro crece con el mod: cada versión añade páginas nuevas, marcadas con NUEVO mientras estés en esa "
        "versión. Si lo pierdes, se fabrica con un libro y un cuero.",
    ], facts=["Versión: 0.3.0", "Criaturas: 4", "Secciones: 8"])

    E("how_to_read", "intro", 2, "Cómo usar este libro", "How to use this book", [
        "Cada criatura tiene una ficha con su modelo 3D real: se mueve con tu ratón. Las que muestran varias "
        "etapas van cambiando solas: cría, adolescente, adulto y alfa.",
        "Las secciones de Vida, Domesticación y Comportamiento explican las mecánicas comunes; las fichas de "
        "Criaturas explican lo propio de cada especie.",
        "Los tiempos se dan en minutos de juego reales (20 ticks = 1 segundo).",
    ])

    # ------------------------------------------------------------------ CRIATURAS
    E("dodo", "creatures", 1, "Dodo", "Dodo", [
        "Ave pequeña, gregaria y pacífica que vive en bandadas guiadas por un individuo alfa. Pasta césped cuando "
        "tiene hambre (el bloque se convierte en tierra) y duerme de noche.",
        "No se defiende: huye de depredadores (lobos, zorros, raptores, dilophosaurus y T. rex) y, si atacan a uno, "
        "toda la bandada cercana se alarma y escapa graznando.",
        "Dónde encontrarlo: junglas, llanuras, bosques y sabanas, en grupos de 3 a 6. Si cae de altura, aletea y "
        "planea sin hacerse daño.",
        "Cómo domesticarlo: dale semillas o bayas dulces varias veces. Cada comida aumenta su confianza; con 4 "
        "comidas (más con el nivel) será tuyo.",
        "Sus hembras ponen huevos en nidos de paja. Al morir suelta carne y plumas.",
    ], facts=["Dieta: herbívoro", "Tamaño: ~1 m", "Vida: 12", "Tame: semillas", "Drop: pluma"],
      preview=prev("dodo", ("baby", "teen", "adult", "alpha")))

    E("velociraptor", "creatures", 2, "Velociraptor", "Velociraptor", [
        "Pequeño, veloz y muy inteligente. Es el cazador de manada del ecosistema: caza solo con hambre, acecha "
        "despacio y de repente carga; los de la manada se avisan con una llamada y rodean a la presa desde "
        "ángulos distintos.",
        "Tamaño real: ~0,5 m de cadera y unos 1,7 m de largo. Plumas en los brazos y un abanico de plumas en la "
        "cola. Los machos lucen una cresta y una garganta de color; las hembras son más discretas.",
        "Cazan dodos, gallinas, conejos, ovejas y cerdos. Si lo hieren de gravedad, huye. Huye del T. rex.",
        "Dónde encontrarlo: junglas, bosques, sabanas y llanuras, en grupos de 2 a 4.",
        "Cómo domesticarlo: SIGILO. Con carne cruda en la mano, agáchate (Shift) y dásela despacio. De pie, "
        "desconfía. Necesita 8 comidas (más con el nivel).",
        "Pone sus huevos en nidos de barro. Al morir suelta carne, garras de raptor y a veces cuero.",
    ], facts=["Dieta: carnívoro", "Cadera: ~0,5 m", "Vida: 28, daño: 5", "Tame: sigilo", "Drop: garra"],
      preview=prev("velociraptor", ("baby", "teen", "adult", "alpha")))

    E("tyrannosaurus", "creatures", 3, "Tyrannosaurus rex", "Tyrannosaurus rex", [
        "El gran depredador. Solitario y territorial, domina su zona: vigila a 18 bloques, avisa con rugidos "
        "y solo ataca si insistes. Acude a investigar los combates que oiga a menos de 48 bloques.",
        "Tamaño real: ~3 m de cadera, cabeza a unos 5 m y ~9 m de largo; las hembras son más grandes que los "
        "machos. Un bebé mide apenas un 20% de un adulto.",
        "Caza presas grandes (vacas, caballos, ovejas, cerdos, llamas...) y también raptores, dodos y "
        "dilophosaurus. Come el cadáver de lo que abate.",
        "Dónde encontrarlo: junglas, sabanas y bosques. Es muy raro: aparece de uno en uno.",
        "Cómo domesticarlo: SEDACIÓN. Dale carne sedante para dormirlo y, mientras duerme, carne cocinada. "
        "Necesita 10 comidas (más con el nivel). Si despierta, ¡te ataca!",
        "Pone sus huevos en montículos de ramas y barro. Suelta carne y dientes de T. rex.",
    ], facts=["Dieta: carnívoro", "Cadera: ~3 m", "Vida: 160, daño: 22", "Tame: sedación", "Drop: diente"],
      preview=prev("tyrannosaurus", ("baby", "teen", "adult", "alpha")))

    E("dilophosaurus", "creatures", 4, "Dilophosaurus", "Dilophosaurus", [
        "Cazador de emboscada de dos crestas paralelas sobre el cráneo y una gorguera en el cuello que despliega "
        "como un abanico al amenazar o atacar, acompañada de un cascabeleo.",
        "HABILIDAD: escupe un proyectil de veneno verdoso. Al impactar causa ralentización y ceguera temporales "
        "además de daño leve. Tiene 10 segundos de reutilización (7 s el alfa, cuyo veneno es más potente). "
        "Solo los adultos pueden escupir.",
        "Tamaño real: ~1 m de cadera y ~4 m de largo. Caza animales pequeños y es territorial: advierte a 12 "
        "bloques (más cerca de su nido).",
        "Dónde encontrarlo: junglas, pantanos, manglares y bosques, en grupos de 1 o 2.",
        "Cómo domesticarlo: CONFIANZA Y TIEMPO. Dale pescado crudo (bacalao, salmón o pez tropical) una vez cada "
        "2 minutos y medio, sin atacarlo. Necesita 5 pescados (más con el nivel).",
        "Pone sus huevos en nidos de barro. Suelta carne y glándulas de veneno.",
    ], facts=["Dieta: carnívoro", "Cadera: ~1 m", "Vida: 40, daño: 6", "Tame: pescado+tiempo", "Drop: glándula"],
      preview=prev("dilophosaurus", ("baby", "teen", "adult", "alpha")))

    # ------------------------------------------------------------------ VIDA Y CRIANZA
    E("growth", "life", 1, "Etapas de crecimiento", "Growth stages", [
        "Toda criatura pasa por tres etapas: CRÍA, ADOLESCENTE y ADULTO. Cada una tiene su propio modelo con "
        "proporciones distintas: la cría tiene la cabeza grande y las patas cortas; el adolescente es larguirucho, "
        "de cabeza y ojos grandes, hocico corto y cola corta; el adulto tiene las proporciones completas.",
        "El crecimiento es progresivo: el tamaño y las estadísticas (vida, daño, velocidad) crecen poco a poco "
        "con la edad. Al cambiar de etapa se produce un 'estirón' con una nube de polvo.",
        "Duración (cría + adolescente): dodo 10+20 min, velociraptor 10+15, dilophosaurus 10+15, T. rex 20+40.",
        "Si pasa hambre (hambre por debajo del 20%), deja de crecer.",
        "Comportamiento por edad: las crías siguen a quien puso su huevo, no cazan ni advierten; los "
        "adolescentes solo cazan presas pequeñas; solo los adultos se reproducen y el dilophosaurus solo "
        "escupe de adulto. Las voces de crías y adolescentes son más agudas.",
    ], facts=["Cría > adolescente", "> adulto", "Crecen con el tiempo"],
      preview=prev("tyrannosaurus", ("baby", "teen", "adult")))

    E("alpha", "life", 2, "Criaturas Alfa", "Alpha creatures", [
        "Los Alfa son ejemplares excepcionales: raros (1 de cada 60 dodos, 1 de cada 40 raptores, 1 de cada 45 "
        "T. rex y dilophosaurus). Su nombre muestra el prefijo Alfa en dorado y negrita.",
        "Aspecto: más grandes, con pieles oscuras de colores intensos, púas de hueso en el lomo, cuernos sobre "
        "los ojos (el dodo, una cresta de plumas) y ojos, rayas y gorguera que BRILLAN en la oscuridad.",
        "Estadísticas: nivel 30-40, el doble de vida y daño (x1,8 el T. rex), piel gruesa (armadura 4) y gran "
        "resistencia al empuje.",
        "Voz: más grave, larga y potente, con eco. Los dodo alfa guían a su bandada.",
        "Recompensa: siempre sueltan Esencia alfa. El veneno del dilophosaurus alfa es más potente y se recarga "
        "antes. Son más difíciles de domesticar (el doble de comidas).",
        "Los huevos tienen una pequeña probabilidad de dar un alfa, mucho mayor si un progenitor lo es.",
    ], facts=["Nivel 30-40", "Brillan de noche", "Sueltan Esencia alfa"],
      preview=prev("tyrannosaurus", ("alpha",)))

    E("nests", "life", 3, "Nidos", "Nests", [
        "Las hembras prefieren poner sus huevos en un nido. Hay tres tipos y cada especie prefiere uno:",
        "NIDO DE PAJA (dodo): 2 palos y 3 trigos. NIDO DE BARRO (velociraptor y dilophosaurus): 2 palos y 3 barros. "
        "MONTÍCULO (T. rex): 2 troncos y 6 barros.",
        "En un nido el huevo queda hundido en la cavidad y se incuba más rápido: x1,5 en el nido que su especie "
        "prefiere y x1,25 en cualquier otro. Si no hay ningún nido cerca, la hembra pone el huevo en el suelo.",
        "Con suerte puedes encontrar nidos salvajes con un huevo: cerca de un 30% de las hembras adultas que "
        "aparecen en el mundo tienen uno. ¡Cuidado, la madre lo vigila!",
    ], facts=["Paja: dodo", "Barro: raptor, dilo", "Montículo: T. rex"])

    E("eggs", "life", 4, "Huevos e incubación", "Eggs and incubation", [
        "Cada especie pone un huevo distinto: el dodo crema con motas marrones, el velociraptor verde moteado, el "
        "tyrannosaurus grande y gris manchado y el dilophosaurus verde azulado con bandas.",
        "Para incubar necesita calor: avanza solo en biomas cálidos (jungla, llanura, sabana...) o cerca de una "
        "hoguera encendida, un horno encendido o un bloque de magma (x1,5). En el frío no avanza y aparecen "
        "copos de nieve.",
        "Mientras incuba brotan motas verdes (y chispas si hay calor extra). El cascarón se agrieta en tres "
        "fases y cruje antes de romperse.",
        "Incubación base: dodo 7,5 min, velociraptor y dilophosaurus 10 min, T. rex 15 min.",
        "Puedes recoger el huevo y llevarlo: conserva especie, genética y progreso. Si colocas un huevo de un "
        "animal domesticado, la cría nacerá domesticada.",
    ], facts=["Necesita calor", "Se puede transportar", "3 fases de grietas"])

    E("breeding", "life", 5, "Reproducción y familia", "Breeding and family", [
        "Un macho y una hembra adultos pueden aparearse si están bien alimentados (hambre al 70% o más), sanos y "
        "sin haberse apareado hace poco. El macho busca a la hembra a menos de 20 bloques.",
        "Tras aparearse hay una gestación (dodo 2,5 min, raptor y dilophosaurus 4 min, T. rex 6,5 min) y la "
        "hembra pone UN huevo, agachándose unos segundos. Nunca aparece directo en tu inventario.",
        "Descanso hasta volver a aparearse: 20 min (T. rex, 40 min).",
        "Cuidado parental: la cría que nace sigue a su madre. La madre se queda cerca del nido, y si alguien "
        "ataca a una cría, ella acude a defenderla. Si te llevas el huevo, ¡puede atacarte!",
        "La cría hereda la genética: nivel (promedio de los padres), colores y una pequeña opción de alfa.",
    ], facts=["Hambre >= 70%", "1 huevo por puesta", "La madre protege"])

    E("hunger", "life", 6, "Hambre y alimentación", "Hunger and feeding", [
        "Toda criatura tiene hambre. Baja con el tiempo; si llega a cero se debilita (sin llegar a morir de "
        "hambre, queda en 2 de vida) y deja de crecer. Con buen alimento se cura sola.",
        "Los herbívoros pastan: buscan césped, se acercan, bajan la cabeza (verás la animación) y comen; el bloque "
        "se convierte en tierra.",
        "Los carnívoros cazan cuando tienen hambre y se comen el cadáver de lo que abaten: no dejan el botín "
        "tirado, lo comen y dejan huesos y restos.",
        "Puedes alimentar a tus criaturas domesticadas con su comida para curarlas y saciarlas.",
    ], facts=["Pastan / cazan", "Comen el cadáver"])

    # ------------------------------------------------------------------ DOMESTICACIÓN
    E("taming", "taming", 1, "Domesticar: lo básico", "Taming basics", [
        "Todas las criaturas se pueden domesticar, pero cada especie con un método distinto. Nunca es "
        "instantáneo: ves el porcentaje sobre su cabeza ('Domesticando %').",
        "Las comidas necesarias suben con el nivel de la criatura (+1 cada 10 niveles) y se duplican en los alfas.",
        "No lo ataques: cada golpe te quita confianza (-20%), y la confianza se pierde sola si lo dejas.",
        "Una vez domesticada: te sigue, defiende al dueño, se sienta o se levanta con clic derecho con la mano "
        "vacía, y se alimenta y cura con su comida. Sus crías nacen domesticadas.",
    ], facts=["Dodo: comida", "Raptor: sigilo", "Dilo: confianza", "T. rex: sedación"])

    E("taming_dodo", "taming", 2, "Domesticar un dodo", "Taming a dodo", [
        "Método: COMIDA. Dale semillas (trigo, remolacha, melón o calabaza) o bayas dulces con clic derecho.",
        "Cada comida (con 5 segundos de espera entre una y otra) sube su confianza. Con 4 comidas a nivel 1 es tuyo.",
        "Consejo: acércate sin golpearlo; los dodos curiosos te miran, pero huyen si alguien ataca a la bandada.",
    ], preview=prev("dodo", ("adult",)), facts=["Semillas / bayas", "4 comidas (nivel 1)"])

    E("taming_raptor", "taming", 3, "Domesticar un velociraptor", "Taming a velociraptor", [
        "Método: SIGILO. Agáchate (Shift) con carne cruda en la mano (carne de dino, ternera, cerdo, cordero, "
        "pollo o conejo crudos) y úsala con clic derecho.",
        "Si no estás agachado, desconfía: avisa con un sonido y pierde 5% de confianza. Necesita 8 comidas a nivel 1.",
        "Aléjate de su manada si se pone agresiva: cuando uno ataca, avisa a los demás.",
    ], preview=prev("velociraptor", ("adult",)), facts=["Shift + carne cruda", "8 comidas (nivel 1)"])

    E("taming_rex", "taming", 4, "Domesticar un T. rex", "Taming a T. rex", [
        "Método: SEDACIÓN. Un T. rex despierto no deja que lo alimentes. Primero dale CARNE SEDANTE: se duerme "
        "unos 45 segundos (como mucho 2 minutos acumulados).",
        "Mientras duerme, dale carne cocinada (carne de dino asada, ternera, cerdo, cordero o pollo cocinados) cada "
        "3 segundos. Necesita 10 comidas a nivel 1: hará falta más de una carne sedante.",
        "Si despierta antes de terminar, pierde parte de la confianza y ataca al jugador más cercano. ¡Prepárate!",
    ], preview=prev("tyrannosaurus", ("adult",)), facts=["Carne sedante", "+ carne cocinada", "10 comidas (nivel 1)"])

    E("taming_dilo", "taming", 5, "Domesticar un dilophosaurus", "Taming a dilophosaurus", [
        "Método: CONFIANZA Y TIEMPO. Dale pescado crudo (bacalao, salmón o pez tropical) con clic derecho.",
        "Solo acepta un pescado cada 2 minutos y medio; si lo intentas antes, te dirá cuánto falta. Necesita 5 "
        "pescados a nivel 1 (unos 12 minutos).",
        "Sus escupitajos ciegan y ralentizan: no lo provoques o perderás su confianza.",
    ], preview=prev("dilophosaurus", ("adult",)), facts=["Pescado crudo", "1 cada 2,5 min", "5 pescados (nivel 1)"])

    E("sedation", "taming", 6, "Sedación y carne sedante", "Sedation and sedative meat", [
        "CARNE SEDANTE: carne de dinosaurio cruda + ojo de araña fermentado + bayas dulces (receta sin forma).",
        "Al dársela a una criatura que se puede sedar (como el T. rex) se duerme: no se mueve, no mira y no ataca. "
        "Dura 45 s por carne, hasta 2 minutos. Si la golpean, no se despierta pero sufre daño.",
        "Un salvaje que despierta pierde parte de la confianza ganada.",
    ], facts=["45 s por carne", "Máximo 2 min"])

    # ------------------------------------------------------------------ COMPORTAMIENTO
    E("territory", "behavior", 1, "Territorio y advertencias", "Territory and warnings", [
        "Los depredadores salvajes defienden su territorio con un aviso en pasos: 1) detectan al intruso, 2) se "
        "giran hacia él, 3) emiten un sonido de advertencia, 4) adoptan una postura amenazante, 5) si te alejas, "
        "se calman, 6) si te acercas demasiado o insistes, atacan.",
        "Distancia de aviso: velociraptor 9 bloques, dilophosaurus 12, T. rex 18. Con un nido cerca aumenta un 40%. "
        "Si duermen, reaccionan solo a la mitad de esa distancia.",
        "No te atacan en creativo y nunca atacan a sus dueños.",
    ], facts=["Raptor: 9 bloques", "Dilo: 12 bloques", "T. rex: 18 bloques"])

    E("hunting", "behavior", 2, "Caza y manadas", "Hunting and packs", [
        "Un carnívoro caza solo con hambre (por debajo del 60%), sano (más del 50% de vida) y despierto. Elige "
        "presas pequeñas (raptor y dilophosaurus) o grandes (T. rex) y nunca mascotas.",
        "La caza tiene dos fases: ACECHO (se acerca despacio mirando) y CARGA (sprint y mordisco). En manada, el "
        "primero avisa a los demás con una llamada y cada uno rodea a la presa desde un lado.",
        "Si el depredador baja del 25% de vida, abandona la lucha y huye.",
        "Después de abatir a la presa, va a comérsela con animación de comer y deja huesos y restos.",
    ], facts=["Solo con hambre", "Acecho > carga"])

    E("sleep_noise", "behavior", 3, "Sueño y ruidos", "Sleep and noise", [
        "De noche las criaturas se acuestan poco a poco (verás la postura de dormir). Se despiertan al "
        "amanecer, si alguien se acerca a menos de 5 bloques o si las atacan.",
        "El T. rex es curioso: cuando oye combates a menos de 48 bloques va a investigar. Cuidado con las peleas "
        "cerca de su territorio.",
        "Los herbívoros huyen de los depredadores y, si atacan a uno, toda la bandada reacciona.",
    ])

    E("venom", "behavior", 4, "Veneno del dilophosaurus", "Dilophosaurus venom", [
        "Cuando tiene un objetivo a media distancia, se detiene, despliega la gorguera con cascabeleo y escupe una "
        "bola de veneno verde con una estela de partículas.",
        "Efecto del impacto: ralentización II durante 8 s, ceguera durante 5 s y 2 de daño. El del alfa: "
        "ralentización III 12 s, ceguera 8 s y 4 de daño. Recarga: 10 s (alfa 7 s).",
        "Consejo: refúgiate tras un bloque; el veneno no atraviesa el terreno. Los dilophosaurus no se dañan "
        "entre sí con él.",
    ], preview=prev("dilophosaurus", ("adult",)), facts=["Ralentización", "Ceguera", "Recarga: 10 s"])

    # ------------------------------------------------------------------ OBJETOS
    E("meat", "items", 1, "Carne de dinosaurio", "Dino meat", [
        "CARNE CRUDA: la sueltan todas las criaturas al morir. Alimento pobre; sirve para domesticar al raptor.",
        "CARNE ASADA: se cocina en horno, ahumador u hoguera. Alimenta mucho y sirve para domesticar al T. rex.",
        "Si matas a una criatura en llamas, la carne ya cae asada.",
    ])

    E("resources", "items", 2, "Recursos especiales", "Special resources", [
        "PLUMA DE DODO, GARRA DE RAPTOR, DIENTE DE T. REX y GLÁNDULA DE VENENO: el recurso propio de cada "
        "especie, para cazadores.",
        "ESENCIA ALFA: la sueltan siempre las criaturas alfa.",
        "Por ahora son trofeos de caza: en futuras versiones se usarán para fabricar armas, armaduras y "
        "objetos especiales.",
    ])

    E("nest_items", "items", 3, "Nidos y huevos", "Nests and eggs", [
        "Los nidos se fabrican (ver Nidos). Los huevos se recogen de los nidos; puedes llevarlos y colocarlos "
        "donde quieras.",
        "Para obtener huevos en creativo usa la pestaña del mod. Un huevo sin datos se comporta como uno nuevo de "
        "su especie.",
    ])

    # ------------------------------------------------------------------ CONSEJOS
    E("tips", "tips", 1, "Consejos de supervivencia", "Survival tips", [
        "Observa antes de acercarte: la postura de la criatura te dice su humor. Una cabeza baja y la boca "
        "abierta es una amenaza.",
        "Los nidos salvajes siempre están vigilados. Atrae a la madre y roba el huevo desde lejos, o usa un "
        "dilophosaurus domesticado.",
        "Las hogueras aceleran la incubación en cualquier clima y los hornos encendidos también.",
        "Lleva comida: alimentar a una criatura domesticada la cura.",
        "Evita luchar cerca de un T. rex: el ruido lo atrae.",
    ])

    E("controls", "tips", 2, "Controles", "Controls", [
        "Clic derecho con comida: alimentar o domesticar. Clic derecho con la mano vacía en una criatura "
        "domesticada: sentarla o levantarla.",
        "Shift: agacharse (necesario para domesticar al velociraptor).",
        "Sobre la criatura verás su nombre, nivel, vida y el progreso de domesticación, hasta 16 bloques (32 si la "
        "miras).",
    ])

    # ------------------------------------------------------------------ NOVEDADES
    E("news_030", "news", 1, "Novedades 0.3.0", "What's new in 0.3.0", [
        "NUEVO: el Dilophosaurus, con gorguera, crestas y veneno que ralentiza y ciega.",
        "NUEVO: Guía del Explorador (este libro), que se entrega al entrar por primera vez a un mundo.",
        "NUEVO: modelos rediseñados con cuello en S, colas largas y plumas; etapa de adolescente con "
        "proporciones propias.",
        "NUEVO: alfas con púas, cuernos y brillo propio; sonidos nuevos más naturales y distintos para cada etapa.",
        "NUEVO: nidos de barro y montículos, huevos de cuatro especies, nidos salvajes, cuidado parental e "
        "incubación con efectos.",
    ], facts=["Dilophosaurus", "Libro-guía", "Adolescentes", "Nidos y alfas"])


README = """GUIA DEL EXPLORADOR - COMO AÑADIR PAGINAS EN FUTURAS VERSIONES
================================================================
El libro lee sus paginas de estos archivos (no hay que tocar codigo Java):

  assets/prehistoricsurvival/guide/sections.json     secciones del indice
  assets/prehistoricsurvival/guide/entries/*.json    una entrada (pagina) por archivo

Una entrada nueva es un JSON asi:

{
  "id": "mi_entrada",                 nombre unico
  "section": "creatures",             id de una seccion de sections.json
  "order": 5,                         orden dentro de la seccion
  "since": "0.4.0",                   version en la que se anade (muestra NUEVO mientras sea la version actual)
  "title": {"es": "Titulo", "en": "Title"},
  "facts": [ {"es": "Dieta: herbivoro"} ],              ficha corta (maximo ~5 lineas)
  "text":  [ {"es": "Parrafo 1"}, {"es": "Parrafo 2"} ],  texto, se pagina solo
  "preview": {"entity": "prehistoricsurvival:dodo", "stage": "adult",
              "stages": ["baby", "teen", "adult", "alpha"]}   (opcional) modelo 3D; "stages" lo cicla
}

Los textos son objetos {"es": ..., "en": ...}; si falta el idioma se usa el espanol.
Una seccion nueva se anade en sections.json (las vacias no se muestran).

Con las herramientas del proyecto: edita tools/guide_content.py (E(...)) y ejecuta tools/generate_assets.py.
CUANDO CAMBIE UN VALOR DEL JUEGO, ACTUALIZA LA ENTRADA QUE LO MENCIONA.
"""
