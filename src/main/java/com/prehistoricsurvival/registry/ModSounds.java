package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PrehistoricSurvival.MOD_ID);

    public static final RegistryObject<SoundEvent> DODO_AMBIENT = register("entity.dodo.ambient");
    public static final RegistryObject<SoundEvent> DODO_HURT = register("entity.dodo.hurt");
    public static final RegistryObject<SoundEvent> DODO_DEATH = register("entity.dodo.death");
    public static final RegistryObject<SoundEvent> DODO_ALERT = register("entity.dodo.alert");
    public static final RegistryObject<SoundEvent> DODO_CHICK = register("entity.dodo.chick");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(PrehistoricSurvival.MOD_ID, name)));
    }

    private ModSounds() {}
}
