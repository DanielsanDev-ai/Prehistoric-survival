package com.prehistoricsurvival.entity;

import com.prehistoricsurvival.entity.ai.EatCarcassGoal;
import com.prehistoricsurvival.entity.ai.FleeWhenWoundedGoal;
import com.prehistoricsurvival.entity.ai.HuntGoal;
import com.prehistoricsurvival.entity.ai.HuntTargetGoal;
import com.prehistoricsurvival.entity.ai.IntruderWarningGoal;
import com.prehistoricsurvival.entity.ai.LayEggGoal;
import com.prehistoricsurvival.entity.ai.MateGoal;
import com.prehistoricsurvival.entity.ai.SedatedGoal;
import com.prehistoricsurvival.entity.ai.SleepAtNightGoal;
import com.prehistoricsurvival.entity.ai.StayNearHerdGoal;
import com.prehistoricsurvival.entity.ai.StayNearNestGoal;
import com.prehistoricsurvival.registry.ModItems;
import com.prehistoricsurvival.registry.ModSounds;
import com.prehistoricsurvival.registry.ModTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Velociraptor (tamano real: ~0,5 m de cadera, ~2 m de largo). Cazador de manada:
 * acecha, avisa a los suyos y rodea a la presa; las crias y los huevos hacen que advierta antes.
 * Se domestica con SIGILO: hay que darle carne cruda agachado, o desconfia.
 */
public class VelociraptorEntity extends PrehistoricEntity {

    public static final SpeciesData DATA = new SpeciesData(
            28.0,     // vida base
            5.0,      // dano base
            0.34,     // velocidad base
            12000,    // bebe
            18000,    // juvenil
            8,        // comidas para domesticar
            35,       // hambre por comida
            40,       // 1 de cada 40 es alfa
            2.0, 1.25f,
            0.92f,    // las hembras son algo mas pequenas
            24.0,     // radio de alerta de la manada
            5000,     // gestacion
            12000,    // incubacion
            24000,    // descanso tras aparearse
            true,     // carnivoro
            9.0,      // advierte al jugador a 9 bloques
            100,
            0.67f,    // escala del modelo (cadera ~0,5 m)
            0.5f      // escala al terminar de ser bebe
    );

    private static final Item[] FOODS = {
            Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT
    };

    public VelociraptorEntity(EntityType<? extends VelociraptorEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, DATA.baseHealth())
                .add(Attributes.MOVEMENT_SPEED, DATA.baseSpeed())
                .add(Attributes.ATTACK_DAMAGE, DATA.baseDamage())
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override public SpeciesData species() { return DATA; }
    @Override protected int variantCount() { return 3; }
    @Override public TameMethod tameMethod() { return TameMethod.STEALTH; }

    @Override
    protected boolean isTamingFood(ItemStack stack) {
        if (stack.is(ModItems.RAW_DINO_MEAT.get())) return true;
        for (Item food : FOODS) {
            if (stack.is(food)) return true;
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new SedatedGoal(this));
        this.goalSelector.addGoal(1, new FleeWhenWoundedGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class,
                e -> e.getType().is(ModTags.APEX_PREDATORS), 20.0f, 1.0, 1.4,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new IntruderWarningGoal(this));
        this.goalSelector.addGoal(5, new HuntGoal(this));
        this.goalSelector.addGoal(6, new EatCarcassGoal(this));
        this.goalSelector.addGoal(7, new LayEggGoal(this));
        this.goalSelector.addGoal(8, new SleepAtNightGoal(this));
        this.goalSelector.addGoal(9, new FollowOwnerGoal(this, 1.2, 8.0f, 2.5f, false));
        this.goalSelector.addGoal(10, new MateGoal(this));
        this.goalSelector.addGoal(11, new StayNearNestGoal(this));
        this.goalSelector.addGoal(12, new StayNearHerdGoal(this, 1.0, 10.0));
        this.goalSelector.addGoal(13, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(14, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new HuntTargetGoal(this, ModTags.SMALL_PREY));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        playSound(ModSounds.RAPTOR_ATTACK.get(), getSoundVolume(), getVoicePitch());
        return super.doHurtTarget(target);
    }

    @Override
    public void playWarnSound() {
        playSound(ModSounds.RAPTOR_WARN.get(), getSoundVolume(), getVoicePitch());
    }

    @Override
    public void playPackCall() {
        playSound(ModSounds.RAPTOR_CALL.get(), 1.4f, getVoicePitch());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isBaby() ? ModSounds.RAPTOR_CHICK.get() : ModSounds.RAPTOR_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.RAPTOR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.RAPTOR_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * (isAlpha() ? 0.8f : 1.0f);
    }
}
