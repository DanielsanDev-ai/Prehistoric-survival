package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Duerme de noche si esta tranquilo. Se despierta si se hace de dia, si alguien le ataca
 * o si un jugador se acerca demasiado (a menos de 5 bloques).
 */
public class SleepAtNightGoal extends Goal {
    private static final double WAKE_DISTANCE = 5.0;

    private final PrehistoricEntity mob;

    public SleepAtNightGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.level().isDay() || mob.isTame() || !mob.onGround() || mob.isInWaterOrBubble()) return false;
        if (mob.getTarget() != null || mob.getLastHurtByMob() != null) return false;
        if (mob.getRandom().nextInt(100) != 0) return false; // se duerme poco a poco, no todos a la vez
        return calm();
    }

    @Override
    public boolean canContinueToUse() {
        return !mob.level().isDay() && !mob.isInWaterOrBubble()
                && mob.getLastHurtByMob() == null && calm();
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        mob.setAction(EntityAction.SLEEPING);
    }

    @Override
    public void stop() {
        if (mob.getAction() == EntityAction.SLEEPING) mob.setAction(EntityAction.NONE);
    }

    private boolean calm() {
        return mob.level().getNearestPlayer(mob, WAKE_DISTANCE) == null;
    }
}
