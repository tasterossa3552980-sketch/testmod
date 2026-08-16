package com.test.testmod.event;

import com.test.testmod.enchantment.enchantmentRegister;
import com.test.testmod.item.itemRegisters;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
    private static boolean isOrbiting(UUID entityId) {
        for (Set<UUID> orbiters : orbitingEntities.values()) {
            if (orbiters.contains(entityId)) {
                return true;
            }
        }
        return false;
    }
    @SubscribeEvent
    public static void onOrbitingEntityHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!event.getSource().is(DamageTypes.FALL) && !event.getSource().is(DamageTypes.IN_WALL)) return;

        if (isOrbiting(event.getEntity().getUUID())) {
            event.setCanceled(true);
        }
    }
    public static final Map<UUID, Set<UUID>> orbitingEntities = new HashMap<>();
    public static final Map<UUID, UUID> launchedEntities = new HashMap<>();
    public static final Map<UUID, List<UUID>> extraFishingHooks = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player p =event.player;

        if(!p.level().isClientSide && p.onGround()) {
            CompoundTag persistentData = p.getPersistentData();
            persistentData.putInt("air",0);
        }
    }
    @SubscribeEvent
    public static void onLaunchedEntityTick(TickEvent.PlayerTickEvent event) {
        Player p = event.player;
        if (p.level().isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) p.level();
        Iterator<Map.Entry<UUID, UUID>> iterator = launchedEntities.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, UUID> entry = iterator.next();
            Entity entity = serverLevel.getEntity(entry.getKey());

            if (entity == null || !entity.isAlive()) {
                iterator.remove();
                continue;
            }

            // 判斷有沒有撞到方塊(水平方向被卡住，代表撞牆了)
            if (entity.horizontalCollision) {
                entity.hurt(entity.damageSources().flyIntoWall(), 4.0F);   // 造成4點傷害
                iterator.remove();
                continue;
            }

            // 也可以判斷有沒有撞到其他生物
            List<Entity> nearby = serverLevel.getEntities(entity, entity.getBoundingBox().inflate(0.3),
                    e -> e != entity && e instanceof net.minecraft.world.entity.LivingEntity);

            if (!nearby.isEmpty()) {
                entity.hurt(entity.damageSources().mobAttack((net.minecraft.world.entity.LivingEntity) nearby.get(0)), 4.0F);
                iterator.remove();
            }
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
        int chickenCount = 0;


        if (p.level().isClientSide()) return;

        if(!hasOrbitRod(p))return;

        Set<UUID> myOrbiters = orbitingEntities.computeIfAbsent(p.getUUID(), k -> new LinkedHashSet<>());
        ServerLevel serverLevel = (ServerLevel) p.level();

        FishingHook hook = p.fishing;
        if (hook != null) {
            Entity e = hook.getHookedIn();
            if (e != null) {
                myOrbiters.add(e.getUUID());
            }
        }

        // 多勾附魔額外拋出的魚線，各自釣到的東西也要一起環繞
        List<UUID> multiHooks = extraFishingHooks.get(p.getUUID());
        if (multiHooks != null) {
            for (UUID hookId : multiHooks) {
                Entity hookEntity = serverLevel.getEntity(hookId);
                if (hookEntity instanceof FishingHook multiHook) {
                    Entity hooked = multiHook.getHookedIn();
                    if (hooked != null) {
                        myOrbiters.add(hooked.getUUID());
                    }
                }
            }
        }

        if (myOrbiters.isEmpty()) return;

        /*Random rand = new Random();
        int num1 = rand.nextInt(5, 10);*/
        double radius = 7;
        double ticksPerCircle = 20;
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
            if (entity instanceof net.minecraft.world.entity.animal.Chicken) {
                chickenCount++;
            }

            double angleOffset = (2 * Math.PI / total) * index;
            double angle = baseAngle + angleOffset;

            double targetX = p.getX() + Math.cos(angle) * radius;
            double targetZ = p.getZ() + Math.sin(angle) * radius;

            entity.teleportTo(targetX, p.getY(), targetZ);   // ← 這裡也要改
            entity.hurtMarked = true;
            index++;
            if (chickenCount >= 10) {
                p.setDeltaMovement(p.getDeltaMovement().x, 0.05, p.getDeltaMovement().z);
                p.hurtMarked = true;
                p.fallDistance = 0;   // 避免之後掉落時被誤判摔落傷害(跟之前教滯空一樣的道理)

            }

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
    public static final Map<UUID, UUID> selectedForLaunch = new HashMap<>();

    public static void selectFirstOrbiter(ServerPlayer p) {
        Set<UUID> myOrbiters = orbitingEntities.get(p.getUUID());
        if (myOrbiters == null || myOrbiters.isEmpty()) return;

        UUID firstId = myOrbiters.iterator().next();   // 最早加入的那一隻(因為改用LinkedHashSet)
        selectedForLaunch.put(p.getUUID(), firstId);
        myOrbiters.remove(firstId);   // 從環繞清單移除，避免跟固定位置邏輯打架
    }

    public static void launchSelectedEntity(ServerPlayer p) {
        UUID selectedId = selectedForLaunch.remove(p.getUUID());
        if (selectedId == null) return;

        ServerLevel serverLevel = p.serverLevel();
        Entity entity = serverLevel.getEntity(selectedId);
        if (entity == null) return;

        Vec3 look = p.getLookAngle();   // 直接用完整方向，指哪打哪(包含上下)
        double launchSpeed = 10;         // 保留設定的速度

        entity.setDeltaMovement(
                look.x * launchSpeed,
                look.y * launchSpeed,
                look.z * launchSpeed
        );
        entity.hurtMarked = true;

        launchedEntities.put(entity.getUUID(), p.getUUID());
    }
    @SubscribeEvent
    public static void onMultiHookCast(PlayerInteractEvent.RightClickItem event) {
        Player p = event.getEntity();
        if (p.level().isClientSide()) return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof net.minecraft.world.item.FishingRodItem)) return;

        int multiHookLevel = stack.getEnchantmentLevel(enchantmentRegister.MULTI_HOOK.get());
        if (multiHookLevel <= 1) return;   // 沒附魔或只有1級時，交給原版單線邏輯處理

        ServerLevel serverLevel = (ServerLevel) p.level();
        event.setCanceled(true);   // 完全接管：一次拋出/收回全部魚線，而非逐次累加

        if (p.fishing == null) {
            // 一次拋出 N 條魚線 (N = 附魔等級)
            int speed = net.minecraft.world.item.enchantment.EnchantmentHelper.getFishingSpeedBonus(stack);
            int luck = net.minecraft.world.item.enchantment.EnchantmentHelper.getFishingLuckBonus(stack);

            // 依玩家面向水平算出一個「右方向量」，讓每條魚線左右錯開，不要疊在同一個點
            double yawRad = Math.toRadians(p.getYRot());
            double forwardX = -Math.sin(yawRad);
            double forwardZ = Math.cos(yawRad);
            double rightX = forwardZ;
            double rightZ = -forwardX;
            double spacing = 1.5;

            List<UUID> hooks = new ArrayList<>();
            for (int n = 0; n < multiHookLevel; n++) {
                FishingHook hook = new FishingHook(p, serverLevel, luck, speed);   // 建構子會自動把 p.fishing 指向最新這一支
                double lateralOffset = (n - (multiHookLevel - 1) / 2.0) * spacing;
                hook.setPos(hook.getX() + rightX * lateralOffset, hook.getY(), hook.getZ() + rightZ * lateralOffset);
                serverLevel.addFreshEntity(hook);
                hooks.add(hook.getUUID());
            }
            extraFishingHooks.put(p.getUUID(), hooks);

            serverLevel.playSound(null, p.getX(), p.getY(), p.getZ(),
                    net.minecraft.sounds.SoundEvents.FISHING_BOBBER_THROW, net.minecraft.sounds.SoundSource.NEUTRAL,
                    0.5F, 0.4F / (serverLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            p.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(stack.getItem()));
            p.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ITEM_INTERACT_START);
        } else {
            // 一次收回全部魚線（含每一條各自可能釣到的東西）
            List<UUID> hooks = extraFishingHooks.remove(p.getUUID());
            Set<UUID> toRetrieve = hooks != null ? new LinkedHashSet<>(hooks) : new LinkedHashSet<>();
            toRetrieve.add(p.fishing.getUUID());

            int totalDamage = 0;
            for (UUID id : toRetrieve) {
                Entity e = serverLevel.getEntity(id);
                if (e instanceof FishingHook fishingHook) {
                    totalDamage += fishingHook.retrieve(stack);
                }
            }
            stack.hurtAndBreak(totalDamage, p, b -> b.broadcastBreakEvent(event.getHand()));

            serverLevel.playSound(null, p.getX(), p.getY(), p.getZ(),
                    net.minecraft.sounds.SoundEvents.FISHING_BOBBER_RETRIEVE, net.minecraft.sounds.SoundSource.NEUTRAL,
                    1.0F, 0.4F / (serverLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }
    @SubscribeEvent
    public static void onChargingTick(TickEvent.PlayerTickEvent event) {
        Player p = event.player;
        if (p.level().isClientSide()) return;

        UUID selectedId = selectedForLaunch.get(p.getUUID());
        if (selectedId == null) return;

        ServerLevel serverLevel = (ServerLevel) p.level();
        Entity target = serverLevel.getEntity(selectedId);

        if (target == null || !target.isAlive()) {
            selectedForLaunch.remove(p.getUUID());
            return;
        }

        Vec3 look = p.getLookAngle();
        double fixedDistance = 3.0;


        target.teleportTo(
                p.getX() + look.x * fixedDistance ,
                p.getEyeY() + look.y * fixedDistance - 0.8,
                p.getZ() + look.z * fixedDistance
        );
        target.hurtMarked = true;
    }



}
