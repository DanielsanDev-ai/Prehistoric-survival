package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.block.DinoEggBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PrehistoricSurvival.MOD_ID);

    public static final RegistryObject<BlockEntityType<DinoEggBlockEntity>> DINO_EGG = BLOCK_ENTITIES.register("dino_egg",
            () -> BlockEntityType.Builder.of(DinoEggBlockEntity::new, ModBlocks.DINO_EGG.get()).build(null));

    private ModBlockEntities() {}
}
