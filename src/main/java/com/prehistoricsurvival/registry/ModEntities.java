package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.DodoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PrehistoricSurvival.MOD_ID);

    public static final RegistryObject<EntityType<DodoEntity>> DODO = ENTITIES.register("dodo",
            () -> EntityType.Builder.of(DodoEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.2f)
                    .clientTrackingRange(10)
                    .build("dodo"));

    private ModEntities() {}
}
