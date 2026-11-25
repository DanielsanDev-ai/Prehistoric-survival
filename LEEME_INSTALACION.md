# Prehistoric Survival — v0.3.0

Minecraft Java **1.20.1** · Forge **47.3.0** · Java **17**

Esta versión parte del proyecto completo v0.2.0. No sustituye la base de criaturas: conserva Dodo, Velociraptor,
Tyrannosaurus rex y sus sistemas de IA, domesticación, seguimiento, reproducción, huevos, incubación,
eclosión, crecimiento y datos guardados.

## Obtener el mod
1. Abre la pestaña **Actions** del repositorio.
2. En la ejecución correspondiente a la rama `v0.3.0-development`, comprueba que el trabajo **Compilar** termina en verde.
3. Descarga el artefacto **prehistoric-survival-jar** para el mod compilado.
4. Descarga también **prehistoric-survival-0.3.0-project** si necesitas el código fuente.
5. Usa Java 17, Minecraft 1.20.1 y Forge 47.3.0. Haz una copia de seguridad de tus mundos y retira el JAR anterior antes de instalar el nuevo.

## Libro de campo
El jugador recibe la enciclopedia al entrar por primera vez. Se entrega una sola vez por jugador.
Abre el objeto **Enciclopedia de Prehistoric Survival** desde el inventario para navegar por la portada,
el índice y las fichas de criaturas/sistemas.

Las futuras páginas se agregan en `GuideBookPages.java` con `GuideBookPages.register(new Page(...))`.
El registro es acumulativo en el código: no elimina secciones anteriores cuando se añade una página nueva.

## Dilophosaurus
- Aparece en biomas del hábitat configurado para el Velociraptor.
- Tiene modelo propio, dos crestas craneales y gola extensible que se despliega en estado de amenaza.
- Es depredador mediano y utiliza la IA común de caza, alimentación, descanso y nidos.
- Se domestica acercándose agachado y ofreciéndole carne aceptada por la especie.
- Tiene huevo propio, incubación, eclosión, herencia genética y etapas de crecimiento.
- Las criaturas domesticadas utilizan el seguimiento del dueño ya presente en la base.

## Pruebas recomendadas
Hazlas en una copia del mundo.

| Prueba | Qué comprobar |
|---|---|
| Libro | Aparece tras entrar por primera vez y abre la interfaz; no se duplica al volver a entrar. |
| Navegación | Portada, índice, secciones, botones anterior/siguiente y cierre. |
| Dilophosaurus | Spawn egg, aparición natural, modelo, textura, sonidos y nombre. |
| Domesticación | Acercamiento agachado, progreso y cambio a mascota. |
| Seguimiento | La mascota sigue al jugador; mano vacía cambia sentarse/seguir. |
| Cría | Huevo de Dilo, incubación en condiciones compatibles, grietas y eclosión. |
| Persistencia | Salir y volver a entrar conserva dueño, genética, crecimiento y progreso del huevo. |
| Versiones anteriores | Dodo, raptor y T. rex siguen funcionando como antes. |

## Estado de verificación
No marques esta versión como compilada hasta ver la ejecución de GitHub Actions en verde.
La inspección del código no sustituye una prueba real en Minecraft.
Consulta `ESTADO.md` y `CHANGELOG.md` para el detalle de las características y limitaciones.
