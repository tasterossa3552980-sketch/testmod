package com.test.testmod.Enchantment;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class enchantmentRegister {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "testmod");

    public static final RegistryObject<Enchantment> ORBIT = ENCHANTMENTS.register(
            "orbit",
            fishRodOrbit::new
    );

    public static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }


}
