package com.prehistoricsurvival;

import com.prehistoricsurvival.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Entrega el libro una sola vez por jugador y mundo (tambien funciona en mundos ya creados). */
@Mod.EventBusSubscriber(modid = PrehistoricSurvival.MOD_ID)
public final class GuideBookEvents {
    private static final String RECEIVED_KEY = "PrehistoricSurvivalGuideBook_v3";

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(RECEIVED_KEY)) return;

        ItemStack book = new ItemStack(ModItems.FIELD_GUIDE.get());
        if (!player.getInventory().add(book)) player.drop(book, false);
        data.putBoolean(RECEIVED_KEY, true);
    }

    private GuideBookEvents() {}
}
