# Prehistoric Survival — v0.1.0 (base técnica + Dodo)

Minecraft Java **1.20.1** · Forge **47.3.0** · Java **17**

> ⚠️ Esta versión **no ha sido compilada ni probada dentro de Minecraft**. Fue escrita en un entorno sin
> Forge, sin Gradle y sin internet. Es muy probable que el primer intento de compilar muestre algún error
> pequeño; pásame el texto del error y lo corrijo. Detalles en `ESTADO.md`.

## Cómo obtener el .jar (sin instalar nada) — GitHub Actions

1. Crea una cuenta gratuita en https://github.com y un repositorio nuevo (privado vale).
2. Pulsa **Add file → Upload files** y arrastra **toda la carpeta del proyecto**
   (importante que se suba también la carpeta oculta `.github`).
3. Ve a la pestaña **Actions**. Verás el trabajo "Compilar mod" ejecutándose (tarda 5–10 min la primera vez).
4. Si termina en verde, ábrelo y descarga el artefacto **prehistoric-survival-jar** (un .zip que contiene el `.jar`).
5. Si termina en rojo, abre el paso "Compilar", copia el error y envíamelo.

## Cómo compilarlo en tu PC (alternativa)

1. Instala **JDK 17** (Temurin) y **Gradle 8.1.1**.
2. En la carpeta del proyecto: `gradle build`
3. El `.jar` queda en `build/libs/prehistoricsurvival-0.1.0.jar`.

## Cómo instalarlo en Minecraft

1. Instala **Forge 1.20.1 (47.3.0 o superior)** con el instalador oficial.
2. Copia el `.jar` a la carpeta `mods` de tu instancia de Minecraft.
3. Abre Minecraft con el perfil de Forge, crea un mundo.

## Cómo probar lo que hay

* Pestaña creativa **Prehistoric Survival** → *Huevo generador de dodo*.
* `/summon prehistoricsurvival:dodo` también funciona.
* Aparecen de forma natural en junglas, bosques, llanuras y sabanas (grupos de 3–6).
* Prueba: acércate de noche (duermen), mira a uno (nombre + nivel + vida), dale semillas (domesticación
  progresiva: 4+ semillas), pégale a uno (los demás huyen y graznan), suelta un lobo cerca.

## Regenerar texturas y sonidos

`python3 tools/generate_assets.py` (necesita Pillow, numpy, scipy y ffmpeg).
