package com.test.testmod;

import com.test.testmod.Item.itemRegisters;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    @SubscribeEvent
    public static void onInteractWithWolf (PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof Wolf wolf){
            Player p = event.getEntity();
            if(wolf.isTame()&&p.isShiftKeyDown()){

                if(!p.level().isClientSide){
                    ItemStack stack = new ItemStack(itemRegisters.big_dog.get());

                    if(wolf.hasCustomName()){
                        stack.setHoverName(wolf.getCustomName());
                    }

                    if(!p.getInventory().add(stack)){
                        p.drop(stack,false);

                    }
                    wolf.discard();
                }
                event.setCanceled(true);
            }
        }
    }
}

