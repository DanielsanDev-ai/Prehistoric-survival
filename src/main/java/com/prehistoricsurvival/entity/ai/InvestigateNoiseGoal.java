package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/** Acude al lugar de un ruido de combate cercano a ver que pasa (T. rex). */
public class InvestigateNoiseGoal extends Goal {
    private final PrehistoricEntity mob;
    private int giveUp;

    public InvestigateNoiseGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.investigatesNoise() && mob.getNoisePos() != null
                && mob.getAction() == EntityAction.NONE && mob.getTarget() == null;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getNoisePos() != null && giveUp > 0 && !mob.getNavigation().isDone() && mob.getTarget() == null;
    }

    @Override
    public void start() {
        giveUp = 400;
        Vec3 p = mob.getNoisePos();
        mob.getNavigation().moveTo(p.x, p.y, p.z, 1.0);
    }

    @Override
    public void tick() {
        giveUp--;
    }

    @Override
    public void stop() {
        mob.clearNoise();
    }
}
