package com.prehistoricsurvival.entity.ai;

import com.prehistoricsurvival.entity.EntityAction;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

/**
 * Elige presa solo cuando tiene hambre, esta sano, despierto y no es cria.
 * Nunca elige mascotas domesticadas ni jugadores (los jugadores solo por territorialidad).
 */
public class HuntTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final PrehistoricEntity hunter;

    public HuntTargetGoal(PrehistoricEntity hunter, TagKey<EntityType<?>> preyTag) {
        super(hunter, LivingEntity.class, 10, true, false,
                e -> e.getType().is(preyTag) && !(e instanceof TamableAnimal pet && pet.isTame())
                        && (hunter.isAdult() || e.getMaxHealth() <= 12.0f));   // los jovenes solo cazan presas pequenas
        this.hunter = hunter;
    }

    @Override
    public boolean canUse() {
        return hunter.getHunger() < 60 && hunter.getAction() == EntityAction.NONE && !hunter.isTame()
                && !hunter.isBaby() && hunter.getHealth() >= hunter.getMaxHealth() * 0.5f && super.canUse();
    }
}
