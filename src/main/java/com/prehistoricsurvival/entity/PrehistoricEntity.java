package com.prehistoricsurvival.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Base de todas las criaturas del mod. Aqui vive lo comun a todas las especies:
 * genero, nivel, alfa, variante de color, crecimiento por etapas, hambre,
 * domesticacion progresiva y guardado de datos.
 */
public abstract class PrehistoricEntity extends TamableAnimal {
    /** Version del formato de datos guardados. Subir al cambiar el formato y migrar en readAdditionalSaveData. */
    public static final int SAVE_VERSION = 1;

    private static final EntityDataAccessor<Boolean> DATA_FEMALE =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ALPHA =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_LEVEL =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_GROWTH =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ACTION =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TAME_PROGRESS =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HUNGER =
            SynchedEntityData.defineId(PrehistoricEntity.class, EntityDataSerializers.INT);

    /** Copia local de los ticks de crecimiento (se avanza en cliente y servidor, el servidor corrige cada 5 s). */
    private int growthTicks;
    private int tameCooldown;
    private GrowthStage lastStage;
    private int lastScaleStep = -1;

    protected PrehistoricEntity(EntityType<? extends PrehistoricEntity> type, Level level) {
        super(type, level);
        this.growthTicks = this.entityData.get(DATA_GROWTH);
        this.lastStage = getStage();
        refreshDimensions();
    }

    // ------------------------------------------------------------------ datos de especie

    public abstract SpeciesData species();

    /** Alimento que acepta la criatura para domesticarla y alimentarla. */
    protected abstract boolean isTamingFood(ItemStack stack);

    /** Numero de variantes de color de la especie. */
    protected int variantCount() {
        return 1;
    }

