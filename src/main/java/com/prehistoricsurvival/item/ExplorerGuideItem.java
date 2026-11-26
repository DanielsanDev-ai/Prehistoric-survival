package com.prehistoricsurvival.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

/** Libro-enciclopedia del mod. Al usarlo se abre la interfaz del libro (solo en el cliente). */
public class ExplorerGuideItem extends Item {
    public ExplorerGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> com.prehistoricsurvival.client.guide.GuideClient.open());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
