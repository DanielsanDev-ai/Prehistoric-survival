package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/** Mientras esta sedada la criatura no se mueve, no mira y no ataca. Tiene la maxima prioridad. */
public class SedatedGoal extends Goal {
    private final PrehistoricEntity mob;

    public SedatedGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return mob.isSedated();
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isSedated();
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        mob.getNavigation().stop();
        mob.setTarget(null);
        if (mob.getAction() != EntityAction.SLEEPING) mob.setAction(EntityAction.SLEEPING);
    }
}
