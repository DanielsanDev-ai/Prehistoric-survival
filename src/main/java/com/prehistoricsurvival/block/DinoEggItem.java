package com.prehistoricsurvival.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** Huevo en el inventario: se puede transportar, colocar en un nido o junto a una fuente de calor. */
public class DinoEggItem extends BlockItem {
    private final String speciesId;
    private final int incubationTicks;

    public DinoEggItem(Block block, Properties properties, String speciesId, int incubationTicks) {
        super(block, properties);
        this.speciesId = speciesId;
        this.incubationTicks = incubationTicks;
    }

    public String getSpeciesId() { return speciesId; }
    public int getIncubationTicks() { return incubationTicks; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        String id = speciesId;
        int lvl = 1;
        int percent = 0;
        if (tag != null) {
            if (tag.contains("Species")) id = tag.getString("Species");
            lvl = Math.max(1, tag.getInt("Level"));
            int total = Math.max(1, tag.getInt("Total"));
            percent = Math.min(99, tag.getInt("Progress") * 100 / total);
        }
        EntityType.byString(id).ifPresent(t -> tooltip.add(t.getDescription().copy().withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.translatable("prehistoricsurvival.tooltip.egg", lvl, percent).withStyle(ChatFormatting.DARK_GRAY));
    }
}
