package com.prehistoricsurvival.entity;

import com.prehistoricsurvival.entity.ai.EatCarcassGoal;
import com.prehistoricsurvival.entity.ai.HuntGoal;
import com.prehistoricsurvival.entity.ai.HuntTargetGoal;
import com.prehistoricsurvival.entity.ai.IntruderWarningGoal;
import com.prehistoricsurvival.entity.ai.InvestigateNoiseGoal;
import com.prehistoricsurvival.entity.ai.LayEggGoal;
import com.prehistoricsurvival.entity.ai.MateGoal;
import com.prehistoricsurvival.entity.ai.SedatedGoal;
import com.prehistoricsurvival.entity.ai.SleepAtNightGoal;
import com.prehistoricsurvival.entity.ai.StayNearNestGoal;
import com.prehistoricsurvival.registry.ModItems;
import com.prehistoricsurvival.registry.ModSounds;
import com.prehistoricsurvival.registry.ModTags;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tyrannosaurus rex (tamano real: ~3 m de cadera, ~9 m de largo; las hembras son mas grandes).
 * Solitario y territorial: advierte desde 18 bloques con rugidos y postura amenazante, acude a
 * investigar ruidos de combate, caza presas grandes y se come el cadaver.
 * Se domestica con SEDACION: carne sedante para dormirlo y carne cocinada mientras duerme.
 */
public class TyrannosaurusEntity extends PrehistoricEntity {

    public static final SpeciesData DATA = new SpeciesData(
            160.0,    // vida base
            22.0,     // dano base
            0.27,     // velocidad base
            24000,    // bebe: 20 min
            48000,    // juvenil: 40 min
            10,       // comidas (cocinadas) para domesticar
            60,
            45,       // 1 de cada 45 es alfa
            1.8, 1.2f,
            1.1f,     // las hembras son MAS grandes
            36.0,
            8000,     // gestacion
            18000,    // incubacion
            48000,    // descanso tras aparearse
            true,
            18.0,     // advierte a 18 bloques
            80,
            2.7f,     // escala del modelo (cadera ~3 m)
            0.2f      // un bebe mide ~20% de un adulto
    );

    private static final Item[] FOODS = {
            Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.COOKED_MUTTON, Items.COOKED_CHICKEN
    };

    public TyrannosaurusEntity(EntityType<? extends TyrannosaurusEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, DATA.baseHealth())
                .add(Attributes.MOVEMENT_SPEED, DATA.baseSpeed())
                .add(Attributes.ATTACK_DAMAGE, DATA.baseDamage())
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override public SpeciesData species() { return DATA; }
    @Override protected int variantCount() { return 3; }
    @Override public TameMethod tameMethod() { return TameMethod.SEDATE; }
    @Override public boolean investigatesNoise() { return true; }

    @Override
    protected boolean isTamingFood(ItemStack stack) {
        if (stack.is(ModItems.COOKED_DINO_MEAT.get())) return true;
        for (Item food : FOODS) {
            if (stack.is(food)) return true;
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new SedatedGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new IntruderWarningGoal(this));
        this.goalSelector.addGoal(4, new HuntGoal(this));
        this.goalSelector.addGoal(5, new EatCarcassGoal(this));
        this.goalSelector.addGoal(6, new InvestigateNoiseGoal(this));
        this.goalSelector.addGoal(7, new LayEggGoal(this));
        this.goalSelector.addGoal(8, new SleepAtNightGoal(this));
        this.goalSelector.addGoal(9, new FollowOwnerGoal(this, 1.0, 10.0f, 3.0f, false));
        this.goalSelector.addGoal(10, new MateGoal(this));
        this.goalSelector.addGoal(11, new StayNearNestGoal(this));
        this.goalSelector.addGoal(12, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(13, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));

        // Es solitario: no hay metas de manada ni de seguir a otros de su especie.
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new HuntTargetGoal(this, ModTags.LARGE_PREY));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        playSound(ModSounds.REX_ATTACK.get(), 1.6f, getVoicePitch());
        return super.doHurtTarget(target);
    }

    @Override
    public void playWarnSound() {
        playSound(ModSounds.REX_WARN.get(), 2.0f, getVoicePitch());
    }

    /** Pisadas pesadas propias. */
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (isBaby()) {
            super.playStepSound(pos, state);
        } else {
            playSound(ModSounds.REX_STEP.get(), 0.9f, 0.9f + getRandom().nextFloat() * 0.2f);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isBaby() ? ModSounds.REX_CHICK.get() : ModSounds.REX_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.REX_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.REX_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * (isAlpha() ? 0.8f : 1.0f);
    }

    @Override
    protected float getSoundVolume() {
        return 2.0f;
    }
}
