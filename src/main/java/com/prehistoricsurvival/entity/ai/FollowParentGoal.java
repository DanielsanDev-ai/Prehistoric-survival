package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/** Las crias y adolescentes siguen a quien puso su huevo (si sigue cerca) y se mantienen a su lado. */
public class FollowParentGoal extends Goal {
    private final PrehistoricEntity mob;
    private final double speed;
    private PrehistoricEntity parent;
    private int recalc;

    public FollowParentGoal(PrehistoricEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!mob.isYoung() || mob.getParentUUID() == null || mob.getAction() != EntityAction.NONE) return false;
        if (mob.getRandom().nextInt(30) != 0) return false;
        parent = mob.getParentEntity();
        if (parent == null) return false;
        double d = mob.distanceToSqr(parent);
        return d > 36.0 && d < 48.0 * 48.0;
    }

    @Override
    public boolean canContinueToUse() {
        return parent != null && parent.isAlive() && mob.getAction() == EntityAction.NONE
                && mob.distanceToSqr(parent) > 9.0 && !mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        recalc = 10;
        mob.getNavigation().moveTo(parent, speed);
    }

    @Override
    public void tick() {
        if (--recalc <= 0) {
            recalc = 10;
            mob.getNavigation().moveTo(parent, speed);
        }
    }

    @Override
    public void stop() {
        parent = null;
    }
}
