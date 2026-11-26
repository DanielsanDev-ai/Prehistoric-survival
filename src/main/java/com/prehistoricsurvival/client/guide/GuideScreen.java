package com.prehistoricsurvival.client.guide;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.GrowthStage;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Libro del explorador paleontologo: portada, indice por secciones y paginas de cada entrada con
 * vista previa 3D real de la criatura (con su modelo, textura y animaciones del juego).
 */
public class GuideScreen extends Screen {
    private static final ResourceLocation COVER = new ResourceLocation(PrehistoricSurvival.MOD_ID, "textures/gui/guide_cover.png");
    private static final ResourceLocation PAGES = new ResourceLocation(PrehistoricSurvival.MOD_ID, "textures/gui/guide_pages.png");

    private static final int BOOK_W = 292, BOOK_H = 180, COVER_W = 146;
    private static final int PAGE_W = 118, LINE_H = 10, TEXT_LINES = 14;
    private static final int INK = 0x3B2A1A, INK_SOFT = 0x6B5238, INK_HEADER = 0x7A2E12, GOLD = 0xE8C766;

    private enum View { COVER, INDEX, ENTRY }

    private final GuideData data;
    private View view = View.COVER;
    private int sectionIndex = 0;
    private GuideData.Entry entry;
    private int textPage = 0;
    private List<List<FormattedCharSequence>> pages = new ArrayList<>();
    private Entity previewEntity;
    private final List<String> stageList = new ArrayList<>();
    private int stageIdx = -1;
    private int animTick;

    private int left, top;

    public GuideScreen(GuideData data) {
        super(Component.translatable("prehistoricsurvival.guide.title"));
        this.data = data;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        left = (width - BOOK_W) / 2;
        top = (height - BOOK_H) / 2;
    }

    @Override
    public void tick() {
        animTick++;
    }

