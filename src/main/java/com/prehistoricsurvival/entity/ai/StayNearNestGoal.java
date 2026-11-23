package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import com.prehistoricsurvival.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

/** La madre se queda cerca de su huevo mientras exista. Si el huevo desaparece, olvida el nido. */
public class StayNearNestGoal extends Goal {
    private final PrehistoricEntity mob;

    public StayNearNestGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!mob.hasNest() || mob.getAction() != EntityAction.NONE || mob.getTarget() != null) return false;
        if (mob.tickCount % 20 != 0) return false;
        BlockPos nest = mob.getNestPos();
        if (!mob.level().getBlockState(nest).is(ModBlocks.DINO_EGG.get())) {
            mob.clearNest();
            return false;
        }
        return mob.distanceToSqr(nest.getX() + 0.5, nest.getY(), nest.getZ() + 0.5) > 49.0;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.hasNest() && !mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        BlockPos nest = mob.getNestPos();
        mob.getNavigation().moveTo(nest.getX() + 0.5, nest.getY(), nest.getZ() + 0.5, 1.0);
    }
}
