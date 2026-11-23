package com.prehistoricsurvival;

import java.util.List;

import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Eventos de juego que conectan a las criaturas con el resto del mundo. */
@Mod.EventBusSubscriber(modid = PrehistoricSurvival.MOD_ID)
public final class CommonEvents {

    /**
     * Si un depredador salvaje mata a una presa, el botin no cae al suelo "como un cofre":
     * el depredador lo retiene, va a comer y despues deja huesos y restos (ver EatCarcassGoal).
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity dead = event.getEntity();
        if (dead.level().isClientSide || dead instanceof Player) return;
        if (!(event.getSource().getEntity() instanceof PrehistoricEntity hunter)
                || !hunter.isCarnivore() || hunter.isTame() || hunter.hasCarcass()) return;

        List<ItemStack> stacks = event.getDrops().stream().map(ItemEntity::getItem).map(ItemStack::copy).toList();
        hunter.setCarcass(dead.position(), (int) Mth.clamp(dead.getMaxHealth() * 3.0f, 10.0f, 80.0f), stacks);
        event.setCanceled(true);
    }

    /** Los combates hacen ruido: las criaturas curiosas (T. rex) cercanas acuden a investigar. */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity hurt = event.getEntity();
        if (hurt.level().isClientSide) return;
        for (PrehistoricEntity listener : hurt.level().getEntitiesOfClass(PrehistoricEntity.class,
                hurt.getBoundingBox().inflate(48.0, 16.0, 48.0),
                e -> e != hurt && e.investigatesNoise())) {
            listener.hearNoise(hurt.position());
        }
    }

    private CommonEvents() {}
}
