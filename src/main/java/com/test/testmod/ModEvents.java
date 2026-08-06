package com.test.testmod;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.animatable.GeoItem;

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
    @SubscribeEvent
    public static void onItemEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.item.ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            if (stack.getItem() instanceof GeoItem) {
                GeoItem.getOrAssignId(stack, (net.minecraft.server.level.ServerLevel) event.getLevel());
            }
        }
    }
}

