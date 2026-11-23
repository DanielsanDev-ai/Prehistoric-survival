package com.prehistoricsurvival.entity.ai;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Comportamiento de bandada: si se aleja demasiado de los de su especie, vuelve con ellos.
 * Prefiere seguir al alfa; las crias solo siguen a adultos.
 */
public class StayNearHerdGoal extends Goal {
    private final PrehistoricEntity mob;
    private final double speed;
    private final double comfortDistance;
    private PrehistoricEntity leader;
    private int recalc;

    public StayNearHerdGoal(PrehistoricEntity mob, double speed, double comfortDistance) {
        this.mob = mob;
        this.speed = speed;
        this.comfortDistance = comfortDistance;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.isTame() || mob.getAction() != EntityAction.NONE) return false;
        if (mob.getRandom().nextInt(40) != 0) return false;

        List<PrehistoricEntity> herd = mob.level().getEntitiesOfClass(PrehistoricEntity.class,
                mob.getBoundingBox().inflate(24.0, 8.0, 24.0),
                e -> e != mob && e.getType() == mob.getType() && e.isAlive() && !e.isTame()
                        && (!mob.isYoung() || e.isAdult()));
        if (herd.isEmpty()) return false;

        leader = herd.stream()
                .filter(PrehistoricEntity::isAlpha)
                .min(Comparator.comparingDouble(mob::distanceToSqr))
                .orElseGet(() -> herd.stream()
                        .min(Comparator.comparingDouble(mob::distanceToSqr))
                        .orElse(null));
        return leader != null && mob.distanceToSqr(leader) > comfortDistance * comfortDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return leader != null && leader.isAlive() && mob.getAction() == EntityAction.NONE
                && !mob.getNavigation().isDone()
                && mob.distanceToSqr(leader) > (comfortDistance * 0.5) * (comfortDistance * 0.5);
    }

    @Override
    public void start() {
        recalc = 10;
        mob.getNavigation().moveTo(leader, speed);
    }

    @Override
    public void tick() {
        if (--recalc <= 0) {
            recalc = 10;
            mob.getNavigation().moveTo(leader, speed);
        }
    }

    @Override
    public void stop() {
        leader = null;
        mob.getNavigation().stop();
    }
}
