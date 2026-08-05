package com.test.testmod;

import com.test.testmod.Item.itemRegisters;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.RegistryObject;


public class ModItemTab {
    public static final DeferredRegister<CreativeModeTab> ITEM_TEB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "testmod");

    public static final RegistryObject<CreativeModeTab> TAB01 = ITEM_TEB.register(
            "item01",
            ()-> CreativeModeTab.builder()
                    .title(Component.translatable("item01"))
                    .icon(()->new ItemStack(itemRegisters.first.get()))
                    .displayItems((parameters,output)->{
                        output.accept(new ItemStack(itemRegisters.first.get()));
                        output.accept(new ItemStack(itemRegisters.FIRST_SWORD.get()));
                        output.accept(new ItemStack(itemRegisters.big_dog.get()));
                    })
                    .build()

    );

    public static void register(IEventBus bus) {
        ITEM_TEB.register(bus);

    }


}
