package com.prehistoricsurvival.entity;

import com.prehistoricsurvival.registry.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

/**
 * Proyectil de veneno verdoso del dilophosaurus. Al impactar: dano leve, ralentizacion y ceguera temporales.
 * Deja una estela de particulas verdes y estalla en salpicaduras. El dilophosaurus lo lanza con un tiempo
 * de reutilizacion para no abusar de el.
 */
public class VenomSpitEntity extends ThrowableItemProjectile {
    private static final DustParticleOptions GREEN_DUST = new DustParticleOptions(new Vector3f(0.45f, 0.9f, 0.2f), 1.0f);

    private boolean potent;

    public VenomSpitEntity(EntityType<? extends VenomSpitEntity> type, Level level) {
        super(type, level);
    }

    public VenomSpitEntity(EntityType<? extends VenomSpitEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    /** Los alfas escupen un veneno mas potente. */
    public void setPotent(boolean potent) {
        this.potent = potent;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.VENOM_GLOB.get();
    }

    @Override
    protected float getGravity() {
        return 0.02f;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                level().addParticle(GREEN_DUST, getX() + (random.nextDouble() - 0.5) * 0.25,
                        getY() + (random.nextDouble() - 0.5) * 0.25, getZ() + (random.nextDouble() - 0.5) * 0.25, 0, 0, 0);
            }
            level().addParticle(ParticleTypes.ITEM_SLIME, getX(), getY(), getZ(), 0, 0, 0);
        } else if (tickCount > 100) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (!(target instanceof LivingEntity living) || target == owner) return;
        if (owner != null && target.getType() == owner.getType()) return;   // no daña a los de su especie

        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, potent ? 240 : 160, potent ? 2 : 1), owner);
        living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, potent ? 160 : 100, 0), owner);
        living.hurt(damageSources().mobProjectile(this, owner instanceof LivingEntity shooter ? shooter : null),
                potent ? 4.0f : 2.0f);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            if (level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.ITEM_SLIME, getX(), getY(), getZ(), 18, 0.3, 0.3, 0.3, 0.1);
                server.sendParticles(GREEN_DUST, getX(), getY(), getZ(), 12, 0.35, 0.3, 0.35, 0.02);
            }
            playSound(SoundEvents.SLIME_SQUISH_SMALL, 1.0f, 0.8f);
            discard();
        }
    }
}
