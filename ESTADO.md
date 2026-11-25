# Estado del proyecto — v0.3.0

Minecraft Java **1.20.1** · Forge **47.3.0** · Java **17**

## Base conservada
La v0.3.0 se desarrolla sobre el código completo de la v0.2.0 en la rama `v0.3.0-development`.
Se conservan Dodo, Velociraptor y Tyrannosaurus rex, sus entidades y modelos, IA, domesticación,
seguimiento del dueño, reproducción, nidos, incubación, eclosión, herencia genética, crecimiento y guardado.

## Añadido en v0.3.0
- Libro de campo entregado una sola vez por jugador al entrar a un mundo.
- Interfaz con portada visual de libro, índice, navegación, secciones por criatura y sistemas generales.
- Registro central de páginas extensible con `GuideBookPages.register(...)`.
- Nueva entidad Dilophosaurus, con atributos, comportamiento depredador, domesticación por sigilo y seguimiento del dueño.
- Modelo específico del Dilophosaurus con dos crestas, gola desplegable, animación de amenaza y modelo de cría.
- Huevo de Dilophosaurus registrado e integrado al bloque común de huevos, incubación, eclosión y herencia genética.
- Spawn egg, aparición en biomas del hábitat de raptor, sonidos registrados y traducciones ES/EN.
- Nombre Alfa, estadísticas y guardado siguen usando la base común de PrehistoricEntity.

## Limitaciones conocidas
- No se implementó el proyectil venenoso solicitado.
- La gola se despliega cuando la criatura entra en estado de amenaza/agresividad; no existe una acción de combate remota nueva.
- Los eventos de sonido del Dilophosaurus reutilizan temporalmente muestras ya existentes; no hay grabaciones originales.
- El libro utiliza una interfaz de pergamino dibujada con paneles y colores; el icono de inventario tiene textura propia.
- No se ha confirmado aún la compilación ni la apariencia en juego. La confirmación depende de GitHub Actions y prueba manual en Minecraft.

## Compatibilidad y prueba
- Mantener Java 17, Minecraft 1.20.1 y Forge 47.3.0.
- Instalar el JAR de v0.3.0 retirando el JAR anterior de la carpeta `mods`.
- Probar en una copia del mundo: entrega del libro, navegación, aparición del Dilophosaurus, seguimiento tras domesticarlo,
  cría y crecimiento, huevos, incubación, eclosión y persistencia tras salir y volver a entrar.
