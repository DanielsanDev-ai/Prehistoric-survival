package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.DilophosaurusEntity;
import com.prehistoricsurvival.entity.EntityAction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Habilidad del dilophosaurus: a media distancia y con linea de vision, se detiene, despliega la gorguera
 * (animacion SPITTING, ~1,2 s) y escupe veneno. Solo se usa con la habilidad lista (tiempo de reutilizacion).
 */
public class VenomSpitGoal extends Goal {
    private static final int WINDUP_TICKS = 24;

    private final DilophosaurusEntity mob;
    private int windup;

    public VenomSpitGoal(DilophosaurusEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        if (t == null || !t.isAlive() || !mob.canSpit()) return false;
        double d = mob.distanceToSqr(t);
        return d > 16.0 && d < 18.0 * 18.0 && mob.getSensing().hasLineOfSight(t);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity t = mob.getTarget();
        return windup > 0 && t != null && t.isAlive();
    }

    @Override
    public void start() {
        windup = WINDUP_TICKS;
        mob.setAction(EntityAction.SPITTING);
        mob.getNavigation().stop();
        mob.playFrillSound();
    }

    @Override
    public void tick() {
        LivingEntity t = mob.getTarget();
        mob.getLookControl().setLookAt(t, 40f, 40f);
        mob.getNavigation().stop();
        if (--windup == 0) mob.spitAt(t);
    }

    @Override
    public void stop() {
        if (mob.getAction() == EntityAction.SPITTING) mob.setAction(EntityAction.NONE);
        windup = 0;
    }
}
