package com.prehistoricsurvival.entity;

import com.prehistoricsurvival.entity.ai.GrazeGoal;
import com.prehistoricsurvival.entity.ai.LayEggGoal;
import com.prehistoricsurvival.entity.ai.MateGoal;
import com.prehistoricsurvival.entity.ai.StayNearNestGoal;
import com.prehistoricsurvival.entity.ai.SleepAtNightGoal;
import com.prehistoricsurvival.entity.ai.StayNearHerdGoal;
import com.prehistoricsurvival.registry.ModSounds;
import com.prehistoricsurvival.registry.ModTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Dodo: ave pequena, gregaria y pacifica. Pasta, duerme de noche, vive en bandadas
 * guiadas por el individuo alfa y huye de los depredadores. No se defiende.
 */
public class DodoEntity extends PrehistoricEntity {

    public static final SpeciesData DATA = new SpeciesData(
            12.0,     // vida base
            1.0,      // dano base
            0.24,     // velocidad base
            12000,    // bebe: 10 min
            24000,    // juvenil: 20 min
            4,        // comidas para domesticar
            30,       // hambre que recupera cada comida
            60,       // 1 de cada 60 es alfa
            2.0,      // multiplicador de estadisticas del alfa
            1.3f,     // escala del alfa
            0.92f,    // escala de las hembras
            16.0,     // radio de alerta
            3000,     // gestacion
            9000,     // incubacion
            24000,    // descanso tras aparearse
            false,    // herbivoro
            0.0,      // no advierte a los jugadores
            0,
            0.8f,     // escala del modelo: un dodo real mide ~1 m
            0.7f      // escala al terminar de ser bebe
    );

    private static final Item[] FOODS = {
            Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.SWEET_BERRIES
    };

    public DodoEntity(EntityType<? extends DodoEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, DATA.baseHealth())
                .add(Attributes.MOVEMENT_SPEED, DATA.baseSpeed())
                .add(Attributes.ATTACK_DAMAGE, DATA.baseDamage());
    }

    @Override
    public SpeciesData species() {
        return DATA;
    }

    @Override
    protected int variantCount() {
        return 3;
    }

    @Override
    protected boolean isTamingFood(ItemStack stack) {
        for (Item food : FOODS) {
            if (stack.is(food)) return true;
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.6));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class,
                e -> e.getType().is(ModTags.PREDATORS), 14.0f, 1.1, 1.5,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LayEggGoal(this));
        this.goalSelector.addGoal(4, new SleepAtNightGoal(this));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.1, 8.0f, 2.5f, false));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1, Ingredient.of(FOODS), false));
        this.goalSelector.addGoal(7, new GrazeGoal(this));
        this.goalSelector.addGoal(8, new StayNearNestGoal(this));
        this.goalSelector.addGoal(8, new MateGoal(this));
        this.goalSelector.addGoal(8, new StayNearHerdGoal(this, 1.0, 9.0));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Aletea al caer: planea suavemente, como las aves de corral.
        Vec3 motion = getDeltaMovement();
        if (!onGround() && motion.y < 0.0 && !isInWater()) {
            setDeltaMovement(motion.multiply(1.0, 0.6, 1.0));
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void onAttacked(LivingEntity attacker) {
        super.onAttacked(attacker);
        if (!isTame()) {
            playSound(ModSounds.DODO_ALERT.get(), getSoundVolume(), getVoicePitch());
        }
    }

    // ------------------------------------------------------------------ sonidos

    @Override
    protected SoundEvent getAmbientSound() {
        return isBaby() ? ModSounds.DODO_CHICK.get() : ModSounds.DODO_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DODO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DODO_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * (isAlpha() ? 0.75f : 1.0f);
    }
}
