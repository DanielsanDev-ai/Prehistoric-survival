# Prehistoric Survival — v0.2.0 (huevos y nidos + Velociraptor + T. rex)

Minecraft Java **1.20.1** · Forge **47.3.0** · Java **17**

> ⚠️ La **v0.1.0 la compilaste y probaste tú y funciona**. La **v0.2.0 es nueva y NO ha sido compilada ni probada
> por mí** (sigo sin compilador ni Forge en mi entorno). Si GitHub Actions marca error, copia el texto del error
> y lo corrijo. Los cambios respecto a v0.1.0 están en `CHANGELOG.md`.

## Cómo obtener el .jar (igual que la vez anterior)
1. Sube **toda la carpeta** a tu repositorio de GitHub sustituyendo los archivos (incluida la carpeta oculta `.github`).
   Si GitHub ya tiene la v0.1.0, borra primero los archivos antiguos o sube encima: lo importante es que
   `src/`, `tools/`, `build.gradle`, `gradle.properties` y `settings.gradle` sean los nuevos.
2. Pestaña **Actions** → espera a que termine "Compilar mod" (en verde).
3. Descarga el artefacto **prehistoric-survival-jar** → dentro está `prehistoricsurvival-0.2.0.jar`.
4. Cópialo a la carpeta `mods` de Forge 1.20.1 (quita antes el .jar de la v0.1.0).

Tus dodos y mundos de la v0.1.0 **siguen funcionando** (los datos guardados son compatibles).

## Guía rápida de pruebas
*Pestaña creativa **Prehistoric Survival**: huevos generadores, nido, huevos, objetos.*

| Qué probar | Cómo |
|---|---|
| **Tamaños reales** | Genera un dodo, un velociraptor y un T. rex juntos y compáralos (y al lado, un bebé de cada). |
| **Domesticar dodo** | Semillas/bayas, varias veces (como antes). |
| **Domesticar velociraptor** | **Agachado (Shift)** y con **carne cruda** en la mano, clic derecho, ~8 veces. De pie desconfía y pierde confianza. |
| **Domesticar T. rex** | Craftea **carne sedante** (carne de dino cruda + ojo de araña fermentado + bayas dulces). Dásela: se duerme ~45 s. Mientras duerme, dale **carne cocinada** ~10 veces. Si se despierta, ¡te ataca! |
| **Reproducción** | Un macho y una hembra adultos y bien alimentados (barra de hambre ≥70). Se aparean → gestación → la hembra busca un **nido** y pone un **huevo**. |
| **Incubación** | El huevo solo avanza en **biomas cálidos** (jungla, llanura, sabana) o cerca de **hoguera/horno/magma**. Un **nido** debajo la acelera. Se ven grietas según avanza. |
| **Recoger huevos** | Se pueden picar y transportar: conservan especie, genética y progreso. Se colocan en cualquier sitio. |
| **Caza en manada** | Suelta 3 velociraptores y un dodo cerca. Acechan despacio y luego cargan, rodeando a la presa. |
| **Comer la presa** | Tras abatirla, el depredador va a ella, la come con animación y deja huesos y restos. |
| **Territorio** | Acércate a un T. rex: se gira, ruge, adopta postura amenazante; si te alejas se calma; si insistes, ataca. |
| **Ruido** | Pelea con un mob a unos 40 bloques de un T. rex: viene a investigar. |

Consejo: en **creativo** los depredadores no te atacan; para probar territorialidad usa **supervivencia**.

## Regenerar texturas, modelos y sonidos
`python3 tools/generate_assets.py` (Pillow, numpy, scipy y ffmpeg). Esto también regenera
`TheropodSpecs.java` (geometría del modelo) para que modelo y textura nunca se desincronicen.
