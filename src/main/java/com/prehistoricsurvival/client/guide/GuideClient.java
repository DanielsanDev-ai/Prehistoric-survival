package com.prehistoricsurvival.client.guide;

import net.minecraft.client.Minecraft;

/** Punto de entrada cliente del libro (se invoca con DistExecutor desde el objeto). */
public final class GuideClient {
    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new GuideScreen(GuideData.load(mc.getResourceManager())));
    }

    private GuideClient() {}
}
