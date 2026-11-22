package com.prehistoricsurvival.registry;

import com.prehistoricsurvival.PrehistoricSurvival;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PrehistoricSurvival.MOD_ID);

    public static final RegistryObject<Item> RAW_DINO_MEAT = ITEMS.register("raw_dino_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).meat().build())));

    public static final RegistryObject<Item> COOKED_DINO_MEAT = ITEMS.register("cooked_dino_meat",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).meat().build())));

    public static final RegistryObject<Item> DODO_FEATHER = ITEMS.register("dodo_feather",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DODO_SPAWN_EGG = ITEMS.register("dodo_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DODO, 0x7d6a55, 0xd8c26a, new Item.Properties()));

    private ModItems() {}
}
