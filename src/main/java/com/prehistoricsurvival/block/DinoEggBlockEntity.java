package com.prehistoricsurvival.block;

import java.util.UUID;

import javax.annotation.Nullable;

import com.prehistoricsurvival.entity.PrehistoricEntity;
import com.prehistoricsurvival.registry.ModBlockEntities;
import com.prehistoricsurvival.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Datos y logica de incubacion de un huevo.
 * Solo avanza si el huevo esta en un bioma calido o cerca de una fuente de calor (hoguera, horno, magma).
 * Un nido debajo la acelera (mas si es el tipo de nido preferido de la especie) y el calor extra, aun mas.
 * Efectos: motas verdes mientras incuba, copos si esta demasiado frio, grietas y sonidos al avanzar.
 */
public class DinoEggBlockEntity extends BlockEntity {
    private String species = "";
    private int progress;
    private int total;
    private int creatureLevel = 1;
    private int variant;
    private boolean female;
    private boolean alpha;
    @Nullable private UUID owner;
    @Nullable private UUID mother;

    public DinoEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DINO_EGG.get(), pos, state);
    }

    public void setup(String species, int total, int creatureLevel, int variant, boolean female, boolean alpha,
                      @Nullable UUID owner, @Nullable UUID mother) {
        this.species = species;
        this.total = Math.max(1, total);
        this.creatureLevel = creatureLevel;
        this.variant = variant;
        this.female = female;
        this.alpha = alpha;
        this.owner = owner;
        this.mother = mother;
        setChanged();
        if (level != null && getBlockState().hasProperty(DinoEggBlock.KIND)) {
            level.setBlock(worldPosition, getBlockState().setValue(DinoEggBlock.KIND, DinoEggBlock.kindOf(species)), 3);
        }
    }

    /** Si el huevo no tiene datos (p. ej. colocado con un comando), se deducen de su aspecto. */
    private void ensureInit() {
        if (!species.isEmpty() && total > 0) return;
        int kind = getBlockState().hasProperty(DinoEggBlock.KIND) ? getBlockState().getValue(DinoEggBlock.KIND) : 0;
        if (species.isEmpty()) species = DinoEggBlock.KIND_SPECIES[Math.min(kind, DinoEggBlock.KIND_SPECIES.length - 1)];
        if (total <= 0) total = DinoEggBlock.KIND_INCUBATION[Math.min(kind, DinoEggBlock.KIND_INCUBATION.length - 1)];
    }

    public String getSpecies() {
        ensureInit();
        return species;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DinoEggBlockEntity egg) {
        if (level.getGameTime() % 20L != 0L || !(level instanceof ServerLevel server)) return;
        egg.ensureInit();
        egg.incubate(server, pos, state);
    }

    private void incubate(ServerLevel level, BlockPos pos, BlockState state) {
        float mult = incubationMultiplier(level, pos);
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.6, cz = pos.getZ() + 0.5;
        if (mult <= 0f) {   // demasiado frio: no avanza
            level.sendParticles(ParticleTypes.SNOWFLAKE, cx, cy, cz, 2, 0.15, 0.1, 0.15, 0.01);
            return;
        }
        progress += (int) (20 * mult);
        setChanged();
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, cx, cy, cz, mult > 1.4f ? 3 : 1, 0.15, 0.1, 0.15, 0.0);
        if (mult > 1.4f) level.sendParticles(ParticleTypes.SMALL_FLAME, cx, cy - 0.2, cz, 1, 0.2, 0.0, 0.2, 0.0);

        if (progress >= total) {
            hatch(level, pos, state);
            return;
        }
        int stage = Math.min(2, progress * 3 / total);
        if (stage != state.getValue(DinoEggBlock.HATCH)) {
            level.setBlock(pos, state.setValue(DinoEggBlock.HATCH, stage), 3);
            level.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7f, 0.9f);
            level.sendParticles(ParticleTypes.POOF, cx, cy, cz, 4, 0.15, 0.1, 0.15, 0.01);
        } else if (stage == 2 && level.random.nextInt(3) == 0) {   // a punto de eclosionar: cruje
            level.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.4f, 1.3f);
        }
    }

    private float incubationMultiplier(ServerLevel level, BlockPos pos) {
        boolean warm = level.getBiome(pos).value().getBaseTemperature() >= 0.7f;
        boolean heat = nearHeat(level, pos);
        if (!warm && !heat) return 0f;
        float mult = heat ? 1.5f : 1.0f;
        BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof NestBlock) {
            mult *= below.getBlock() == ModBlocks.nestFor(species) ? 1.5f : 1.25f;
        }
        return mult;
    }

    private static boolean nearHeat(Level level, BlockPos pos) {
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))) {
            BlockState s = level.getBlockState(p);
            if (s.getBlock() instanceof CampfireBlock && s.getValue(CampfireBlock.LIT)) return true;
            if (s.getBlock() instanceof AbstractFurnaceBlock && s.getValue(AbstractFurnaceBlock.LIT)) return true;
            if (s.is(Blocks.MAGMA_BLOCK) || s.is(BlockTags.FIRE)) return true;
        }
        return false;
    }

    private void hatch(ServerLevel level, BlockPos pos, BlockState state) {
        level.removeBlock(pos, false);
        EntityType.byString(species).ifPresent(type -> {
            Entity entity = type.create(level);
            if (entity instanceof PrehistoricEntity baby) {
                baby.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360f, 0f);
                baby.initFromGenes(creatureLevel, variant, female, alpha);
                baby.setParent(mother);
                if (owner != null) {
                    baby.setTame(true);
                    baby.setOwnerUUID(owner);
                }
                level.addFreshEntity(baby);
            }
        });
        level.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.9f, 1.0f);
        level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 12, 0.3, 0.2, 0.3, 0.03);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Species", species);
        tag.putInt("Progress", progress);
        tag.putInt("Total", total);
        tag.putInt("Level", creatureLevel);
        tag.putInt("Variant", variant);
        tag.putBoolean("Female", female);
        tag.putBoolean("Alpha", alpha);
        if (owner != null) tag.putUUID("Owner", owner);
        if (mother != null) tag.putUUID("Mother", mother);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        species = tag.contains("Species") ? tag.getString("Species") : "";
        progress = tag.getInt("Progress");
        total = tag.getInt("Total");
        creatureLevel = Math.max(1, tag.getInt("Level"));
        variant = tag.getInt("Variant");
        female = tag.getBoolean("Female");
        alpha = tag.getBoolean("Alpha");
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        mother = tag.hasUUID("Mother") ? tag.getUUID("Mother") : null;
    }
}
