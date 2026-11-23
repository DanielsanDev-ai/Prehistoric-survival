package com.prehistoricsurvival.entity.ai;

import java.util.EnumSet;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

/**
 * Territorialidad en 6 pasos:
 * 1) detecta al intruso  2) se gira hacia el  3) emite un sonido de advertencia
 * 4) adopta postura amenazante  5) si el jugador se aleja, se calma
 * 6) si se acerca demasiado o insiste, ataca.
 * Una criatura dormida solo reacciona a distancia corta, y con un nido cerca el radio aumenta.
 */
public class IntruderWarningGoal extends Goal {
    private final PrehistoricEntity mob;
    private Player intruder;
    private int warnTimer;
    private int calmTimer;

    public IntruderWarningGoal(PrehistoricEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private double radius() {
        double r = mob.species().warnRadius();
        if (mob.hasNest()) r *= 1.4;
        return mob.getAction() == EntityAction.SLEEPING ? r * 0.5 : r;
    }

    @Override
    public boolean canUse() {
        if (mob.species().warnRadius() <= 0 || mob.isTame() || mob.isYoung() || mob.getTarget() != null
                || mob.isSedated() || mob.getAction() == EntityAction.LAYING) return false;
        if (mob.tickCount % 10 != 0) return false;
        Player p = mob.level().getNearestPlayer(mob.getX(), mob.getY(), mob.getZ(), radius(),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        if (p == null || !mob.hasLineOfSight(p)) return false;
        intruder = p;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return intruder != null && intruder.isAlive() && !intruder.isCreative() && !intruder.isSpectator()
                && mob.getTarget() == null && !mob.isSedated() && calmTimer < 60;
    }

    @Override
    public void start() {
        warnTimer = 0;
        calmTimer = 0;
        mob.setAction(EntityAction.THREAT);
        mob.getNavigation().stop();
        mob.playWarnSound();
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(intruder, 30f, 30f);
        mob.getNavigation().stop();
        double d = mob.distanceTo(intruder);
        double r = mob.species().warnRadius() * (mob.hasNest() ? 1.4 : 1.0);
        if (d > r * 1.3) {
            calmTimer++;
            warnTimer = Math.max(0, warnTimer - 2);
            return;
        }
        calmTimer = 0;
        warnTimer++;
        if (warnTimer % 40 == 0) mob.playWarnSound();
        if (d < r * 0.4 || warnTimer > mob.species().warnTicks()) {
            mob.setTarget(intruder);   // paso 6: ataca
        }
    }

    @Override
    public void stop() {
        if (mob.getAction() == EntityAction.THREAT) mob.setAction(EntityAction.NONE);
        intruder = null;
    }
}
