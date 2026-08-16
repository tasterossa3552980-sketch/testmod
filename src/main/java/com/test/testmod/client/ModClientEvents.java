package com.test.testmod.client;

import com.test.testmod.network.LaunchTriggerPacket;
import com.test.testmod.network.NetworkHander;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "testmod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ModKeybindings.LAUNCH_KEY.consumeClick()) {
            NetworkHander.INSTANCE.sendToServer(new LaunchTriggerPacket());
        }
    }
}
