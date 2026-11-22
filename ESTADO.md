# Estado del proyecto — v0.1.0

Última actualización: 22-nov-2026. **Nada de lo siguiente ha sido ejecutado en Minecraft.**

## Verificado en el entorno de desarrollo (real)
* Los 18 archivos `.json` del proyecto son JSON válido.
* Las 7 texturas del dodo, 3 iconos y 10 sonidos `.ogg` existen, se generaron con `tools/generate_assets.py`
  y los `.ogg` se decodifican correctamente (Vorbis, mono, 44,1 kHz). Las texturas se inspeccionaron a mano.
* Estructura y llaves/paréntesis de los 18 archivos Java equilibradas.

## NO verificado (escrito, pero nunca compilado ni ejecutado)
* Todo el código Java (~1.300 líneas). No había `javac`, ni Forge, ni red.
* Que las animaciones del modelo se vean bien en el juego (ojos, orientación de alas, postura de dormir).
* Que el sonido sintético resulte agradable (es una síntesis básica, no grabaciones).
* El rendimiento y el equilibrio de las cifras.

## Implementado (en código y recursos)
**Base técnica**
* Proyecto Forge 1.20.1 con compilación automática (GitHub Actions).
* Clase base `PrehistoricEntity` para todas las criaturas: género, nivel (1-30; alfas 30-40), alfa, variante
  de color, crecimiento por 3 etapas, hambre, domesticación, guardado de datos con número de versión.
* `SpeciesData`: tabla de equilibrio por especie (vida, daño, velocidad, tiempos, probabilidad de alfa…).

**Dodo (primera especie)**
* Modelo 3D propio hecho a mano: **modelo de bebé distinto** (cabeza grande, patas cortas) y modelo adulto.
* 7 texturas: 3 colores × macho/hembra (diferencias de cabeza, pico y alas) + alfa propio.
* Tamaño según edad (se redimensiona también el hitbox), hembras algo más pequeñas, alfa 1,3×.
* Animaciones: caminar, aleteo al caer (y planeo), cabeza que sigue la mirada, comer (picoteo),
  dormir (agachado, cabeza escondida), sentado.
* IA: huir de depredadores (etiqueta `predators`), pánico al ser atacado, **alerta a su bandada**
  (los demás huyen y graznan), seguir a la bandada (prefiere al alfa; las crías solo siguen a adultos),
  pastar (busca césped, se acerca, come y convierte el bloque en tierra), **dormir de noche**
  (se despierta si un jugador se acerca a menos de 5 bloques o si le atacan), curiosidad por el jugador, ser tentado con semillas.
* Hambre real: baja con el tiempo, se recupera pastando; sin comer no crece y se debilita
  (la inanicion nunca lo mata: se queda en 2 de vida).
* Domesticación **progresiva**: semillas/bayas, con enfriamiento entre comidas; más difícil a mayor nivel y
  el doble para alfas; la confianza se pierde lentamente si dejas de alimentarlo. Domesticado: te sigue,
  clic vacío = sentarse/levantarse, se alimenta y cura con semillas.
* Información sobre la criatura: nombre, "Alfa", nivel, vida actual/máxima y % de domesticación,
  visible a menos de 16 bloques (o mirándolo hasta 32).
* Aparición natural por biomas (junglas, bosques, llanuras, sabana) en grupos de 3 a 6.
* Botín: carne cruda (asable en horno, ahumador y fogata) y plumas.
* Sonidos propios: ambiente (3), dolor (2), muerte, alarma (2) y cría (2).
* Español e inglés.

## Pendiente (NO existe todavía)
* Las otras 11 especies: Velociraptor, Dilophosaurus, T. rex, Brachiosaurus, Parasaurolophus, Triceratops,
  Carnotaurus, Spinosaurus, Compsognathus, Pachycephalosaurus, Titanoboa.
* Carnívoros: caza, persecución, alimentarse del cadáver (animación y comportamiento).
* Territorios, advertencias en 6 pasos, familias y protección de crías, huevos, nidos, incubación,
  reproducción (aquí está desactivada a propósito: los huevos llegan en la v0.2).
* Monturas, sillas y habilidades (clic derecho / R), órdenes avanzadas (defender zona, etc.).
* Barra de vida gráfica (ahora es texto), pantalla de estadísticas, inventario de criaturas.
* Armas y herramientas de hueso/piedra, pistola y dardos tranquilizantes, armaduras, trofeos y cráneos,
  construcciones (cercas, corrales, comederos, incubadoras), sonidos ambientales, huesos/dientes/garras.
* Etapa juvenil con proporciones propias (ahora usa el modelo adulto a menor escala).
* Sonidos de pasos, comer y movimiento propios (hoy usa el paso de gallina de vanilla).
* Optimización medida (no se ha podido medir nada).

## Errores conocidos / riesgos
* **Sin compilar**: puede haber errores de sintaxis o de nombres de la API de Forge/Minecraft.
  Puntos con más riesgo: firma de `AvoidEntityGoal` (7 argumentos), `SpawnPlacementRegisterEvent`,
  `ForgeEventFactory.getMobGriefingEvent`, plugin de Gradle `foojay`.
* Si los ojos o las puntas de ala salen en el sitio equivocado, es la orientación de texturas: se ajusta
  en `tools/generate_assets.py`.
* El nombre/nivel flotante puede solaparse con otros nombres si hay muchas criaturas juntas.
* Los alfas del dodo aparecen 1 de cada 60: para probarlos rápido, baja `alphaChance` en `DodoEntity`.
* El crecimiento tarda 30 min de juego reales (12.000 + 24.000 ticks) — ajustable en `DodoEntity.DATA`.

## Hoja de ruta propuesta (tú decides el orden)
* **v0.2** — Sistema de huevos, nidos e incubación; Velociraptor y T. rex (primeros carnívoros: caza,
  cadáveres, territorio y advertencias).
* **v0.3** — Triceratops y Parasaurolophus; protección de crías; primeras monturas.
* **v0.4** — Pistola/dardos tranquilizantes, armas de hueso, trofeos; resto de especies.
