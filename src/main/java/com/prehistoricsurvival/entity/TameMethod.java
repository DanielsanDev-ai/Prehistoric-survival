package com.prehistoricsurvival.entity;

/** Cada especie se domestica de una forma distinta. */
public enum TameMethod {
    /** Ganarse la confianza dandole su comida favorita (dodo). */
    FEED,
    /** Dar carne agachado y sin asustarlo; si te ve de pie desconfia (velociraptor). */
    STEALTH,
    /** Sedarlo con carne sedante y alimentarlo mientras duerme (tyrannosaurus). */
    SEDATE,
    /** Confianza y tiempo: pescado, una vez cada pocos minutos, sin atacarlo (dilophosaurus). */
    TRUST
}
