package com.prehistoricsurvival.entity.guide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro central de paginas de la enciclopedia.
 * Para ampliar el libro en futuras versiones basta con registrar nuevas paginas aqui.
 */
public final class GuideBookPages {
    public record Page(String title, List<String> lines) {
        public Page(String title, String... lines) {
            this(title, List.of(lines));
        }
    }

    private static final List<Page> PAGES = new ArrayList<>();

    static {
        register(new Page("Prehistoric Survival", "CUADERNO DEL EXPLORADOR",
                "Un registro de campo para estudiar, criar y domesticar las criaturas prehistoricas.",
                "Version del contenido: 0.3.0", "Usa las flechas para recorrer las secciones."));
        register(new Page("Indice", "1. Dodo", "2. Velociraptor", "3. Tyrannosaurus rex",
                "4. Dilophosaurus", "5. Nidos, huevos e incubacion",
                "6. Crecimiento", "7. Criaturas Alfa", "8. Consejos de campo"));
        register(new Page("Dodo", "Tipo: herbivoro tranquilo y gregario.",
                "Habitat: llanuras y zonas abiertas.",
                "Domesticacion: ofrece semillas o bayas varias veces.",
                "Alimentacion: semillas y bayas dulces.",
                "Comportamiento: huye de los grandes depredadores y se mantiene cerca de su bandada.",
                "Recurso: pluma de dodo."));
        register(new Page("Velociraptor", "Tipo: depredador carnivoro de manada.",
                "Habitat: biomas calidos y abiertos.",
                "Domesticacion: acercate agachado y ofrece carne cruda.",
                "Alimentacion: carne cruda y presas pequeñas.",
                "Comportamiento: acecha, carga y puede coordinarse con otros raptores.",
                "Recurso: garra de raptor."));
        register(new Page("Tyrannosaurus rex", "Tipo: gran depredador territorial.",
                "Habitat: regiones adecuadas para grandes depredadores.",
                "Domesticacion: requiere el metodo de sedacion ya existente y alimento cocinado.",
                "Alimentacion: carne y presas grandes.",
                "Comportamiento: advierte antes de atacar, investiga ruidos y caza cuando tiene hambre.",
                "Recurso: diente de T. rex."));
        register(new Page("Dilophosaurus", "Tipo: depredador carnivoro de tamaño mediano.",
                "Rasgos: dos crestas craneales y gola extensible alrededor del cuello.",
                "Domesticacion: acercate agachado y ofrece carne cruda.",
                "Alimentacion: carne cruda y presas pequeñas.",
                "Comportamiento: caza criaturas pequeñas, advierte cuando se siente amenazado y protege su espacio.",
                "Reproduccion: utiliza su propio huevo e incubacion."));
        register(new Page("Nidos y huevos", "Coloca un nido en una superficie estable.",
                "Las hembras adultas pueden buscar un nido cuando termina la gestacion.",
                "Los huevos conservan especie, nivel, variante, sexo, rasgo alfa y progreso al recogerlos.",
                "Incubacion: necesita un bioma calido o una fuente de calor cercana.",
                "Un nido debajo del huevo acelera la incubacion; el calor cercano tambien ayuda.",
                "Al completar el progreso, nace una cria con los genes guardados."));
        register(new Page("Crecimiento", "Cada dinosaurio puede pasar por tres etapas:",
                "BEBE: pequeño, con estadisticas reducidas.",
                "ADOLESCENTE: tamaño y capacidades intermedias.",
                "ADULTO: alcanza el tamaño y estadisticas completas.",
                "El progreso avanza con el tiempo y se guarda en los datos de la criatura.",
                "La falta de alimento detiene el crecimiento hasta que vuelva a comer."));
        register(new Page("Criaturas Alfa", "Los ejemplares Alfa son poco frecuentes.",
                "Se identifican por el prefijo Alfa y una apariencia especial.",
                "Tienen mas salud, daño y tamaño que un ejemplar normal.",
                "Su domesticacion requiere mas alimento; sus recursos especiales se conservan al guardarlos."));
        register(new Page("Consejos de campo", "Observa el comportamiento antes de acercarte.",
                "Alimenta a tus criaturas para mantener su hambre y salud.",
                "Las mascotas siguen a su dueño y pueden sentarse con clic derecho en mano vacia.",
                "Los dinosaurios salvajes pueden defender su nido y alertar a otros de su especie.",
                "Haz copias de seguridad de tus mundos antes de probar versiones nuevas."));
    }

    public static void register(Page page) {
        if (page != null && page.title() != null && !page.title().isBlank()) PAGES.add(page);
    }

    public static List<Page> all() {
        return Collections.unmodifiableList(PAGES);
    }

    private GuideBookPages() {}
}
