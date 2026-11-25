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
import com.prehistoricsurvival.entity.ai.StayNearNestGoal;
import com.prehistoricsurvival.registry.ModItems;
import com.prehistoricsurvival.registry.ModSounds;
import com.prehistoricsurvival.registry.ModTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Dilophosaurus: depredador mediano reconocible por sus dos crestas y su gola extensible.
 * Usa la misma base de guardado, crecimiento, reproduccion, huevos y seguimiento que el resto.
 */
public class DilophosaurusEntity extends PrehistoricEntity {
    public static final SpeciesData DATA = new SpeciesData(
            34.0, 5.0, 0.31,
            12000, 20000,
            8, 35,
            48, 1.75, 1.18f,
            0.98f, 18.0,
            6000, 15000, 30000,
            true, 8.0, 80,
            0.95f, 0.56f
    );

    public DilophosaurusEntity(EntityType<? extends DilophosaurusEntity> type, Level level) {
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
    @Override protected int variantCount() { return 1; }
    @Override public TameMethod tameMethod() { return TameMethod.STEALTH; }

    @Override
    protected boolean isTamingFood(ItemStack stack) {
        return stack.is(ModItems.RAW_DINO_MEAT.get())
                || stack.is(Items.BEEF) || stack.is(Items.PORKCHOP)
                || stack.is(Items.MUTTON) || stack.is(Items.CHICKEN);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(0, new SedatedGoal(this));
        goalSelector.addGoal(1, new FleeWhenWoundedGoal(this));
        goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(3, new IntruderWarningGoal(this));
        goalSelector.addGoal(4, new HuntGoal(this));
        goalSelector.addGoal(5, new EatCarcassGoal(this));
        goalSelector.addGoal(6, new LayEggGoal(this));
        goalSelector.addGoal(7, new SleepAtNightGoal(this));
        goalSelector.addGoal(8, new FollowOwnerGoal(this, 1.12, 8.0f, 2.5f, false));
        goalSelector.addGoal(9, new MateGoal(this));
        goalSelector.addGoal(10, new StayNearNestGoal(this));
        goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 0.85));
        goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(13, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(4, new HuntTargetGoal(this, ModTags.SMALL_PREY));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        playSound(ModSounds.DILO_ATTACK.get(), getSoundVolume(), getVoicePitch());
        return super.doHurtTarget(target);
    }

    @Override
    protected void onAttacked(net.minecraft.world.entity.LivingEntity attacker) {
        super.onAttacked(attacker);
        if (!isTame()) {
            setAction(EntityAction.THREAT);
            playWarnSound();
        }
    }

    @Override public void playWarnSound() {
        playSound(ModSounds.DILO_WARN.get(), getSoundVolume(), getVoicePitch());
    }

    @Override public void playPackCall() {
        playSound(ModSounds.DILO_AMBIENT.get(), 1.1f, getVoicePitch());
    }

    @Override protected SoundEvent getAmbientSound() { return ModSounds.DILO_AMBIENT.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.DILO_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.DILO_DEATH.get(); }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && getAction() == EntityAction.THREAT
                && getTarget() == null && tickCount % 40 == 0) {
            setAction(EntityAction.NONE);
        }
    }
}
