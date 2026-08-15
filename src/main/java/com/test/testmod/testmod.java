package com.test.testmod;

import com.test.testmod.Block.ModBlockEntities;
import com.test.testmod.Block.blockRegister;
import com.test.testmod.Enchantment.enchantmentRegister;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.test.testmod.Item.itemRegisters;



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
