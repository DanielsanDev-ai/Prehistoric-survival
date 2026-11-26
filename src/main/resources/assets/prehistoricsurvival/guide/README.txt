GUIA DEL EXPLORADOR - COMO AÑADIR PAGINAS EN FUTURAS VERSIONES
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
