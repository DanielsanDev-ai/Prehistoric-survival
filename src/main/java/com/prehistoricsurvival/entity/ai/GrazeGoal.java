package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Herbivoros: cuando tienen hambre buscan cesped, se acercan, agachan la cabeza
 * (animacion EATING) y comen. El bloque de cesped se convierte en tierra.
 */
public class GrazeGoal extends Goal {
    private static final int EAT_TICKS = 40;

    private final PrehistoricEntity mob;
    private BlockPos target;
    private int eatTicks;
    private int giveUp;

    public GrazeGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (mob.getHunger() > 75 || mob.getAction() != EntityAction.NONE || mob.isOrderedToSit()) return false;
        if (mob.getRandom().nextInt(mob.getHunger() < 30 ? 10 : 50) != 0) return false;
        target = findGrass();
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return eatTicks > 0 || (target != null && giveUp > 0 && !mob.getNavigation().isDone());
    }

    @Override
    public void start() {
        giveUp = 200;
        eatTicks = 0;
        if (closeEnough()) {
            beginEating();
        } else {
            mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0);
        }
    }

    @Override
    public void tick() {
        if (eatTicks > 0) {
            mob.getNavigation().stop();
            if (--eatTicks == 0) finishEating();
            return;
        }
        giveUp--;
        if (closeEnough()) beginEating();
    }

    @Override
    public void stop() {
        if (mob.getAction() == EntityAction.EATING) mob.setAction(EntityAction.NONE);
        target = null;
        eatTicks = 0;
    }

    private void beginEating() {
        eatTicks = EAT_TICKS;
        mob.setAction(EntityAction.EATING);
        mob.getNavigation().stop();
    }

    private void finishEating() {
        Level level = mob.level();
        BlockPos ground = mob.blockPosition().below();
        if (level.getBlockState(ground).is(Blocks.GRASS_BLOCK) && ForgeEventFactory.getMobGriefingEvent(level, mob)) {
            level.levelEvent(2001, ground, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
            level.setBlock(ground, Blocks.DIRT.defaultBlockState(), 2);
        }
        mob.addHunger(35);
        mob.gameEvent(GameEvent.EAT);
        mob.setAction(EntityAction.NONE);
    }

    private boolean closeEnough() {
        return target != null
                && mob.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) < 2.0;
    }

    private BlockPos findGrass() {
        Level level = mob.level();
        BlockPos here = mob.blockPosition();
        if (isGrazable(level, here)) return here;
        RandomSource r = mob.getRandom();
        for (int i = 0; i < 12; i++) {
            BlockPos c = here.offset(r.nextInt(13) - 6, r.nextInt(5) - 2, r.nextInt(13) - 6);
            if (isGrazable(level, c)) return c;
        }
        return null;
    }

    private static boolean isGrazable(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }
}
