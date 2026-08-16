package com.test.testmod.registry;

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
    public static final RegistryObject<SoundEvent>  BIG_DOG_CHARGE =
            Sound.register("big_dog_charge", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation("testmod", "big_dog_charge")));
    public static final RegistryObject<SoundEvent> BIG_DOG_OWL =
            Sound.register("big_dog_owl", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation("testmod", "big_dog_owl")));
    public static final RegistryObject<SoundEvent> BIG_DOG_noOWL =
            Sound.register("big_dog_noowl", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation("testmod", "big_dog_noowl")));
    public static void register(IEventBus bus) {
        Sound.register(bus);
    }

}
