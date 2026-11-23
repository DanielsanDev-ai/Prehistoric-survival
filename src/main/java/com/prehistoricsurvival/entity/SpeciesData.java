package com.prehistoricsurvival.entity;

/**
 * Parametros de equilibrio de una especie. Aqui se ajusta casi todo sin tocar la logica.
 * Los tiempos estan en ticks (20 ticks = 1 segundo).
 */
public record SpeciesData(
        double baseHealth,
        double baseDamage,
        double baseSpeed,
        int babyTicks,          // duracion de la etapa de bebe
        int juvenileTicks,      // duracion de la etapa juvenil
        int feedingsToTame,     // comidas necesarias para domesticar (nivel 1, no alfa)
        int foodValue,          // hambre que recupera cada comida (0-100)
        int alphaChance,        // 1 de cada N apariciones es alfa (0 = nunca)
        double alphaStatMultiplier,
        float alphaScale,
        float femaleScale,
        double alertRadius,     // radio en el que avisa a su especie cuando le atacan
        int gestationTicks,     // desde el apareamiento hasta que la hembra pone el huevo
        int incubationTicks,    // incubacion del huevo en condiciones normales
        int breedCooldownTicks, // descanso tras aparearse
        boolean carnivore,
        double warnRadius,      // distancia a la que advierte a un jugador (0 = no advierte)
        int warnTicks,          // ticks de advertencia antes de atacar si el jugador no se va
        float modelScale,       // escala visual del modelo (1.0 = tamano base del modelo)
        float babyScale         // escala al final de la etapa de bebe (relativa al adulto)
) {
    public int adultTicks() {
        return babyTicks + juvenileTicks;
    }
}
