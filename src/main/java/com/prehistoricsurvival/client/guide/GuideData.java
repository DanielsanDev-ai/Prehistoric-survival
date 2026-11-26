package com.prehistoricsurvival.client.guide;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prehistoricsurvival.PrehistoricSurvival;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModList;

/**
 * Contenido del libro-guia, leido de los recursos del mod:
 *   assets/prehistoricsurvival/guide/sections.json        -> secciones del indice
 *   assets/prehistoricsurvival/guide/entries/*.json       -> una entrada (pagina) por archivo
 * Para ampliar el libro en futuras versiones basta con anadir un JSON en entries/ (ver guide/README.txt).
 * Los textos son objetos {"es": "...", "en": "..."}; se usa el idioma del juego y, si falta, el espanol.
 */
public final class GuideData {

    public static final class Section {
        public final String id;
        public final int order;
        public final JsonObject title;

        Section(String id, int order, JsonObject title) {
            this.id = id;
            this.order = order;
            this.title = title;
        }
    }

    public static final class Entry {
        public final String id, section, since;
        public final int order;
        public final JsonObject title;
        public final JsonArray facts, text;
        public final JsonObject preview;   // {"entity": "...", "stage": "baby|teen|adult", "alpha": bool, "scale": n}

        Entry(JsonObject o) {
            this.id = str(o, "id");
            this.section = str(o, "section");
            this.since = str(o, "since");
            this.order = o.has("order") ? o.get("order").getAsInt() : 100;
            this.title = o.has("title") ? o.getAsJsonObject("title") : new JsonObject();
            this.facts = o.has("facts") ? o.getAsJsonArray("facts") : new JsonArray();
            this.text = o.has("text") ? o.getAsJsonArray("text") : new JsonArray();
            this.preview = o.has("preview") ? o.getAsJsonObject("preview") : null;
        }
    }

    public final List<Section> sections = new ArrayList<>();
    public final Map<String, List<Entry>> bySection = new LinkedHashMap<>();

    private static String str(JsonObject o, String key) {
        return o.has(key) ? o.get(key).getAsString() : "";
    }

    /** Idioma de los textos del libro: el del juego si esta disponible, si no el espanol. */
    public static String lang() {
        String code = Minecraft.getInstance().options.languageCode;
        return code != null && code.toLowerCase().startsWith("es") ? "es" : "en";
    }

    public static String tr(JsonElement element) {
        if (element == null || !element.isJsonObject()) return element != null && element.isJsonPrimitive() ? element.getAsString() : "";
        JsonObject o = element.getAsJsonObject();
        String lang = lang();
        if (o.has(lang)) return o.get(lang).getAsString();
        if (o.has("es")) return o.get("es").getAsString();
        return o.entrySet().isEmpty() ? "" : o.entrySet().iterator().next().getValue().getAsString();
    }

    public static String modVersion() {
        return ModList.get().getModContainerById(PrehistoricSurvival.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString()).orElse("");
    }

    public static GuideData load(ResourceManager manager) {
        GuideData data = new GuideData();
        ResourceLocation sectionsFile = new ResourceLocation(PrehistoricSurvival.MOD_ID, "guide/sections.json");
        manager.getResource(sectionsFile).ifPresent(res -> {
            try (Reader reader = res.openAsReader()) {
                JsonArray arr = JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("sections");
                for (JsonElement e : arr) {
                    JsonObject o = e.getAsJsonObject();
                    data.sections.add(new Section(str(o, "id"), o.has("order") ? o.get("order").getAsInt() : 100,
                            o.getAsJsonObject("title")));
                }
            } catch (Exception ex) {
                PrehistoricSurvival.LOGGER.error("No se pudo leer guide/sections.json", ex);
            }
        });
        data.sections.sort(Comparator.comparingInt(s -> s.order));
        for (Section s : data.sections) data.bySection.put(s.id, new ArrayList<>());

        Map<ResourceLocation, Resource> found = manager.listResources("guide/entries", rl -> rl.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> e : found.entrySet()) {
            try (Reader reader = e.getValue().openAsReader()) {
                Entry entry = new Entry(JsonParser.parseReader(reader).getAsJsonObject());
                data.bySection.computeIfAbsent(entry.section, k -> new ArrayList<>()).add(entry);
            } catch (Exception ex) {
                PrehistoricSurvival.LOGGER.error("Entrada de guia invalida: {}", e.getKey(), ex);
            }
        }
        for (List<Entry> list : data.bySection.values()) list.sort(Comparator.comparingInt((Entry x) -> x.order));
        // quitar secciones vacias
        data.sections.removeIf(s -> data.bySection.getOrDefault(s.id, List.of()).isEmpty());
        return data;
    }
}
