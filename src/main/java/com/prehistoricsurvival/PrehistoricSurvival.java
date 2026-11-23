package com.prehistoricsurvival;

import com.prehistoricsurvival.registry.ModBlockEntities;
import com.prehistoricsurvival.registry.ModBlocks;
import com.prehistoricsurvival.registry.ModCreativeTabs;
import com.prehistoricsurvival.registry.ModEntities;
import com.prehistoricsurvival.registry.ModItems;
import com.prehistoricsurvival.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PrehistoricSurvival.MOD_ID)
public class PrehistoricSurvival {
    public static final String MOD_ID = "prehistoricsurvival";

    public PrehistoricSurvival() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSounds.SOUNDS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModItems.ITEMS.register(bus);
        ModCreativeTabs.TABS.register(bus);
    }
}
