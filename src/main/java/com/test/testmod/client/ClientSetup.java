package com.test.testmod.client;

import com.test.testmod.block.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import com.test.testmod.block.displayStandRender;

@Mod.EventBusSubscriber(modid = "testmod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class ClientSetup {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

        });

    }
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DISPLAY_BLOCK_ENTITY.get(), displayStandRender::new);
    }
}
