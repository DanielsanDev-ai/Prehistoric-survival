package com.prehistoricsurvival.entity;

/** Accion visible de la criatura. Se sincroniza con el cliente para animar el modelo. */
public enum EntityAction {
    NONE, EATING, SLEEPING, LAYING, THREAT, SPITTING;

    public static EntityAction byId(int id) {
        EntityAction[] all = values();
        return all[Math.max(0, Math.min(id, all.length - 1))];
    }
}
