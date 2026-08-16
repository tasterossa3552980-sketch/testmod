package com.test.testmod.registry;

import com.test.testmod.block.blockRegister;
import com.test.testmod.enchantment.enchantmentRegister;
import com.test.testmod.item.itemRegisters;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Properties;


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
                        output.accept(new ItemStack(blockRegister.DISPLAY_BLOCK.get()));
                    })
                    .build()

    );
    public static final RegistryObject<CreativeModeTab> TAB02 = ITEM_TEB.register(
            "item02",
            ()-> CreativeModeTab.builder()
                    .title(Component.translatable("item02"))
                    .icon(()-> {
                        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                        EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(Enchantments.SILK_TOUCH, 1));
                        return book;
                    })
                    .displayItems((parameters,output)->{
                        ItemStack orbitBook = new ItemStack(Items.ENCHANTED_BOOK);
                        ItemStack throwBook = new ItemStack(Items.ENCHANTED_BOOK);
                        EnchantedBookItem.addEnchantment(orbitBook, new EnchantmentInstance(enchantmentRegister.ORBIT.get(), 1));
                        output.accept(orbitBook);
                        EnchantedBookItem.addEnchantment(throwBook, new EnchantmentInstance(enchantmentRegister.THROW.get(), 1));
                        output.accept(throwBook);

                    })
                    .build()

    );

    public static void register(IEventBus bus) {
        ITEM_TEB.register(bus);

    }


}
