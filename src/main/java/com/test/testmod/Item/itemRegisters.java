package com.test.testmod.Item;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class itemRegisters {

    public static final DeferredRegister<Item>  ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "testmod");

    public static final RegistryObject<Item> FIRST_SWORD = ITEMS.register("first_sword",
            ()-> new firstSword(Tiers.IRON ,3,-2.4F,new Item.Properties()));

    public static final RegistryObject<Item> first = ITEMS.register("first",
            () -> new first(new Item.Properties().durability(64)));

    public static final RegistryObject<Item> big_dog =ITEMS.register("big_dog",
            ()->new bigDog(new Item.Properties().durability(256)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }


}
