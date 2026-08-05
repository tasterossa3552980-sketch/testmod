package com.test.testmod;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="testmod")

public class ModEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player p =event.player;

        if(!p.level().isClientSide && p.onGround()) {
            CompoundTag persistentData = p.getPersistentData();
            persistentData.putInt("air",0);
        }
    }
}
