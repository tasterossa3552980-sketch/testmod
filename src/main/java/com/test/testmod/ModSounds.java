package com.test.testmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> Sound = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
            "testmod");

    public static final RegistryObject<SoundEvent> first =Sound.register(
            "first",()-> SoundEvent.createVariableRangeEvent(new ResourceLocation("testmod","first"))


    );
    public static void register(IEventBus bus) {
        Sound.register(bus);
    }

}
