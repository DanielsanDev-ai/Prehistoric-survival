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

    public static final RegistryObject<SoundEvent> RAPTOR_AMBIENT = register("entity.velociraptor.ambient");
    public static final RegistryObject<SoundEvent> RAPTOR_WARN = register("entity.velociraptor.warn");
    public static final RegistryObject<SoundEvent> RAPTOR_ATTACK = register("entity.velociraptor.attack");
    public static final RegistryObject<SoundEvent> RAPTOR_HURT = register("entity.velociraptor.hurt");
    public static final RegistryObject<SoundEvent> RAPTOR_DEATH = register("entity.velociraptor.death");
    public static final RegistryObject<SoundEvent> RAPTOR_CALL = register("entity.velociraptor.call");
    public static final RegistryObject<SoundEvent> RAPTOR_CHICK = register("entity.velociraptor.chick");

    public static final RegistryObject<SoundEvent> REX_AMBIENT = register("entity.tyrannosaurus.ambient");
    public static final RegistryObject<SoundEvent> REX_WARN = register("entity.tyrannosaurus.warn");
    public static final RegistryObject<SoundEvent> REX_ATTACK = register("entity.tyrannosaurus.attack");
    public static final RegistryObject<SoundEvent> REX_HURT = register("entity.tyrannosaurus.hurt");
    public static final RegistryObject<SoundEvent> REX_DEATH = register("entity.tyrannosaurus.death");
    public static final RegistryObject<SoundEvent> REX_STEP = register("entity.tyrannosaurus.step");
    public static final RegistryObject<SoundEvent> REX_CHICK = register("entity.tyrannosaurus.chick");

    public static final RegistryObject<SoundEvent> DILO_AMBIENT = register("entity.dilophosaurus.ambient");
    public static final RegistryObject<SoundEvent> DILO_WARN = register("entity.dilophosaurus.warn");
    public static final RegistryObject<SoundEvent> DILO_ATTACK = register("entity.dilophosaurus.attack");
    public static final RegistryObject<SoundEvent> DILO_HURT = register("entity.dilophosaurus.hurt");
    public static final RegistryObject<SoundEvent> DILO_DEATH = register("entity.dilophosaurus.death");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(PrehistoricSurvival.MOD_ID, name)));
    }

    private ModSounds() {}
}
