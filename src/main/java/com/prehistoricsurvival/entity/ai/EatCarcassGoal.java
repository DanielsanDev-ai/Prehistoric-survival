package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/**
 * Tras abatir una presa el depredador va a ella y come (animacion de comer, particulas y sonido).
 * Al terminar recupera hambre y vida y deja huesos y restos. Si lo interrumpen, el botin se suelta entero.
 */
public class EatCarcassGoal extends Goal {
    private static final int EAT_TICKS = 100;

    private final PrehistoricEntity mob;
    private int eatTicks;
    private int giveUp;

    public EatCarcassGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        Vec3 c = mob.getCarcassPos();
        return c != null && mob.getTarget() == null && mob.getAction() == EntityAction.NONE
                && !mob.isOrderedToSit() && mob.distanceToSqr(c) < 24.0 * 24.0;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.hasCarcass() && mob.getTarget() == null && (eatTicks > 0 || giveUp > 0);
    }

    @Override
    public void start() {
        giveUp = 300;
        eatTicks = 0;
        Vec3 c = mob.getCarcassPos();
        mob.getNavigation().moveTo(c.x, c.y, c.z, 1.1);
    }

    @Override
    public void tick() {
        Vec3 c = mob.getCarcassPos();
        if (c == null) return;
        mob.getLookControl().setLookAt(c.x, c.y, c.z);
        if (eatTicks > 0) {
            mob.getNavigation().stop();
            eatTicks--;
            if (eatTicks % 20 == 0 && mob.level() instanceof ServerLevel level) {
                level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BEEF)),
                        c.x, c.y + 0.4, c.z, 8, 0.25, 0.2, 0.25, 0.05);
                mob.playSound(SoundEvents.GENERIC_EAT, 0.9f, 0.7f);
            }
            if (eatTicks == 0) finish();
            return;
        }
        giveUp--;
        double reach = 2.5 * Math.max(1.0, mob.getBbWidth());
        if (mob.distanceToSqr(c) < reach * reach) {
            eatTicks = EAT_TICKS;
            mob.setAction(EntityAction.EATING);
            mob.getNavigation().stop();
        } else if (mob.tickCount % 10 == 0) {
            mob.getNavigation().moveTo(c.x, c.y, c.z, 1.1);
        }
    }

    private void finish() {
        int food = mob.getCarcassFood();
        mob.addHunger(food);
        mob.heal(food / 10.0f);
        mob.consumeCarcass(0.5f);
        mob.setAction(EntityAction.NONE);
    }

    @Override
    public void stop() {
        if (mob.getAction() == EntityAction.EATING) mob.setAction(EntityAction.NONE);
        if (eatTicks > 0 && mob.hasCarcass()) mob.consumeCarcass(1.0f);   // interrumpido
        eatTicks = 0;
    }
}