    // ------------------------------------------------------------------ datos sincronizados

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FEMALE, false);
        this.entityData.define(DATA_ALPHA, false);
        this.entityData.define(DATA_LEVEL, 1);
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_GROWTH, species().adultTicks()); // por defecto: adulto
        this.entityData.define(DATA_ACTION, 0);
        this.entityData.define(DATA_TAME_PROGRESS, 0);
        this.entityData.define(DATA_HUNGER, 80);
    }

    @Override
    public void onSyncedDataUpdated(net.minecraft.network.syncher.EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_GROWTH.equals(key)) {
            this.growthTicks = this.entityData.get(DATA_GROWTH);
            refreshDimensions();
        } else if (DATA_ALPHA.equals(key) || DATA_FEMALE.equals(key)) {
            refreshDimensions();
        }
    }

    public boolean isFemale() { return this.entityData.get(DATA_FEMALE); }
    public void setFemale(boolean female) { this.entityData.set(DATA_FEMALE, female); }
    public boolean isAlpha() { return this.entityData.get(DATA_ALPHA); }
    public void setAlpha(boolean alpha) { this.entityData.set(DATA_ALPHA, alpha); }
    public int getCreatureLevel() { return this.entityData.get(DATA_LEVEL); }
    public void setCreatureLevel(int level) { this.entityData.set(DATA_LEVEL, Math.max(1, level)); }
    public int getVariant() { return this.entityData.get(DATA_VARIANT); }
    public void setVariant(int variant) { this.entityData.set(DATA_VARIANT, variant); }
    public int getHunger() { return this.entityData.get(DATA_HUNGER); }
    public void setHunger(int hunger) { this.entityData.set(DATA_HUNGER, Mth.clamp(hunger, 0, 100)); }
    public void addHunger(int amount) { setHunger(getHunger() + amount); }
    public int getTameProgress() { return this.entityData.get(DATA_TAME_PROGRESS); }
    public void setTameProgress(int progress) { this.entityData.set(DATA_TAME_PROGRESS, Mth.clamp(progress, 0, 100)); }
    public EntityAction getAction() { return EntityAction.byId(this.entityData.get(DATA_ACTION)); }
    public void setAction(EntityAction action) { this.entityData.set(DATA_ACTION, action.ordinal()); }
    public int getGrowthTicks() { return growthTicks; }

    // ------------------------------------------------------------------ crecimiento y tamano

    public GrowthStage getStage() {
        SpeciesData s = species();
        if (growthTicks < s.babyTicks()) return GrowthStage.BABY;
        if (growthTicks < s.adultTicks()) return GrowthStage.JUVENILE;
        return GrowthStage.ADULT;
    }

    public boolean isAdult() { return getStage() == GrowthStage.ADULT; }
    public boolean isYoung() { return getStage() != GrowthStage.ADULT; }

    /** Sustituye al sistema de edad de vanilla: aqui "bebe" es la primera etapa de crecimiento. */
    @Override
    public boolean isBaby() {
        return getStage() == GrowthStage.BABY;
    }

    /** Escala visual y de hitbox respecto al adulto normal. */
    public float getBodyScale() {
        SpeciesData s = species();
        float scale;
        if (growthTicks < s.babyTicks()) {
            scale = Mth.lerp(growthTicks / (float) Math.max(1, s.babyTicks()), 0.45f, 0.7f);
        } else if (growthTicks < s.adultTicks()) {
            scale = Mth.lerp((growthTicks - s.babyTicks()) / (float) Math.max(1, s.juvenileTicks()), 0.65f, 1.0f);
        } else {
            scale = 1.0f;
        }
        if (isAlpha()) scale *= s.alphaScale();
        if (isFemale()) scale *= s.femaleScale();
        return scale;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(getBodyScale());
    }

    // ------------------------------------------------------------------ estadisticas

    /** Recalcula salud, dano y velocidad segun nivel, etapa de vida y alfa. Conserva el % de vida. */
    protected void recalcStats() {
        SpeciesData s = species();
        float ratio = getMaxHealth() > 0 ? getHealth() / getMaxHealth() : 1.0f;

        double stage = switch (getStage()) {
            case BABY -> 0.3;
            case JUVENILE -> 0.65;
            case ADULT -> 1.0;
        };
        double speedStage = switch (getStage()) {
            case BABY -> 0.8;
            case JUVENILE -> 0.92;
            case ADULT -> 1.0;
        };
        double levelFactor = 1.0 + 0.06 * (getCreatureLevel() - 1);
        double alpha = isAlpha() ? s.alphaStatMultiplier() : 1.0;

        setBase(Attributes.MAX_HEALTH, s.baseHealth() * stage * levelFactor * alpha);
        setBase(Attributes.ATTACK_DAMAGE, s.baseDamage() * stage * (1.0 + 0.04 * (getCreatureLevel() - 1)) * alpha);
        setBase(Attributes.MOVEMENT_SPEED, s.baseSpeed() * speedStage * (1.0 + 0.004 * (getCreatureLevel() - 1)));

        setHealth(Math.max(1.0f, getMaxHealth() * ratio));
    }

    private void setBase(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        AttributeInstance instance = getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }

    // ------------------------------------------------------------------ aparicion

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        RandomSource r = level.getRandom();
        SpeciesData s = species();

        setFemale(r.nextBoolean());
        setVariant(r.nextInt(variantCount()));

        boolean alpha = s.alphaChance() > 0 && r.nextInt(s.alphaChance()) == 0;
        setAlpha(alpha);
        setCreatureLevel(alpha ? 30 + r.nextInt(11) : 1 + (int) (Math.pow(r.nextFloat(), 1.7) * 29));

        int growth = s.adultTicks();
        if (!alpha && reason != MobSpawnType.SPAWN_EGG && reason != MobSpawnType.COMMAND) {
            float roll = r.nextFloat();
            if (roll < 0.08f) growth = r.nextInt(Math.max(1, s.babyTicks()));
            else if (roll < 0.20f) growth = s.babyTicks() + r.nextInt(Math.max(1, s.juvenileTicks()));
        }
        this.growthTicks = growth;
        this.entityData.set(DATA_GROWTH, growth);
        this.lastStage = getStage();

        setHunger(60 + r.nextInt(40));
        recalcStats();
        setHealth(getMaxHealth());
        refreshDimensions();

        SpawnGroupData group = data != null ? data : new AgeableMob.AgeableMobGroupData(false);
        return super.finalizeSpawn(level, difficulty, reason, group, tag);
    }

    // ------------------------------------------------------------------ ciclo de vida

    @Override
    public void aiStep() {
        super.aiStep();

        // Crecimiento: se detiene si la criatura esta famelica.
        if (growthTicks < species().adultTicks() && getHunger() > 20) {
            growthTicks++;
        }

        GrowthStage stage = getStage();
        if (stage != lastStage) {
            lastStage = stage;
            refreshDimensions();
            if (!level().isClientSide) recalcStats();
        }
        int step = (int) (getBodyScale() * 50.0f);
        if (step != lastScaleStep) {
            lastScaleStep = step;
            refreshDimensions();
        }

        if (!level().isClientSide) {
            if (tameCooldown > 0) tameCooldown--;
            if (tickCount % 100 == 0) serverSlowTick();
        }
    }

    /** Se ejecuta cada 5 segundos: hambre, regeneracion, sincronizacion y perdida de confianza. */
    private void serverSlowTick() {
        this.entityData.set(DATA_GROWTH, growthTicks);

        if (getAction() != EntityAction.SLEEPING || tickCount % 200 == 0) {
            setHunger(getHunger() - 1);
        }
        if (getHunger() <= 0) {
            // La inanicion duele pero no mata a la criatura: se queda en 2 de vida.
            if (getHealth() > 2.0f) hurt(damageSources().starve(), 1.0f);
        } else if (getHunger() >= 50 && getHealth() < getMaxHealth()) {
            heal(1.0f);
        }
        if (!isTame() && getTameProgress() > 0 && tameCooldown == 0 && tickCount % 600 == 0) {
            setTameProgress(getTameProgress() - 5);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !level().isClientSide) {
            if (getAction() == EntityAction.SLEEPING) setAction(EntityAction.NONE);
            if (source.getEntity() instanceof LivingEntity attacker && attacker != this) {
                onAttacked(attacker);
            }
        }
        return result;
    }

    /** Reaccion al ser atacado. Por defecto, avisa a los de su especie para que reaccionen. */
    protected void onAttacked(LivingEntity attacker) {
        if (isTame()) return;
        double r = species().alertRadius();
        for (PrehistoricEntity other : level().getEntitiesOfClass(PrehistoricEntity.class,
                getBoundingBox().inflate(r, 6.0, r),
                e -> e != this && e.getType() == getType() && !e.isTame())) {
            other.setLastHurtByMob(attacker);
        }
    }

    // ------------------------------------------------------------------ domesticacion

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean client = level().isClientSide;

        if (isTame()) {
            if (isOwnedBy(player)) {
                if (isTamingFood(stack) && getHunger() < 100) {
                    if (!client) {
                        addHunger(species().foodValue());
                        heal(4.0f);
                        if (!player.getAbilities().instabuild) stack.shrink(1);
                        gameEvent(GameEvent.EAT);
                        level().broadcastEntityEvent(this, (byte) 7);
                    }
                    return InteractionResult.sidedSuccess(client);
                }
                if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                    if (!client) {
                        setOrderedToSit(!isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                        setTarget(null);
                    }
                    return InteractionResult.sidedSuccess(client);
                }
            }
            return super.mobInteract(player, hand);
        }

        if (isTamingFood(stack)) {
            if (!client) {
                if (tameCooldown > 0 || getLastHurtByMob() != null) {
                    return InteractionResult.CONSUME; // todavia desconfia
                }
                if (!player.getAbilities().instabuild) stack.shrink(1);
                feedForTaming(player);
            }
            return InteractionResult.sidedSuccess(client);
        }
        return super.mobInteract(player, hand);
    }

    private void feedForTaming(Player player) {
        int needed = species().feedingsToTame() + getCreatureLevel() / 10;
        if (isAlpha()) needed *= 2;
        int gain = Math.max(1, Mth.ceil(100.0f / needed));
        int progress = Math.min(100, getTameProgress() + gain);
        setTameProgress(progress);
        addHunger(species().foodValue());
        tameCooldown = 100;
        gameEvent(GameEvent.EAT);

        if (progress >= 100) {
            tame(player);
            this.navigation.stop();
            setTarget(null);
            setOrderedToSit(false);
            setInSittingPose(false);
            level().broadcastEntityEvent(this, (byte) 7);
        } else if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + getBbHeight() * 0.7, getZ(),
                    5, 0.3, 0.3, 0.3, 0.02);
        }
    }

    /** Se desactiva la cria por amor de vanilla: la reproduccion con huevos y nidos llegara en la v0.2. */
    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    // ------------------------------------------------------------------ interfaz sobre la criatura

    @Override
    public Component getDisplayName() {
        MutableComponent text = Component.empty();
        if (isAlpha()) {
            text.append(Component.translatable("prehistoricsurvival.label.alpha")).append(Component.literal(" "));
        }
        text.append(super.getDisplayName());
        text.append(Component.literal("  Lv." + getCreatureLevel()));
        text.append(Component.literal("  " + Math.round(getHealth()) + "/" + Math.round(getMaxHealth()) + " \u2665"));
        if (!isTame() && getTameProgress() > 0) {
            text.append(Component.literal("  "))
                    .append(Component.translatable("prehistoricsurvival.label.taming", getTameProgress()));
        }
        return text;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(net.minecraft.sounds.SoundEvents.CHICKEN_STEP, 0.15f, 1.0f);
    }

    // ------------------------------------------------------------------ guardado

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PSVersion", SAVE_VERSION);
        tag.putBoolean("Female", isFemale());
        tag.putBoolean("Alpha", isAlpha());
        tag.putInt("CreatureLevel", getCreatureLevel());
        tag.putInt("Variant", getVariant());
        tag.putInt("Growth", growthTicks);
        tag.putInt("Hunger", getHunger());
        tag.putInt("TameProgress", getTameProgress());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // tag.getInt("PSVersion") permitira migrar datos de versiones anteriores.
        setFemale(tag.getBoolean("Female"));
        setAlpha(tag.getBoolean("Alpha"));
        setCreatureLevel(tag.contains("CreatureLevel") ? tag.getInt("CreatureLevel") : 1);
        setVariant(Mth.clamp(tag.getInt("Variant"), 0, Math.max(0, variantCount() - 1)));
        this.growthTicks = tag.contains("Growth") ? tag.getInt("Growth") : species().adultTicks();
        this.entityData.set(DATA_GROWTH, growthTicks);
        this.lastStage = getStage();
        setHunger(tag.contains("Hunger") ? tag.getInt("Hunger") : 80);
        setTameProgress(tag.getInt("TameProgress"));
        recalcStats();
    }
}
