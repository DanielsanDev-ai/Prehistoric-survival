package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.DodoEntity;
import com.prehistoricsurvival.entity.DilophosaurusEntity;
import com.prehistoricsurvival.entity.TyrannosaurusEntity;
import com.prehistoricsurvival.entity.VelociraptorEntity;
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
                    .sized(0.5f, 1.0f)
                    .clientTrackingRange(10)
                    .build("dodo"));

    /** Velociraptor real: ~0,5 m de cadera. */
    public static final RegistryObject<EntityType<VelociraptorEntity>> VELOCIRAPTOR = ENTITIES.register("velociraptor",
            () -> EntityType.Builder.of(VelociraptorEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 0.9f)
                    .clientTrackingRange(10)
                    .build("velociraptor"));

    /** Tyrannosaurus real: ~3 m de cadera, ~5 m con la cabeza erguida. */
    public static final RegistryObject<EntityType<TyrannosaurusEntity>> TYRANNOSAURUS = ENTITIES.register("tyrannosaurus",
            () -> EntityType.Builder.of(TyrannosaurusEntity::new, MobCategory.CREATURE)
                    .sized(2.2f, 4.4f)
                    .clientTrackingRange(16)
                    .build("tyrannosaurus"));

    /** Dilophosaurus mediano, integrado con crianza y domesticacion. */
    public static final RegistryObject<EntityType<DilophosaurusEntity>> DILOPHOSAURUS = ENTITIES.register("dilophosaurus",
            () -> EntityType.Builder.of(DilophosaurusEntity::new, MobCategory.CREATURE)
                    .sized(0.85f, 1.45f)
                    .clientTrackingRange(12)
                    .build("dilophosaurus"));

    private ModEntities() {}
}
