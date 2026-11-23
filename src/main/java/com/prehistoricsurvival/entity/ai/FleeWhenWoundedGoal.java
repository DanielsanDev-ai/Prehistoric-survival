package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

/** Un depredador herido de gravedad (menos del 25%) abandona la lucha y huye. */
public class FleeWhenWoundedGoal extends net.minecraft.world.entity.ai.goal.Goal {
    private final PrehistoricEntity mob;

    public FleeWhenWoundedGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !mob.isTame() && mob.getHealth() < mob.getMaxHealth() * 0.25f && mob.getLastHurtByMob() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !mob.getNavigation().isDone() && mob.getHealth() < mob.getMaxHealth() * 0.4f;
    }

    @Override
    public void start() {
        LivingEntity attacker = mob.getLastHurtByMob();
        mob.setTarget(null);
        Vec3 away = attacker == null ? null : DefaultRandomPos.getPosAway(mob, 18, 7, attacker.position());
        if (away != null) mob.getNavigation().moveTo(away.x, away.y, away.z, 1.5);
    }
}
