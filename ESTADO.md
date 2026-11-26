# Estado del proyecto — v0.3.0

Última actualización: 24-nov-2026.

## Verificación (lo que SÍ y lo que NO)
* **v0.1.0 y v0.2.0**: compiladas por el director creativo en GitHub Actions; la v0.1.0 además probada en juego.
* **v0.3.0 — comprobado aquí (real)**
  * Sintaxis de los **53 archivos Java** validada con el compilador de Java (sin errores de sintaxis).
  * `tools/check_project.py` sin errores: 111 JSON válidos, 62 texturas, 82 sonidos, estados de bloque completos
    (3 etapas × 4 especies × con/sin nido), todos los modelos/texturas/sonidos/idiomas referenciados existen,
    ModSounds/ModItems/ModEntities/ModBlocks usados existen, imports internos resuelven, entradas del libro válidas.
  * Sonidos: medidos (sin silencios ni recortes, firmas espectrales distintas por especie).
  * Modelos de terópodos: vistas previas propias (lateral y frontal) para verificar proporciones, ángulos y gorguera.
  * Toda la API de Minecraft/Forge que es NUEVA respecto a la v0.2.0 compilada se revisó a mano.
* **v0.3.0 — NO comprobado**
  * **Que compile**: no tengo los jars de Minecraft/Forge, así que no se puede comprobar tipos contra la API.
    **No puedo confirmarte que compile**; lo confirma GitHub Actions. Punto de mayor incertidumbre:
    `InventoryScreen.renderEntityInInventoryFollowsMouse` en `GuideScreen.java` (vista previa 3D del libro).
    Si falla, es una sola línea y la corrijo.
  * Cómo se ven y se oyen en el juego: modelos, animaciones, brillo de alfas, libro y sonidos (son síntesis; no he
    podido escucharlos, solo medirlos). El equilibrio y el rendimiento.

## Implementado
**Libro-guía**: portada y páginas con arte propio, índice por secciones, 28 entradas, paginación automática, vista previa
3D con ciclo de etapas, marca NUEVO, entrega única al primer inicio, receta (libro + cuero). Ampliable con JSON.
**Criaturas (4)**: dodo, velociraptor, T. rex, dilophosaurus, cada una con 3 etapas (modelos propios), alfa, dimorfismo,
3 colores × macho/hembra, sonidos propios, método de domesticación y recurso único:

| | Dodo | Velociraptor | T. rex | Dilophosaurus |
|---|---|---|---|---|
| Tamaño real | ~1 m | cadera ~0,5 m | cadera ~3 m | cadera ~1 m |
| Domesticación | comida | sigilo | sedación | confianza y tiempo |
| Recurso | pluma | garra | diente | glándula de veneno |
| Nido preferido | paja | barro | montículo | barro |
| Habilidad | planea | caza en manada | rugido, territorio | veneno (ralentiza+ciega) |

**Sistemas**: crecimiento continuo y etapas, alfas (armadura, resistencia, brillo, voz, esencia), nidos (3), huevos (4 + 3
fases), incubación con calor/nido/efectos, eclosión con genética, cuidado parental, nidos salvajes, caza y territorio.

## Pendiente / límites (sin disimular)
* **Rediseño**: es una mejora real de los terópodos (más piezas, escamas, plumas, S-curve), pero sigue siendo estilo bloque
  de Minecraft. **El dodo no se rediseñó** (solo se le añadió adolescente y cresta de alfa).
* **Cambio de etapa**: el tamaño crece de forma continua, pero el modelo cambia de golpe al cambiar de etapa (cría →
  adolescente → adulto); se disimula con una nube de polvo. No hay transición intermedia entre modelos.
* **Sonidos**: mejor técnica (formantes, rugosidad, reverb) pero siguen siendo síntesis, no grabaciones; puede que alguno
  siga sonando artificial. Si quieres sonidos más reales hace falta aportar grabaciones con licencia.
* **Libro**: los textos están en español; en inglés solo hay títulos (el resto cae al español).
* **Efectos de incubación**: partículas y sonidos; el huevo no "tiembla" (requeriría un renderizador de bloque propio).
* **Nidos salvajes**: se crean al aparecer una hembra nueva (30%), no son estructuras de generación de mundo.
* **Padres**: siguen, defienden y vigilan el nido; todavía no cazan ni traen comida a las crías.
* **Dilophosaurus**: el domesticado escupe contra los objetivos de su dueño, pero aún no hay montura ni control manual de la habilidad.
* Sigue pendiente de versiones anteriores: 8 especies más, monturas y habilidades, cadáver visible, utilidad de los
  recursos (armas/armaduras), pistola y dardos, trofeos, construcciones, barra de vida gráfica.

## Mantenimiento del libro
Cuando cambie un valor del juego (`SpeciesData`, recetas, tiempos), actualiza la entrada de `tools/guide_content.py` que lo
menciona y vuelve a ejecutar `python3 tools/generate_assets.py`.
