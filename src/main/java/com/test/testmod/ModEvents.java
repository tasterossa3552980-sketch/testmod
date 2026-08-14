package com.test.testmod;

import com.test.testmod.Enchantment.enchantmentRegister;
import com.test.testmod.Item.itemRegisters;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.animatable.GeoItem;
import net.minecraft.server.level.ServerLevel;

import java.util.*;
import java.util.Random;

import static net.minecraft.commands.arguments.EntityArgument.getEntity;

@Mod.EventBusSubscriber(modid="testmod")

public class ModEvents {
    private static boolean hasOrbitRod(Player p) {
        ItemStack mainHand = p.getMainHandItem();
        ItemStack offHand = p.getOffhandItem();

        boolean mainHandValid = mainHand.getItem() instanceof net.minecraft.world.item.FishingRodItem
                && mainHand.getEnchantmentLevel(enchantmentRegister.ORBIT.get()) > 0;

        boolean offHandValid = offHand.getItem() instanceof net.minecraft.world.item.FishingRodItem
                && offHand.getEnchantmentLevel(enchantmentRegister.ORBIT.get()) > 0;

        return mainHandValid || offHandValid;
    }
    private static final Map<UUID, Set<UUID>> orbitingEntities = new HashMap<>();
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
    @SubscribeEvent

    public static void onPlayerTickOrbit(TickEvent.PlayerTickEvent event) {
        Player p = event.player;


        if (p.level().isClientSide()) return;

        if(!hasOrbitRod(p))return;

        Set<UUID> myOrbiters = orbitingEntities.computeIfAbsent(p.getUUID(), k -> new HashSet<>());

        FishingHook hook = p.fishing;
        if (hook != null) {
            Entity e = hook.getHookedIn();
            if (e != null) {
                myOrbiters.add(e.getUUID());
            }
        }
        if (myOrbiters.isEmpty()) return;

        ServerLevel serverLevel = (ServerLevel) p.level();
        /*Random rand = new Random();
        int num1 = rand.nextInt(5, 10);*/
        double radius = 7;
        double ticksPerCircle = 10;
        double baseAngle = (p.level().getGameTime() % ticksPerCircle) / ticksPerCircle * Math.PI * 2;
        int total = myOrbiters.size();
        int index = 0;

        Iterator<UUID> iterator = myOrbiters.iterator();
        while (iterator.hasNext()) {
            UUID id = iterator.next();
            Entity entity = serverLevel.getEntity(id);
            if (entity == null || !entity.isAlive()) {
                iterator.remove();
                continue;
            }

            double angleOffset = (2 * Math.PI / total) * index;
            double angle = baseAngle + angleOffset;

            double targetX = p.getX() + Math.cos(angle) * radius;
            double targetZ = p.getZ() + Math.sin(angle) * radius;

            entity.teleportTo(targetX, p.getY(), targetZ);   // ← 這裡也要改
            entity.hurtMarked = true;
            index++;
        }
    }
    @SubscribeEvent
    public static void cancelOrbit(PlayerInteractEvent.RightClickItem event) {
        Player p = event.getEntity();
        if(p.level().isClientSide()) return;
        if(!hasOrbitRod(p))return;
        if(!p.isShiftKeyDown()){return;}
        Set<UUID> myOrbiters = orbitingEntities.get(p.getUUID());
        if(myOrbiters!=null){
            myOrbiters.clear();
        }
        event.setCanceled(true);
    }

}

