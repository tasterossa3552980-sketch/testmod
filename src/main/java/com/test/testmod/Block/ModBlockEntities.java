package com.test.testmod.Block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "testmod");
    public static final RegistryObject<BlockEntityType<DisplayBlockEntity>> DISPLAY_BLOCK_ENTITY =
            BLOCKS.register("display_stand",
                    () -> BlockEntityType.Builder.of(DisplayBlockEntity::new,
                            blockRegister.DISPLAY_BLOCK.get()).build(null));
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

}
