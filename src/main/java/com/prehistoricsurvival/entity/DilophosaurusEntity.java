package com.prehistoricsurvival.entity;

import javax.annotation.Nullable;

import com.prehistoricsurvival.entity.ai.EatCarcassGoal;
import com.prehistoricsurvival.entity.ai.FleeWhenWoundedGoal;
import com.prehistoricsurvival.entity.ai.FollowParentGoal;
import com.prehistoricsurvival.entity.ai.HuntGoal;
import com.prehistoricsurvival.entity.ai.HuntTargetGoal;
import com.prehistoricsurvival.entity.ai.IntruderWarningGoal;
import com.prehistoricsurvival.entity.ai.LayEggGoal;
import com.prehistoricsurvival.entity.ai.MateGoal;
import com.prehistoricsurvival.entity.ai.SedatedGoal;
import com.prehistoricsurvival.entity.ai.SleepAtNightGoal;
import com.prehistoricsurvival.entity.ai.StayNearHerdGoal;
import com.prehistoricsurvival.entity.ai.StayNearNestGoal;
import com.prehistoricsurvival.entity.ai.VenomSpitGoal;
import com.prehistoricsurvival.registry.ModEntities;
import com.prehistoricsurvival.registry.ModSounds;
import com.prehistoricsurvival.registry.ModTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.phys.Vec3;

/**
 * Dilophosaurus (tamano real: ~1 m de cadera, ~4 m de largo). Depredador de emboscada con dos crestas
 * sobre el craneo y una gorguera que despliega al amenazar o escupir. Su habilidad: lanza un proyectil de
 * veneno que ralentiza y ciega (tiempo de reutilizacion de 10 s; 7 s el alfa).
 * Se domestica con CONFIANZA Y TIEMPO: pescado crudo, una vez cada 2,5 minutos, sin atacarlo.
 */
public class DilophosaurusEntity extends PrehistoricEntity {

    public static final SpeciesData DATA = new SpeciesData(
            40.0,     // vida base
            6.0,      // dano base
            0.32,     // velocidad base
            12000,    // bebe: 10 min
            18000,    // adolescente: 15 min
            5,        // pescados para domesticar (uno cada 2,5 min)
            40,
            45,       // 1 de cada 45 es alfa
            1.9, 1.2f,
            0.92f,
            20.0,
            5000,     // gestacion
            12000,    // incubacion
            24000,    // descanso tras aparearse
            true,
            12.0,     // advierte a 12 bloques
            100,
            1.3f,     // escala del modelo (cadera ~1 m)
            0.5f
    );

    private static final Item[] FOODS = {Items.COD, Items.SALMON, Items.TROPICAL_FISH};

    private int spitCooldown;
    private float frill;
    private float frillO;

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
    @Override protected int variantCount() { return 3; }
    @Override public TameMethod tameMethod() { return TameMethod.TRUST; }

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
        this.goalSelector.addGoal(0, new SedatedGoal(this));
        this.goalSelector.addGoal(1, new FleeWhenWoundedGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class,
                e -> e.getType().is(ModTags.APEX_PREDATORS), 20.0f, 1.0, 1.4,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new IntruderWarningGoal(this));
        this.goalSelector.addGoal(5, new VenomSpitGoal(this));
        this.goalSelector.addGoal(6, new HuntGoal(this));
        this.goalSelector.addGoal(7, new EatCarcassGoal(this));
        this.goalSelector.addGoal(8, new LayEggGoal(this));
        this.goalSelector.addGoal(9, new SleepAtNightGoal(this));
        this.goalSelector.addGoal(10, new FollowOwnerGoal(this, 1.2, 8.0f, 2.5f, false));
        this.goalSelector.addGoal(11, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(12, new MateGoal(this));
        this.goalSelector.addGoal(13, new StayNearNestGoal(this));
        this.goalSelector.addGoal(14, new StayNearHerdGoal(this, 1.0, 12.0));
        this.goalSelector.addGoal(15, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(16, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(17, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new HuntTargetGoal(this, ModTags.SMALL_PREY));
    }

    // ------------------------------------------------------------------ habilidad: escupir veneno

    public boolean canSpit() {
        return isAdult() && spitCooldown <= 0 && !isSedated();
    }

    /** Se muestra la gorguera y se suelta el cascabeleo justo antes de escupir. */
    public void playFrillSound() {
        playSound(ModSounds.DILO_FRILL.get(), getSoundVolume(), getVoicePitch());
    }

    public void spitAt(LivingEntity target) {
        VenomSpitEntity spit = new VenomSpitEntity(ModEntities.VENOM_SPIT.get(), this, level());
        spit.setPotent(isAlpha());
        Vec3 look = getLookAngle();
        spit.setPos(getX() + look.x * getBbWidth(), getEyeY() - 0.1, getZ() + look.z * getBbWidth());
        double dx = target.getX() - spit.getX();
        double dy = target.getEyeY() - 0.2 - spit.getY();
        double dz = target.getZ() - spit.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        spit.shoot(dx, dy + horizontal * 0.1, dz, 1.3f, 2.0f);
        level().addFreshEntity(spit);
        playSound(ModSounds.DILO_SPIT.get(), 1.0f, getVoicePitch());
        spitCooldown = isAlpha() ? 140 : 200;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && spitCooldown > 0) spitCooldown--;

        // Animacion de la gorguera: se despliega con suavidad al amenazar, escupir o atacar.
        frillO = frill;
        boolean show = getAction() == EntityAction.THREAT || getAction() == EntityAction.SPITTING || isAggressive();
        frill = Mth.clamp(frill + (show ? 0.12f : -0.08f), 0.0f, 1.0f);
    }

    /** 0 = plegada, 1 = totalmente desplegada. */
    public float getFrill(float partialTick) {
        return Mth.lerp(partialTick, frillO, frill);
    }

    // ------------------------------------------------------------------ sonidos

    @Override
    public boolean doHurtTarget(Entity target) {
        playSound(isAlpha() ? ModSounds.DILO_ATTACK_ALPHA.get() : ModSounds.DILO_ATTACK.get(), getSoundVolume(), getVoicePitch());
        return super.doHurtTarget(target);
    }

    @Override
    public void playWarnSound() {
        playSound(isAlpha() ? ModSounds.DILO_WARN_ALPHA.get() : ModSounds.DILO_WARN.get(), getSoundVolume(), getVoicePitch());
    }

    @Nullable
    @Override
    protected SoundEvent getStepSoundEvent() {
        return ModSounds.DILO_STEP.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (isBaby()) return ModSounds.DILO_CHICK.get();
        return isAlpha() ? ModSounds.DILO_AMBIENT_ALPHA.get() : ModSounds.DILO_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return isAlpha() ? ModSounds.DILO_HURT_ALPHA.get() : ModSounds.DILO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isAlpha() ? ModSounds.DILO_DEATH_ALPHA.get() : ModSounds.DILO_DEATH.get();
    }
}
