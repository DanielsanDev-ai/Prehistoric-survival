# Changelog

## 0.2.0
Nuevo
- **Velociraptor** y **Tyrannosaurus rex** (modelo propio con cría distinta, 3 colores × macho/hembra + alfa,
  animaciones de caminar, amenaza, mordisco, comer, dormir y poner huevo; sonidos propios).
- **Reproducción completa**: apareamiento → gestación → puesta en un nido (o en el suelo) → huevo-bloque →
  incubación según calor → eclosión. Genética (nivel, color, alfa, sexo) heredada de los padres.
- **Nido** (craftable) y **huevo de dinosaurio** (bloque con 3 etapas de grietas y aspecto por especie;
  se puede recoger y transportar conservando datos). Madres que vigilan su nido.
- **Carnívoros**: caza solo con hambre, acecho→carga, caza en manada con flanqueo, huida al estar herido,
  alimentarse del cadáver (animación, huesos y restos), advertencia territorial en 6 pasos,
  investigación de ruidos (T. rex), alertas de manada.
- **Un método de domesticación distinto por especie**: dodo (comida), velociraptor (sigilo), T. rex (sedación).
- **Un recurso único por especie**: pluma de dodo, garra de raptor, diente de T. rex; los alfas sueltan Esencia alfa.
- **Tamaños reales** por especie y por edad (un T. rex bebé mide ~20% de un adulto).
- Carne sedante, huevos generadores nuevos, recetas, aparición por biomas, tablas de botín.

Cambios
- Dodo: hitbox y modelo más pequeños (1 m real). Los datos guardados de v0.1.0 siguen siendo válidos.
- Formato de guardado versión 2 (lee sin problema el de la versión 1).

## 0.3.0
Nuevo
- **Enciclopedia de campo**: se entrega al iniciar sesión por primera vez; portada, índice y páginas para Dodo, Velociraptor, T. rex, Dilophosaurus, huevos, incubación, crecimiento y criaturas Alfa.
- **Registro ampliable de páginas**: nuevas secciones se agregan mediante `GuideBookPages.register(...)`.
- **Dilophosaurus**: entidad propia, modelo con dos crestas y gola desplegable, animación de amenaza, sonidos registrados, aparición natural, huevo incubable, cría, crecimiento y domesticación por sigilo.
- Huevos de Dilophosaurus con datos de especie y genética conservados al recogerlos; incubación con el sistema existente.
- Traducciones en español e inglés, modelos de inventario y modelos de huevo para la nueva especie.

Conservación
- Se conserva la base v0.2.0: criaturas previas, domesticación, seguimiento del dueño, IA, reproducción, incubación y guardado de datos.
- Se mantiene Minecraft Java 1.20.1, Forge 47.3.0 y Java 17.

Limitaciones
- No se añadió la habilidad de proyectil venenoso del Dilophosaurus.
- La gola se abre como animación de amenaza; no se crearon sonidos originales nuevos, se reutilizaron eventos de sonido existentes como recurso provisional.
- La compilación y la prueba dentro de Minecraft quedan pendientes de la ejecución de GitHub Actions y de una prueba en juego.
