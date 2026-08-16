package com.test.testmod;

import com.test.testmod.block.ModBlockEntities;
import com.test.testmod.block.blockRegister;
import com.test.testmod.enchantment.enchantmentRegister;
import com.test.testmod.network.NetworkHander;
import com.test.testmod.registry.ModItemTab;
import com.test.testmod.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.test.testmod.item.itemRegisters;



@Mod("testmod")
public class testmod {
    public testmod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        itemRegisters.register(bus);
        ModBlockEntities.register(bus);
        enchantmentRegister.register(bus);
        ModItemTab.register(bus);
        ModSounds.register(bus);
        blockRegister.register(bus);
        NetworkHander.register();
    }

}
