# Prehistoric Survival — v0.3.0

Minecraft Java **1.20.1** · Forge **47.3.0** · Java **17**

> ⚠️ **Estado de compilación:** la v0.2.0 la compilaste tú y funciona. La v0.3.0 está escrita pero **yo no he
> podido compilarla** (no tengo los jars de Minecraft/Forge). Sí comprobé la sintaxis de los 53 archivos Java con
> el compilador y revisé a mano la API nueva. Si GitHub Actions marca error, copia el texto y lo corrijo.
> Detalles en `ESTADO.md`.

## Cómo obtener el .jar (igual que antes)
1. Sube **toda la carpeta** a tu repositorio de GitHub, sustituyendo los archivos (incluida la carpeta oculta `.github`).
2. Pestaña **Actions** → espera a que "Compilar mod" termine en verde.
3. Descarga el artefacto **prehistoric-survival-jar** → dentro está `prehistoricsurvival-0.3.0.jar`.
4. Cópialo a `mods` (quita antes el .jar de la v0.2.0). Tus mundos y criaturas de la v0.2.0 siguen funcionando.

## Guía de pruebas
| Qué probar | Cómo |
|---|---|
| **Libro-guía** | Al entrar al mundo recibes la **Guía del Explorador** (también en mundos ya creados, una sola vez). Clic derecho para abrirla. Si la pierdes: libro + cuero. |
| **Vista previa 3D del libro** | Índice → Criaturas → cualquier criatura: el modelo real se mueve con el ratón y cicla cría → adolescente → adulto → alfa. |
| **Dilophosaurus** | Creativo → huevo generador. En **supervivencia** acércate: despliega la gorguera y rechina; a media distancia **escupe veneno** (ralentización + ceguera, 10 s de recarga). |
| **Domesticar dilophosaurus** | Pescado crudo con clic derecho, **una vez cada 2,5 min** (te avisa si es pronto), 5 veces. |
| **Etapas de crecimiento** | Genera un T. rex bebé/adolescente: `/summon prehistoricsurvival:tyrannosaurus` y mira el libro; en la partida las crías salen de los huevos. Al cambiar de etapa hay una nube de polvo. |
| **Alfas** | Se ven con **"Alfa" dorado** en el nombre, púas/cuernos y brillo en la oscuridad. Para encontrarlos rápido baja `alphaChance` en `SpeciesData` de la especie. |
| **Nidos** | Fabrica nido de paja (2 palos+3 trigo), de barro (2 palos+3 barro) o montículo (2 troncos+6 barro). Pon un huevo encima: se hunde en la cavidad. |
| **Nidos salvajes** | ~30% de las hembras adultas que aparecen nuevas traen un nido con huevo. ¡La madre lo vigila! |
| **Incubación** | Solo en biomas cálidos o junto a hoguera/horno encendido; verás motas verdes, copos si hace frío y grietas. |
| **Cuidado parental** | La cría sigue a su madre; ataca a una cría y la madre acude. Roba un huevo y la madre te ataca. |
| **Sonidos** | Compara bebé / adolescente / adulto / alfa de cada especie; los T. rex tienen pisadas propias. |

Consejo: en **creativo** los depredadores no te atacan.

## Ampliar el libro en futuras versiones
Cada página es un archivo `guide/entries/*.json` (ver `guide/README.txt` dentro de los recursos y `tools/guide_content.py`).

## Regenerar recursos / comprobar el proyecto
`python3 tools/generate_assets.py` (texturas, modelos, sonidos, idiomas, datos y libro) y
`python3 tools/check_project.py` (comprobaciones estáticas). Requieren Pillow, numpy, scipy y ffmpeg.