    // ------------------------------------------------------------------ dibujo

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g);
        switch (view) {
            case COVER -> renderCover(g);
            case INDEX -> renderIndex(g, mouseX, mouseY);
            case ENTRY -> renderEntry(g, mouseX, mouseY);
        }
        super.render(g, mouseX, mouseY, partial);
    }

    private void renderCover(GuiGraphics g) {
        int x = (width - COVER_W) / 2;
        g.blit(COVER, x, top, 0, 0, COVER_W, BOOK_H, COVER_W, BOOK_H);
        g.pose().pushPose();
        g.pose().translate(width / 2f, top + 26, 0);
        g.pose().scale(1.5f, 1.5f, 1f);
        g.drawCenteredString(font, Component.translatable("prehistoricsurvival.guide.cover1"), 0, 0, GOLD);
        g.drawCenteredString(font, Component.translatable("prehistoricsurvival.guide.cover2"), 0, 11, GOLD);
        g.pose().popPose();
        g.drawCenteredString(font, Component.translatable("prehistoricsurvival.guide.subtitle"), width / 2, top + 148, 0xD9B96A);
        g.drawCenteredString(font, Component.literal("v" + GuideData.modVersion()), width / 2, top + 160, 0xA8895A);
        if ((animTick / 20) % 2 == 0) {
            g.drawCenteredString(font, Component.translatable("prehistoricsurvival.guide.open"), width / 2, top + BOOK_H + 8, 0xFFFFFF);
        }
    }

    private void drawPages(GuiGraphics g) {
        g.blit(PAGES, left, top, 0, 0, BOOK_W, BOOK_H, BOOK_W, BOOK_H);
    }

    private int leftX() { return left + 16; }
    private int rightX() { return left + 160; }
    private int textTop() { return top + 18; }

    private void renderIndex(GuiGraphics g, int mouseX, int mouseY) {
        drawPages(g);
        // pagina izquierda: secciones
        g.drawString(font, Component.translatable("prehistoricsurvival.guide.index"), leftX(), textTop(), INK_HEADER, false);
        int y = textTop() + 16;
        for (int i = 0; i < data.sections.size(); i++) {
            boolean hover = inside(mouseX, mouseY, leftX() - 2, y - 2, PAGE_W + 4, 14);
            if (hover || i == sectionIndex) g.fill(leftX() - 2, y - 2, leftX() + PAGE_W + 2, y + 11, 0x33A07A3C);
            g.drawString(font, GuideData.tr(data.sections.get(i).title), leftX() + 4, y, i == sectionIndex ? INK_HEADER : INK, false);
            y += 15;
        }
        // pagina derecha: entradas de la seccion elegida
        if (data.sections.isEmpty()) return;
        GuideData.Section section = data.sections.get(sectionIndex);
        g.drawString(font, GuideData.tr(section.title), rightX(), textTop(), INK_HEADER, false);
        y = textTop() + 16;
        String version = GuideData.modVersion();
        for (GuideData.Entry e : data.bySection.get(section.id)) {
            boolean hover = inside(mouseX, mouseY, rightX() - 2, y - 2, PAGE_W + 4, 14);
            if (hover) g.fill(rightX() - 2, y - 2, rightX() + PAGE_W + 2, y + 11, 0x33A07A3C);
            g.drawString(font, GuideData.tr(e.title), rightX() + 4, y, INK, false);
            if (!version.isEmpty() && version.equals(e.since)) {
                g.drawString(font, Component.translatable("prehistoricsurvival.guide.new"), rightX() + PAGE_W - 22, y, 0xB03A2E, false);
            }
            y += 14;
        }
    }

    private void renderEntry(GuiGraphics g, int mouseX, int mouseY) {
        drawPages(g);
        // ---- pagina izquierda: titulo, vista previa y datos
        int y = textTop();
        g.drawString(font, GuideData.tr(entry.title), leftX(), y, INK_HEADER, false);
        y += 11;
        if (!entry.since.isEmpty()) {
            g.drawString(font, Component.translatable("prehistoricsurvival.guide.added", entry.since), leftX(), y, INK_SOFT, false);
        }
        y += 12;
        if (previewEntity != null) {
            int bx = leftX(), by = y, bw = PAGE_W, bh = 62;
            g.fill(bx, by, bx + bw, by + bh, 0x40A8C8A0);
            g.renderOutline(bx, by, bw, bh, 0xFF7A5A32);
            renderPreview(g, bx, by, bw, bh, mouseX, mouseY);
            y += bh + 5;
        }
        for (JsonElement fact : entry.facts) {
            for (FormattedCharSequence line : font.split(Component.literal(GuideData.tr(fact)), PAGE_W)) {
                if (y > top + BOOK_H - 24) break;
                g.drawString(font, line, leftX(), y, INK, false);
                y += 9;
            }
        }
        // ---- pagina derecha: texto paginado
        if (!pages.isEmpty()) {
            int ty = textTop();
            for (FormattedCharSequence line : pages.get(textPage)) {
                g.drawString(font, line, rightX(), ty, INK, false);
                ty += LINE_H;
            }
            g.drawString(font, Component.translatable("prehistoricsurvival.guide.page", textPage + 1, pages.size()),
                    rightX() + PAGE_W - 40, top + BOOK_H - 18, INK_SOFT, false);
        }
        // ---- navegacion
        drawNav(g, mouseX, mouseY, leftX(), top + BOOK_H - 18, 44, Component.translatable("prehistoricsurvival.guide.back"));
        if (textPage > 0) drawNav(g, mouseX, mouseY, rightX(), top + BOOK_H - 18, 14, Component.literal("<"));
        if (textPage < pages.size() - 1) drawNav(g, mouseX, mouseY, rightX() + 16, top + BOOK_H - 18, 14, Component.literal(">"));
    }

    private void drawNav(GuiGraphics g, int mx, int my, int x, int y, int w, Component label) {
        boolean hover = inside(mx, my, x, y - 2, w, 13);
        g.fill(x, y - 2, x + w, y + 11, hover ? 0x66A07A3C : 0x33A07A3C);
        g.renderOutline(x, y - 2, w, 13, 0xFF7A5A32);
        g.drawCenteredString(font, label, x + w / 2, y, INK);
    }

    private void renderPreview(GuiGraphics g, int bx, int by, int bw, int bh, int mouseX, int mouseY) {
        if (!(previewEntity instanceof LivingEntity living)) return;
        living.tickCount = animTick;
        String caption = null;
        if (living instanceof PrehistoricEntity dino && stageList.size() > 1) {
            int idx = (animTick / 100) % stageList.size();
            if (idx != stageIdx) {
                stageIdx = idx;
                applyStage(dino, stageList.get(idx));
            }
            caption = stageList.get(stageIdx);
        }
        // La escala se calcula con la altura del ADULTO para que las etapas se vean a su tamano relativo real.
        float height = Math.max(0.4f, living.getType().getHeight());
        float scale = Math.min(60f, Math.min((bh - 14) / height, bw * 0.8f / (height * 2.2f)));
        if (entry.preview != null && entry.preview.has("scale")) scale = entry.preview.get("scale").getAsFloat();
        int cx = bx + bw / 2;
        int feet = by + bh - 8;
        g.enableScissor(bx + 1, by + 1, bx + bw - 1, by + bh - 1);
        InventoryScreen.renderEntityInInventoryFollowsMouse(g, cx, feet, (int) scale,
                (float) cx - mouseX, (float) (feet - bh / 2) - mouseY, living);
        g.disableScissor();
        if (caption != null) {
            String key = switch (caption) {
                case "baby" -> "prehistoricsurvival.stage.baby";
                case "teen" -> "prehistoricsurvival.stage.teen";
                case "alpha" -> "prehistoricsurvival.label.alpha";
                default -> "prehistoricsurvival.stage.adult";
            };
            g.drawString(font, Component.translatable(key), bx + 4, by + bh - 11, 0x6B5238, false);
        }
    }

    private static void applyStage(PrehistoricEntity dino, String stage) {
        switch (stage) {
            case "baby" -> dino.previewStage(GrowthStage.BABY, false);
            case "teen" -> dino.previewStage(GrowthStage.JUVENILE, false);
            case "alpha" -> dino.previewStage(GrowthStage.ADULT, true);
            default -> dino.previewStage(GrowthStage.ADULT, false);
        }
    }

    // ------------------------------------------------------------------ interaccion

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private void click() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        switch (view) {
            case COVER -> {
                view = View.INDEX;
                click();
                return true;
            }
            case INDEX -> {
                int y = textTop() + 16;
                for (int i = 0; i < data.sections.size(); i++) {
                    if (inside(mx, my, leftX() - 2, y - 2, PAGE_W + 4, 14)) {
                        sectionIndex = i;
                        click();
                        return true;
                    }
                    y += 15;
                }
                if (!data.sections.isEmpty()) {
                    y = textTop() + 16;
                    for (GuideData.Entry e : data.bySection.get(data.sections.get(sectionIndex).id)) {
                        if (inside(mx, my, rightX() - 2, y - 2, PAGE_W + 4, 14)) {
                            openEntry(e);
                            click();
                            return true;
                        }
                        y += 14;
                    }
                }
            }
            case ENTRY -> {
                int ny = top + BOOK_H - 18;
                if (inside(mx, my, leftX(), ny - 2, 44, 13)) {
                    view = View.INDEX;
                    previewEntity = null;
                    click();
                    return true;
                }
                if (textPage > 0 && inside(mx, my, rightX(), ny - 2, 14, 13)) {
                    textPage--;
                    click();
                    return true;
                }
                if (textPage < pages.size() - 1 && inside(mx, my, rightX() + 16, ny - 2, 14, 13)) {
                    textPage++;
                    click();
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (view == View.ENTRY) {
            if (key == 262 && textPage < pages.size() - 1) { textPage++; click(); return true; }   // flecha derecha
            if (key == 263 && textPage > 0) { textPage--; click(); return true; }                  // flecha izquierda
        }
        return super.keyPressed(key, scan, mods);
    }

    // ------------------------------------------------------------------ entradas

    private void openEntry(GuideData.Entry e) {
        this.entry = e;
        this.view = View.ENTRY;
        this.textPage = 0;
        this.pages = paginate(e);
        this.previewEntity = createPreview(e);
    }

    private List<List<FormattedCharSequence>> paginate(GuideData.Entry e) {
        List<FormattedCharSequence> all = new ArrayList<>();
        for (JsonElement paragraph : e.text) {
            all.addAll(font.split(Component.literal(GuideData.tr(paragraph)), PAGE_W));
            all.add(FormattedCharSequence.EMPTY);
        }
        List<List<FormattedCharSequence>> result = new ArrayList<>();
        for (int i = 0; i < all.size(); i += TEXT_LINES) {
            result.add(new ArrayList<>(all.subList(i, Math.min(all.size(), i + TEXT_LINES))));
        }
        if (result.isEmpty()) result.add(new ArrayList<>());
        return result;
    }

    private Entity createPreview(GuideData.Entry e) {
        JsonObject p = e.preview;
        Minecraft mc = Minecraft.getInstance();
        if (p == null || mc.level == null || !p.has("entity")) return null;
        stageList.clear();
        stageIdx = -1;
        if (p.has("stages")) p.getAsJsonArray("stages").forEach(el -> stageList.add(el.getAsString()));
        Entity entity = EntityType.byString(p.get("entity").getAsString()).map(t -> t.create(mc.level)).orElse(null);
        if (entity instanceof PrehistoricEntity dino) {
            String stage = p.has("stage") ? p.get("stage").getAsString() : "adult";
            GrowthStage gs = switch (stage) {
                case "baby" -> GrowthStage.BABY;
                case "teen" -> GrowthStage.JUVENILE;
                default -> GrowthStage.ADULT;
            };
            dino.previewStage(gs, p.has("alpha") && p.get("alpha").getAsBoolean());
        }
        return entity;
    }
}
