package com.prehistoricsurvival.client;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.guide.GuideBookPages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Interfaz de enciclopedia con indice por paginas y navegacion simple. */
public class GuideBookScreen extends Screen {
    private static final ResourceLocation COVER = new ResourceLocation(
            PrehistoricSurvival.MOD_ID, "textures/gui/field_guide_cover.png");
    private int pageIndex;

    public GuideBookScreen() {
        super(Component.literal("Prehistoric Survival - Enciclopedia"));
    }

    @Override
    protected void init() {
        int center = width / 2;
        addRenderableWidget(Button.builder(Component.literal("< Anterior"), b -> changePage(-1))
                .bounds(center - 118, height - 36, 92, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Siguiente >"), b -> changePage(1))
                .bounds(center + 26, height - 36, 92, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cerrar"), b -> onClose())
                .bounds(center - 34, height - 36, 68, 20).build());
    }

    private void changePage(int delta) {
        int count = GuideBookPages.all().size();
        if (count == 0) return;
        pageIndex = Math.floorMod(pageIndex + delta, count);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        int left = width / 2 - 148;
        int top = height / 2 - 112;
        int right = width / 2 + 148;
        int bottom = height / 2 + 100;

        g.fill(left - 5, top - 5, right + 5, bottom + 5, 0xFF17120D);
        g.fill(left, top, right, bottom, 0xFFE4D4AE);
        g.fill(left + 7, top + 7, right - 7, bottom - 7, 0xFFBDA77E);
        g.fill(left + 10, top + 10, right - 10, bottom - 10, 0xFFE8DABB);
        g.fill(width / 2 - 1, top + 5, width / 2 + 1, bottom - 5, 0xFF876B45);

        var pages = GuideBookPages.all();
        if (!pages.isEmpty()) {
            var page = pages.get(pageIndex);
            if (pageIndex == 0) {
                g.blit(COVER, width / 2 - 120, top + 18, 0, 0, 240, 164, 256, 176);
                g.drawCenteredString(font, Component.literal("PREHISTORIC SURVIVAL"),
                        width / 2, top + 75, 0xFFFFE4A0);
                g.drawCenteredString(font, Component.literal("CUADERNO DEL EXPLORADOR"),
                        width / 2, top + 112, 0xFFFFE4A0);
                g.drawCenteredString(font, Component.literal("ENCICLOPEDIA DE CAMPO · 0.3.0"),
                        width / 2, top + 145, 0xFFE8D4A8);
            } else {
                g.drawCenteredString(font, Component.literal(page.title()), width / 2, top + 20, 0xFF392615);
                g.fill(left + 26, top + 39, right - 26, top + 40, 0xFF8A704C);

                int y = top + 53;
                for (String line : page.lines()) {
                    if (y > bottom - 20) break;
                    for (var wrapped : font.split(Component.literal(line), right - left - 44)) {
                        if (y > bottom - 20) break;
                        g.drawString(font, wrapped, left + 22, y, 0xFF332719, false);
                        y += 12;
                    }
                    y += 3;
                }
            }
            g.drawCenteredString(font, Component.literal((pageIndex + 1) + " / " + pages.size()),
                    width / 2, bottom - 16, 0xFF675236);
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
