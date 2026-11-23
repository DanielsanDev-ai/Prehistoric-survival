package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.block.DinoEggBlock;
import com.prehistoricsurvival.block.NestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PrehistoricSurvival.MOD_ID);

    public static final RegistryObject<Block> DINO_EGG = BLOCKS.register("dino_egg",
            () -> new DinoEggBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND).strength(0.5f).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> NEST = BLOCKS.register("nest",
            () -> new NestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW).strength(0.4f).sound(SoundType.GRASS).noOcclusion()));

    private ModBlocks() {}
}
