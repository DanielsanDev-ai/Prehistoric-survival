package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import com.prehistoricsurvival.block.NestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * La hembra gestante busca un nido (o, si no hay, un sitio llano y seco), se agacha unos segundos
 * y pone un huevo. El huevo aparece como bloque en el mundo, nunca en el inventario.
 */
public class LayEggGoal extends Goal {
    private static final int LAY_TICKS = 80;

    private final PrehistoricEntity mob;
    private BlockPos site;
    private int layTicks;
    private int giveUp;

    public LayEggGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!mob.isFemale() || !mob.readyToLay() || mob.getAction() != EntityAction.NONE
                || mob.isOrderedToSit() || !mob.onGround() || mob.getTarget() != null) return false;
        if (mob.getRandom().nextInt(20) != 0) return false;
        site = findSite();
        return site != null;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.readyToLay() && (layTicks > 0 || (giveUp > 0 && !mob.getNavigation().isDone()));
    }

    @Override
    public void start() {
        layTicks = 0;
        giveUp = 400;
        mob.getNavigation().moveTo(site.getX() + 0.5, site.getY(), site.getZ() + 0.5, 1.0);
    }

    @Override
    public void tick() {
        if (layTicks > 0) {
            mob.getNavigation().stop();
            if (--layTicks == 0) {
                if (mob.layEgg(site)) mob.finishLaying();
                mob.setAction(EntityAction.NONE);
            }
            return;
        }
        giveUp--;
        if (mob.distanceToSqr(site.getX() + 0.5, site.getY(), site.getZ() + 0.5) < 2.5 * Math.max(1.0, mob.getBbWidth())) {
            layTicks = LAY_TICKS;
            mob.setAction(EntityAction.LAYING);
            mob.getNavigation().stop();
        }
    }

    @Override
    public void stop() {
        if (mob.getAction() == EntityAction.LAYING) mob.setAction(EntityAction.NONE);
        layTicks = 0;
        site = null;
    }

    private BlockPos findSite() {
        Level level = mob.level();
        BlockPos base = mob.blockPosition();
        Block preferred = mob.preferredNest();
        BlockPos any = null;
        for (BlockPos p : BlockPos.betweenClosed(base.offset(-10, -3, -10), base.offset(10, 3, 10))) {
            BlockState st = level.getBlockState(p);
            if (st.getBlock() instanceof NestBlock && level.getBlockState(p.above()).isAir()) {
                if (st.is(preferred)) return p.above().immutable();   // el nido que mas le gusta
                if (any == null) any = p.above().immutable();
            }
        }
        if (any != null) return any;
        for (int i = 0; i < 12; i++) {
            BlockPos c = base.offset(mob.getRandom().nextInt(13) - 6, mob.getRandom().nextInt(3) - 1,
                    mob.getRandom().nextInt(13) - 6);
            if (level.getBlockState(c).isAir() && level.getBlockState(c.below()).isFaceSturdy(level, c.below(), Direction.UP)
                    && level.getFluidState(c).isEmpty()) {
                return c.immutable();
            }
        }
        return null;
    }
}
