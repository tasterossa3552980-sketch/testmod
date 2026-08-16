package com.test.testmod.client;

import com.test.testmod.block.ModBlockEntities;
import com.test.testmod.block.displayStandRender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "testmod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 必須將你的 BlockEntity 類型與渲染器綁定
        event.registerBlockEntityRenderer(ModBlockEntities.DISPLAY_BLOCK_ENTITY.get(), displayStandRender::new);
    }
}
