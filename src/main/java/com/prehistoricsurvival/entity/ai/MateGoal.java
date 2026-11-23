package com.prehistoricsurvival.entity.ai;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;

/** El macho adulto busca una hembra disponible de su especie; al llegar, ella queda gestante. */
public class MateGoal extends Goal {
    private final PrehistoricEntity mob;
    private PrehistoricEntity partner;
    private int timeout;
    private int recalc;

    public MateGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.isFemale() || !mob.canMate() || mob.getRandom().nextInt(120) != 0) return false;
        List<PrehistoricEntity> females = mob.level().getEntitiesOfClass(PrehistoricEntity.class,
                mob.getBoundingBox().inflate(20.0, 6.0, 20.0),
                e -> e != mob && e.getType() == mob.getType() && e.isFemale() && e.canMate());
        partner = females.stream().min(Comparator.comparingDouble(mob::distanceToSqr)).orElse(null);
        return partner != null;
    }

    @Override
    public boolean canContinueToUse() {
        return partner != null && partner.isAlive() && partner.canMate() && mob.canMate() && timeout > 0;
    }

    @Override
    public void start() {
        timeout = 400;
        recalc = 0;
    }

    @Override
    public void tick() {
        timeout--;
        mob.getLookControl().setLookAt(partner, 30f, 30f);
        if (--recalc <= 0) {
            recalc = 10;
            mob.getNavigation().moveTo(partner, 1.0);
        }
        if (mob.distanceToSqr(partner) < 4.0 * Math.max(1.0, mob.getBbWidth())) {
            partner.startPregnancy(mob);
            mob.startBreedCooldown();
            partner.startBreedCooldown();
            if (mob.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.HEART, partner.getX(), partner.getY() + partner.getBbHeight(),
                        partner.getZ(), 7, 0.5, 0.5, 0.5, 0.02);
            }
            partner = null;
        }
    }

    @Override
    public void stop() {
        partner = null;
        mob.getNavigation().stop();
    }
}
