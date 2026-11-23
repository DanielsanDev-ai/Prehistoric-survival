package com.prehistoricsurvival.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import com.prehistoricsurvival.block.DinoEggBlockEntity;
import com.prehistoricsurvival.registry.ModBlocks;
import com.prehistoricsurvival.registry.ModItems;

/**
 * Base de todas las criaturas del mod. Aqui vive lo comun a todas las especies:
 * genero, nivel, alfa, variante de color, crecimiento por etapas, hambre,
 * domesticacion progresiva y guardado de datos.
 */
public abstract class PrehistoricEntity extends TamableAnimal {
    /** Version del formato de datos guardados. Subir al cambiar el formato y migrar en readAdditionalSaveData. */
    public static final int SAVE_VERSION = 2;

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

    // reproduccion
    private int breedCooldown;
    private int pregnantTicks = -1;   // -1 = no gestante, 0 = lista para poner el huevo
    private int fatherLevel = 1;
    private int fatherVariant;
    private boolean fatherAlpha;

    // sedacion, nido y ruido
    private int sedatedTicks;
    @Nullable private BlockPos nestPos;
    @Nullable private Vec3 noisePos;
    private int noiseCooldown;

    // carnivoros: presa recien abatida que esta comiendo
    @Nullable private Vec3 carcassPos;
    private int carcassFood;
    private int carcassTicks;
    private final List<ItemStack> carcassItems = new ArrayList<>();

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
        float b = s.babyScale();
        if (growthTicks < s.babyTicks()) {
            scale = Mth.lerp(growthTicks / (float) Math.max(1, s.babyTicks()), b * 0.65f, b);
        } else if (growthTicks < s.adultTicks()) {
            scale = Mth.lerp((growthTicks - s.babyTicks()) / (float) Math.max(1, s.juvenileTicks()), b * 0.93f, 1.0f);
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
            if (breedCooldown > 0) breedCooldown--;
            if (pregnantTicks > 0) pregnantTicks--;
            if (noiseCooldown > 0) noiseCooldown--;
            if (sedatedTicks > 0 && --sedatedTicks == 0) wakeFromSedation();
            if (carcassPos != null && ++carcassTicks > 1200) consumeCarcass(1.0f); // nadie vino a comer: se suelta el botin
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

        TameMethod method = tameMethod();
        if (method == TameMethod.SEDATE && stack.is(ModItems.SEDATIVE_MEAT.get())) {
            if (!client) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                sedate(900);
                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.EFFECT, getX(), getY() + getBbHeight() * 0.8, getZ(),
                            12, 0.5, 0.5, 0.5, 0.05);
                }
            }
            return InteractionResult.sidedSuccess(client);
        }
        if (isTamingFood(stack)) {
            if (!client) {
                if (getLastHurtByMob() != null || getTarget() == player) return InteractionResult.CONSUME;
                if (method == TameMethod.SEDATE && !isSedated()) {
                    player.displayClientMessage(Component.translatable("prehistoricsurvival.message.needs_sedation"), true);
                    return InteractionResult.CONSUME;
                }
                if (method == TameMethod.STEALTH && !player.isShiftKeyDown()) {
                    player.displayClientMessage(Component.translatable("prehistoricsurvival.message.too_wary"), true);
                    setTameProgress(getTameProgress() - 5);
                    playWarnSound();
                    return InteractionResult.CONSUME;
                }
                if (tameCooldown > 0) return InteractionResult.CONSUME;
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
        tameCooldown = tameMethod() == TameMethod.SEDATE ? 60 : 100;
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
        tag.putInt("BreedCooldown", breedCooldown);
        tag.putInt("Sedated", sedatedTicks);
        if (nestPos != null) {
            tag.putInt("NestX", nestPos.getX());
            tag.putInt("NestY", nestPos.getY());
            tag.putInt("NestZ", nestPos.getZ());
        }
        tag.putInt("Pregnant", pregnantTicks);
        tag.putInt("FatherLevel", fatherLevel);
        tag.putInt("FatherVariant", fatherVariant);
        tag.putBoolean("FatherAlpha", fatherAlpha);
        if (carcassPos != null) {
            CompoundTag c = new CompoundTag();
            c.putDouble("X", carcassPos.x);
            c.putDouble("Y", carcassPos.y);
            c.putDouble("Z", carcassPos.z);
            c.putInt("Food", carcassFood);
            ListTag items = new ListTag();
            for (ItemStack stack : carcassItems) items.add(stack.save(new CompoundTag()));
            c.put("Items", items);
            tag.put("Carcass", c);
        }
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
        breedCooldown = tag.getInt("BreedCooldown");
        sedatedTicks = tag.getInt("Sedated");
        nestPos = tag.contains("NestX") ? new BlockPos(tag.getInt("NestX"), tag.getInt("NestY"), tag.getInt("NestZ")) : null;
        pregnantTicks = tag.contains("Pregnant") ? tag.getInt("Pregnant") : -1;
        fatherLevel = tag.contains("FatherLevel") ? tag.getInt("FatherLevel") : 1;
        fatherVariant = tag.getInt("FatherVariant");
        fatherAlpha = tag.getBoolean("FatherAlpha");
        carcassPos = null;
        carcassItems.clear();
        if (tag.contains("Carcass", Tag.TAG_COMPOUND)) {
            CompoundTag c = tag.getCompound("Carcass");
            carcassPos = new Vec3(c.getDouble("X"), c.getDouble("Y"), c.getDouble("Z"));
            carcassFood = c.getInt("Food");
            ListTag items = c.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < items.size(); i++) carcassItems.add(ItemStack.of(items.getCompound(i)));
        }
        recalcStats();
    }

    // ------------------------------------------------------------------ reproduccion

    public boolean isPregnant() { return pregnantTicks >= 0; }
    public boolean readyToLay() { return pregnantTicks == 0; }

    /** Adulto sano, bien alimentado, descansado y libre. */
    public boolean canMate() {
        return isAdult() && !isPregnant() && breedCooldown <= 0 && getHunger() >= 70
                && getAction() == EntityAction.NONE && !isOrderedToSit()
                && getHealth() > getMaxHealth() * 0.5f;
    }

    public void startPregnancy(PrehistoricEntity father) {
        this.pregnantTicks = species().gestationTicks();
        this.fatherLevel = father.getCreatureLevel();
        this.fatherVariant = father.getVariant();
        this.fatherAlpha = father.isAlpha();
    }

    public void finishLaying() { this.pregnantTicks = -1; }
    public void startBreedCooldown() { this.breedCooldown = species().breedCooldownTicks(); }

    /** Pone un bloque-huevo con la genetica de los padres. No entrega nada al inventario. */
    public boolean layEgg(BlockPos pos) {
        if (!(level() instanceof ServerLevel server) || !server.getBlockState(pos).isAir()) return false;
        RandomSource r = getRandom();
        int lvl = Mth.clamp((getCreatureLevel() + fatherLevel) / 2 + r.nextInt(6) - 2, 1, 30);
        boolean alpha = r.nextInt((isAlpha() || fatherAlpha) ? 12 : 120) == 0;
        int variant = r.nextInt(10) < 9 ? (r.nextBoolean() ? getVariant() : fatherVariant) : r.nextInt(variantCount());
        boolean female = r.nextBoolean();

        server.setBlock(pos, ModBlocks.DINO_EGG.get().defaultBlockState()
                .setValue(com.prehistoricsurvival.block.DinoEggBlock.KIND,
                        com.prehistoricsurvival.block.DinoEggBlock.kindOf(EntityType.getKey(getType()).toString())), 3);
        if (server.getBlockEntity(pos) instanceof DinoEggBlockEntity egg) {
            egg.setup(EntityType.getKey(getType()).toString(), species().incubationTicks(),
                    lvl, variant, female, alpha, isTame() ? getOwnerUUID() : null);
        }
        this.nestPos = pos.immutable();
        return true;
    }

    /** Inicializa una cria recien eclosionada. */
    public void initFromGenes(int lvl, int variant, boolean female, boolean alpha) {
        setCreatureLevel(lvl);
        setVariant(Mth.clamp(variant, 0, Math.max(0, variantCount() - 1)));
        setFemale(female);
        setAlpha(alpha);
        this.growthTicks = 0;
        this.entityData.set(DATA_GROWTH, 0);
        this.lastStage = getStage();
        setHunger(70);
        recalcStats();
        setHealth(getMaxHealth());
        refreshDimensions();
    }

    // ------------------------------------------------------------------ carnivoros

    public boolean isCarnivore() { return species().carnivore(); }
    public boolean hasCarcass() { return carcassPos != null; }
    @Nullable public Vec3 getCarcassPos() { return carcassPos; }
    public int getCarcassFood() { return carcassFood; }

    public void setCarcass(Vec3 pos, int food, List<ItemStack> items) {
        this.carcassPos = pos;
        this.carcassFood = food;
        this.carcassTicks = 0;
        this.carcassItems.clear();
        this.carcassItems.addAll(items);
    }

    /**
     * Termina con la presa. Suelta la fraccion indicada de los objetos de la presa; si es menor que 1
     * (se la comio) ademas deja algunos huesos.
     */
    public void consumeCarcass(float leftoverFraction) {
        if (carcassPos == null) return;
        Level level = level();
        for (ItemStack stack : carcassItems) {
            int count = Mth.ceil(stack.getCount() * leftoverFraction);
            if (count > 0) {
                ItemStack drop = stack.copy();
                drop.setCount(count);
                level.addFreshEntity(new ItemEntity(level, carcassPos.x, carcassPos.y + 0.2, carcassPos.z, drop));
            }
        }
        if (leftoverFraction < 1.0f) {
            level.addFreshEntity(new ItemEntity(level, carcassPos.x, carcassPos.y + 0.2, carcassPos.z,
                    new ItemStack(Items.BONE, 1 + getRandom().nextInt(2))));
        }
        carcassItems.clear();
        carcassPos = null;
        carcassFood = 0;
        carcassTicks = 0;
    }

    /** Si muere con una presa a medias, el botin no se pierde. */
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && hasCarcass()) consumeCarcass(1.0f);
    }

    // ------------------------------------------------------------------ ganchos de sonido de comportamiento

    /** Sonido de advertencia ante un intruso. */
    public void playWarnSound() {}

    /** Llamada para avisar a la manada al empezar una caza. */
    public void playPackCall() {}

    // ------------------------------------------------------------------ domesticacion, sedacion, nido, ruido

    /** Metodo de domesticacion propio de la especie. */
    public TameMethod tameMethod() { return TameMethod.FEED; }

    /** Si es true, la criatura acude a investigar los ruidos de combate cercanos. */
    public boolean investigatesNoise() { return false; }

    public boolean isSedated() { return sedatedTicks > 0; }

    public void sedate(int ticks) {
        this.sedatedTicks = Math.min(2400, sedatedTicks + ticks);
        setAction(EntityAction.SLEEPING);
        setTarget(null);
        this.navigation.stop();
    }

    private void wakeFromSedation() {
        setAction(EntityAction.NONE);
        if (!isTame()) {
            setTameProgress(Math.max(0, getTameProgress() - 30));   // se despierta furioso y desconfiado
            Player nearest = level().getNearestPlayer(this, 12.0);
            if (nearest != null && !nearest.isCreative() && !nearest.isSpectator()) setTarget(nearest);
        }
    }

    public boolean hasNest() { return nestPos != null; }
    @Nullable public BlockPos getNestPos() { return nestPos; }
    public void clearNest() { this.nestPos = null; }

    /** Un ruido de combate cercano: la criatura (si es curiosa) ira a investigar. */
    public void hearNoise(Vec3 pos) {
        if (noiseCooldown > 0 || isTame() || getAction() != EntityAction.NONE || getTarget() != null) return;
        this.noisePos = pos;
        this.noiseCooldown = 600;
    }
    @Nullable public Vec3 getNoisePos() { return noisePos; }
    public void clearNoise() { this.noisePos = null; }

    /** Los alfas sueltan siempre un recurso especial. */
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (isAlpha()) spawnAtLocation(ModItems.ALPHA_ESSENCE.get());
    }
}
