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

    /** Nido de paja (dodo). */
    public static final RegistryObject<Block> NEST = BLOCKS.register("nest",
            () -> new NestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW).strength(0.4f).sound(SoundType.GRASS).noOcclusion()));

    /** Nido de barro (velociraptor y dilophosaurus). */
    public static final RegistryObject<Block> MUD_NEST = BLOCKS.register("mud_nest",
            () -> new NestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT).strength(0.5f).sound(SoundType.MUD).noOcclusion()));

    /** Monticulo de ramas y barro (tyrannosaurus). */
    public static final RegistryObject<Block> MOUND_NEST = BLOCKS.register("mound_nest",
            () -> new NestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PODZOL).strength(0.8f).sound(SoundType.PACKED_MUD).noOcclusion()));

    /** Nido preferido de una especie (por su id completo). */
    public static Block nestFor(String speciesId) {
        if (speciesId.endsWith("tyrannosaurus")) return MOUND_NEST.get();
        if (speciesId.endsWith("velociraptor") || speciesId.endsWith("dilophosaurus")) return MUD_NEST.get();
        return NEST.get();
    }

    private ModBlocks() {}
}
