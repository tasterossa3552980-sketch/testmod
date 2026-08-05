package com.test.testmod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.test.testmod.Item.itemRegisters;



@Mod("testmod")
public class testmod {
    public testmod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        itemRegisters.register(bus);
        ModItemTab.register(bus);
        ModSounds.register(bus);
    }

}
