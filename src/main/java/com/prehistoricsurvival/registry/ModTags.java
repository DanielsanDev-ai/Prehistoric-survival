package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModTags {
    /** Criaturas de las que huyen los herbivoros pequenos. Editable por datapack. */
    public static final TagKey<EntityType<?>> PREDATORS =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(PrehistoricSurvival.MOD_ID, "predators"));

    private ModTags() {}
}
