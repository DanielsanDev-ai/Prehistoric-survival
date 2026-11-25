package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.block.DinoEggItem;
import com.prehistoricsurvival.item.GuideBookItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PrehistoricSurvival.MOD_ID);

    // ---- enciclopedia
    public static final RegistryObject<Item> FIELD_GUIDE = ITEMS.register("field_guide",
            () -> new GuideBookItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    // ---- carne
    public static final RegistryObject<Item> RAW_DINO_MEAT = ITEMS.register("raw_dino_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).meat().build())));
    public static final RegistryObject<Item> COOKED_DINO_MEAT = ITEMS.register("cooked_dino_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).meat().build())));
    /** Mezcla sedante: se le da a un T. rex para dormirlo y poder domesticarlo. No es comestible. */
    public static final RegistryObject<Item> SEDATIVE_MEAT = ITEMS.register("sedative_meat",
            () -> new Item(new Item.Properties()));

    // ---- recursos unicos de cada especie
    public static final RegistryObject<Item> DODO_FEATHER = ITEMS.register("dodo_feather",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAPTOR_CLAW = ITEMS.register("raptor_claw",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REX_TOOTH = ITEMS.register("rex_tooth",
            () -> new Item(new Item.Properties()));
    /** Recurso especial que sueltan los alfas de cualquier especie. */
    public static final RegistryObject<Item> ALPHA_ESSENCE = ITEMS.register("alpha_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    // ---- nidos y huevos
    public static final RegistryObject<Item> NEST = ITEMS.register("nest",
            () -> new BlockItem(ModBlocks.NEST.get(), new Item.Properties()));
    public static final RegistryObject<Item> DODO_EGG = ITEMS.register("dodo_egg",
            () -> new DinoEggItem(ModBlocks.DINO_EGG.get(), new Item.Properties().stacksTo(8),
                    "prehistoricsurvival:dodo", 9000));
    public static final RegistryObject<Item> VELOCIRAPTOR_EGG = ITEMS.register("velociraptor_egg",
            () -> new DinoEggItem(ModBlocks.DINO_EGG.get(), new Item.Properties().stacksTo(8),
                    "prehistoricsurvival:velociraptor", 12000));
    public static final RegistryObject<Item> TYRANNOSAURUS_EGG = ITEMS.register("tyrannosaurus_egg",
            () -> new DinoEggItem(ModBlocks.DINO_EGG.get(), new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                    "prehistoricsurvival:tyrannosaurus", 18000));

    public static final RegistryObject<Item> DILOPHOSAURUS_EGG = ITEMS.register("dilophosaurus_egg",
            () -> new DinoEggItem(ModBlocks.DINO_EGG.get(), new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON),
                    "prehistoricsurvival:dilophosaurus", 15000));

    // ---- huevos generadores
    public static final RegistryObject<Item> DODO_SPAWN_EGG = ITEMS.register("dodo_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DODO, 0x7d6a55, 0xd8c26a, new Item.Properties()));
    public static final RegistryObject<Item> VELOCIRAPTOR_SPAWN_EGG = ITEMS.register("velociraptor_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.VELOCIRAPTOR, 0x5f6e40, 0xc8552f, new Item.Properties()));
    public static final RegistryObject<Item> TYRANNOSAURUS_SPAWN_EGG = ITEMS.register("tyrannosaurus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.TYRANNOSAURUS, 0x4b5a3a, 0x9a8a6a, new Item.Properties()));

    public static final RegistryObject<Item> DILOPHOSAURUS_SPAWN_EGG = ITEMS.register("dilophosaurus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DILOPHOSAURUS, 0x66734a, 0xc4a75b, new Item.Properties()));

    /** Objeto huevo correspondiente a una especie (para conservar el huevo al recogerlo). */
    public static Item eggFor(String speciesId) {
        if (speciesId.endsWith("velociraptor")) return VELOCIRAPTOR_EGG.get();
        if (speciesId.endsWith("tyrannosaurus")) return TYRANNOSAURUS_EGG.get();
        if (speciesId.endsWith("dilophosaurus")) return DILOPHOSAURUS_EGG.get();
        return DODO_EGG.get();
    }

    private ModItems() {}
}
