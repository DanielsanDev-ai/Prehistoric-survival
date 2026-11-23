# Estado del proyecto — v0.2.0

Última actualización: 23-nov-2026.

## Qué está verificado y qué no
* **v0.1.0**: compilada y probada por el director creativo en Minecraft (dodo bebé/adulto/domesticación OK).
* **v0.2.0 — comprobado en mi entorno (real)**: 48 JSON válidos; las 38 texturas y 38 sonidos `.ogg` existen y
  se inspeccionaron/decodificaron; cada modelo, textura, sonido y clave de idioma referenciados existe;
  todos los `import` internos resuelven; llaves y paréntesis equilibrados en los 43 archivos Java.
* **v0.2.0 — NO comprobado**: que **compile** (sin Forge ni `javac` aquí), que las animaciones/modelos se vean
  bien en el juego, el equilibrio y el rendimiento. Todo lo de abajo está *escrito*, no *probado*.

## Implementado en la v0.2.0 (código y recursos existentes)
**Especies**: Dodo (v0.1) · **Velociraptor** · **Tyrannosaurus rex**.

| | Dodo | Velociraptor | T. rex |
|---|---|---|---|
| Hitbox adulto | 0,5 × 1,0 | 0,6 × 0,9 | 2,2 × 4,4 |
| Tamaño (modelo) | ~1 m | cadera ~0,5 m, ~1,7 m largo | cadera ~2,9 m, cabeza ~4,9 m, ~9 m largo |
| Vida / daño | 12 / 1 | 28 / 5 | 160 / 22 |
| Dieta | herbívoro | carnívoro (presas pequeñas) | carnívoro (presas grandes) |
| Sociedad | bandada con alfa | manada, caza coordinada | solitario, territorial (18 bloques) |
| Domesticación | **comida** (semillas) | **sigilo** (carne cruda agachado) | **sedación** (carne sedante + carne cocinada) |
| Recurso único | pluma | garra de raptor | diente de T. rex |
| Incubación / gestación | 9.000 / 3.000 ticks | 12.000 / 5.000 | 18.000 / 8.000 |
| Etapas (bebé+juvenil) | 10 + 20 min | 10 + 15 min | 20 + 40 min |

**Sistemas nuevos**
* Reproducción, huevos (bloque + objeto), nidos, incubación por calor, eclosión con genética, madre que vigila el nido.
* IA de depredadores: caza con hambre, acecho y carga, flanqueo en manada, huida herido, comer cadáver, advertencia
  territorial, investigar ruidos, dormir de noche (se despiertan si te acercas), sedación.
* Los alfas sueltan Esencia alfa; los depredadores salvajes retienen el botín de sus presas y lo sueltan al comer.
* Los herbívoros (dodo) huyen de velociraptor y T. rex; los raptores huyen del T. rex.

## Pendiente (NO existe todavía)
* Otras 10 especies: Dilophosaurus, Brachiosaurus, Parasaurolophus, Triceratops, Carnotaurus, Spinosaurus,
  Compsognathus, Pachycephalosaurus, Titanoboa (+ el resto de la lista inicial).
* Monturas, sillas, habilidades (clic derecho / R), órdenes avanzadas (defender zona…).
* **Cadáver visible**: hoy la presa desaparece al morir y el depredador come en ese punto (animación sí; cuerpo no).
* Padres que cazan y llevan comida a las crías; crías que siguen a sus padres concretos (hoy siguen a adultos de su especie).
* Utilidad de los recursos (garra, diente, esencia): aún no hay armas, armaduras ni crafteos que los usen.
* Pistola y dardos tranquilizantes (la sedación por carne ya existe y usará el mismo sistema), armas de hueso,
  armaduras, trofeos y cráneos, construcciones (cercas, corrales, comederos, incubadoras).
* Juvenil con proporciones propias (usa el modelo adulto a menor escala).
* Barra de vida gráfica y pantalla de estadísticas (hoy: texto sobre la criatura), sonidos ambientales de bioma.
* Variantes visuales de huevo por especie solo para 3 especies; dimorfismo adicional.
* Optimización medida (nada medido todavía).

## Errores conocidos / riesgos
* **Primera compilación**: pueden aparecer errores en el código nuevo (los puntos con más riesgo: `DinoEggBlockEntity`,
  `DinoEggBlock.getTicker`, `TheropodModel`, `HuntTargetGoal`). Envíame el error de Actions y lo arreglo.
* El T. rex es enorme: puede atascarse en árboles o pasillos estrechos (pathfinding de vanilla).
* Un T. rex sedado despierta furioso y ataca al jugador más cercano.
* La tarea de comer cadáveres solo se activa si el depredador *salvaje* mata a la presa (las mascotas no capturan botín).
* Si ves los ojos o dientes mal colocados, es la orientación de texturas; se ajusta en `tools/theropods.py`.
* Los tiempos (gestación, incubación, crecimiento) son largos a propósito: se ajustan en `SpeciesData` de cada especie.

## Siguiente versión propuesta (tú decides)
* **v0.3** — Triceratops y Parasaurolophus (herbívoros defensivos, cargas, alertas), protección de crías,
  cadáver visible, primeras monturas (T. rex y raptor) con habilidades.
* **v0.4** — Pistola/dardos tranquilizantes, armas y armaduras con los recursos, trofeos; Carnotaurus y Spinosaurus.
