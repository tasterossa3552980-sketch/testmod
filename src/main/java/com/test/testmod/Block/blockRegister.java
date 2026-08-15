package com.test.testmod.Block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class blockRegister {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "testmod");

    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "testmod");

    public static final RegistryObject<Block> DISPLAY_BLOCK = BLOCKS.register("display_stand",
            () -> new displayStand(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0F)
                    .noOcclusion()));
    // 在 blockRegister.java 中修改：
    public static final RegistryObject<Item> DISPLAY_BLOCK_ITEM = BLOCK_ITEMS.register("display_stand",
            () -> new com.test.testmod.Item.displayStand(DISPLAY_BLOCK.get(), new Item.Properties()));


    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
    }
}
