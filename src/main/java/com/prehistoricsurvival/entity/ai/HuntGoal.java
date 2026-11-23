package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;
import java.util.List;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Caza en dos fases: ACECHO (se acerca despacio observando) y CARGA (sprint y mordisco).
 * En manada avisa a los companeros y cada uno rodea a la presa desde un angulo distinto.
 */
public class HuntGoal extends Goal {
    private static final int STALK_TIME = 60;

    private final PrehistoricEntity mob;
    private int attackCooldown;
    private int recalc;
    private int stalkTicks;
    private int lostTicks;
    private float flank;
    private boolean hasPack;

    public HuntGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private static boolean valid(LivingEntity t) {
        return t.isAlive() && !(t instanceof Player p && (p.isCreative() || p.isSpectator()));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        return t != null && valid(t) && !mob.isOrderedToSit() && !mob.isSedated()
                && mob.getAction() != EntityAction.SLEEPING;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity t = mob.getTarget();
        return canUse() && lostTicks < 100 && mob.distanceToSqr(t) < 40.0 * 40.0;
    }

    @Override
    public void start() {
        attackCooldown = 10;
        stalkTicks = 0;
        lostTicks = 0;
        recalc = 0;
        flank = (mob.getRandom().nextFloat() - 0.5f) * 1.8f;
        hasPack = false;
        if (!mob.isTame()) alertPack();
    }

    @Override
    public void stop() {
        mob.setAggressive(false);
        mob.getNavigation().stop();
        LivingEntity t = mob.getTarget();
        if (t != null && !valid(t)) mob.setTarget(null);
    }

    @Override
    public void tick() {
        LivingEntity t = mob.getTarget();
        double d = mob.distanceToSqr(t);
        mob.getLookControl().setLookAt(t, 30f, 30f);
        lostTicks = mob.getSensing().hasLineOfSight(t) ? 0 : lostTicks + 1;
        if (attackCooldown > 0) attackCooldown--;

        if (d < 14.0 * 14.0) stalkTicks++;
        boolean charging = stalkTicks >= STALK_TIME || d < 25.0 || mob.getLastHurtByMob() == t || t instanceof Player;
        mob.setAggressive(charging);
        double speed = charging ? 1.5 : 0.7;

        if (--recalc <= 0) {
            recalc = 4 + mob.getRandom().nextInt(7);
            if (charging && hasPack && d > 25.0) {
                Vec3 from = mob.position().subtract(t.position());
                from = new Vec3(from.x, 0, from.z);
                if (from.lengthSqr() > 0.01) {
                    Vec3 dir = from.normalize().yRot(flank);
                    Vec3 goal = t.position().add(dir.scale(Math.min(4.0, Math.sqrt(d))));
                    mob.getNavigation().moveTo(goal.x, goal.y, goal.z, speed);
                } else {
                    mob.getNavigation().moveTo(t, speed);
                }
            } else {
                mob.getNavigation().moveTo(t, speed);
            }
        }

        double reach = mob.getBbWidth() * 2.0f * mob.getBbWidth() * 2.0f + t.getBbWidth();
        if (charging && d <= reach && attackCooldown <= 0) {
            attackCooldown = 20;
            mob.swing(InteractionHand.MAIN_HAND);
            mob.doHurtTarget(t);
        }
    }

    private void alertPack() {
        double r = mob.species().alertRadius();
        List<PrehistoricEntity> pack = mob.level().getEntitiesOfClass(PrehistoricEntity.class,
                mob.getBoundingBox().inflate(r, 6.0, r),
                e -> e != mob && e.getType() == mob.getType() && e.isAlive() && !e.isTame()
                        && !e.isBaby() && e.getTarget() == null && !e.isSedated());
        for (PrehistoricEntity e : pack) {
            e.setTarget(mob.getTarget());
            if (e.getAction() == EntityAction.SLEEPING) e.setAction(EntityAction.NONE);
        }
        hasPack = !pack.isEmpty();
        if (hasPack) mob.playPackCall();
    }
}
