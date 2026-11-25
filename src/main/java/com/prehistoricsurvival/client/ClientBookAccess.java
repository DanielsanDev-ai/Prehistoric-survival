package com.prehistoricsurvival.client;

import net.minecraft.client.Minecraft;

/** Punto de entrada seguro para abrir la interfaz solo en el cliente. */
public final class ClientBookAccess {
    public static void open() {
        Minecraft.getInstance().setScreen(new GuideBookScreen());
    }

    private ClientBookAccess() {}
}
