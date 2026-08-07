package com.test.testmod.Item;

import com.test.testmod.ModSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class bigDog extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Map<Player, Integer> lastSoundTick = new HashMap<>();
    public bigDog(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override

    public void registerControllers(AnimatableManager.ControllerRegistrar controll) {
        AnimationController<bigDog> controller =(new AnimationController<>
                (this, "atk_controller", 2, state
                -> {
                    ItemStack stack=state.getData(software.bernie.geckolib.constant.DataTickets.ITEMSTACK);

                    if(stack != null && stack.hasTag() && stack.getTag().getBoolean("charge")){
                        net.minecraft.client.player.LocalPlayer user = net.minecraft.client.Minecraft.getInstance().player;
                        if (user != null && user.isUsingItem() && user.getUseItem() == stack){
                            return state.setAndContinue(RawAnimation.begin().thenPlay("animation.big_dog.charge"));
                        }
                    }
                    state.getController().forceAnimationReset();

                    return PlayState.STOP;
                }));
                controller.triggerableAnim("charge", RawAnimation.begin().thenPlay("animation.big_dog.charge"));
                controller.triggerableAnim("return", RawAnimation.begin().thenPlay("animation.big_dog.return"));
                controll.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public InteractionResultHolder<ItemStack> use (Level level, Player p, InteractionHand hand){
        ItemStack itemstack = p.getItemInHand(hand);

        itemstack.getOrCreateTag().putBoolean("charge", true);
        p.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }


    @Override
    public int getUseDuration(ItemStack itemstack){
        return 72000;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseTicks) {
        if (level.isClientSide() && livingEntity instanceof Player p) {
            int usedTicks = this.getUseDuration(stack) - remainingUseTicks;

            // 滯空：把垂直方向的下墜速度歸零，兩端都要做，才會同步
            p.setDeltaMovement(0, 0, 0);
            p.fallDistance = 0;      // 順便清空「已下墜距離」，避免放開後突然被判定摔落傷害
            p.hurtMarked = true;

            // 蓄力進度：0.0(剛開始) ~ 1.0(蓄滿)
            int maxEffect = 100;
            float progress = Math.min(usedTicks / (float)maxEffect, 1.0F);

            spawnChargeParticles(level, p, progress);


            int soundIntervalTicks = (int)(20*((1- progress))) +10;
            int lastTick = lastSoundTick.getOrDefault(p, -999);

            if(usedTicks - lastTick >= soundIntervalTicks){
                float pitch = 0.8f+(progress*1.2f);
                level.playSound(p,p.getX(), p.getY(), p.getZ(),
                        ModSounds.BIG_DOG_CHARGE.get(), SoundSource.PLAYERS, 0.5F, pitch);
            }
        }
    }

    private void spawnChargeParticles(Level level, Player p, float progress) {
        // 1. 計算武器在世界中的精確中心點位置
        // 取得玩家面朝方向的單位向量
        net.minecraft.world.phys.Vec3 lookVec = p.getLookAngle();
        // 取得玩家右側的單位向量（用於計算右手拿武器的橫向偏移）
        net.minecraft.world.phys.Vec3 rightVec = lookVec.cross(new net.minecraft.world.phys.Vec3(0, 1, 0)).normalize();

        // 基礎點：玩家的眼睛高度
        double baseY = p.getEyeY();
        // 如果玩家正在蹲下（Sneaking），眼睛高度與武器位置會稍微下沉
        if (p.isShiftKeyDown()) {
            baseY -= 0.08;
        }

        // 核心偏移綜整：眼睛位置 + 向前延伸 + 向右/左偏移 + 稍微向下（手肘位置）
        // 判斷是否為左撇子（如果是左撇子則反轉右側向量）
        boolean isLeftHanded = p.getMainArm() == net.minecraft.world.entity.HumanoidArm.LEFT;
        double handSideMultiplier = isLeftHanded ? -1.0 : 1.0;

        // 精確計算出正手武器的世界座標 (可根據視覺效果微調數值)
        double targetX = p.getX() + lookVec.x * 0.5 + rightVec.x * 0.35 * handSideMultiplier;
        double targetY = baseY - 0.2 + lookVec.y * 0.3;
        double targetZ = p.getZ() + lookVec.z * 0.5 + rightVec.z * 0.35 * handSideMultiplier;

        // 2. 粒子發射邏輯
        // 隨著蓄力進度（0.0 ~ 1.0），出生的圓圈半徑從 1.2 縮小到 0.1
        double radius = 1.2 * (1.0 - progress * 0.9);
        // 設定粒子要在多少 Tick 內衝到武器上（數字越小，粒子移動越快）
        double ticksToReachCenter = 12.0;

        for (int i = 0; i < 3; i++) {
            // 圍繞著武器中心點計算隨機出生角度
            double angle = level.random.nextDouble() * Math.PI * 2;
            // Y 軸出生高度微幅隨機，形成立體聚集感
            double offsetY = (level.random.nextDouble() - 0.5) * 0.4;

            // 粒子的出生位置（以武器目標點為圓心往外擴散）
            double spawnX = targetX + Math.cos(angle) * radius;
            double spawnY = targetY + offsetY;
            double spawnZ = targetZ + Math.sin(angle) * radius;

            // 【聚集公式】(目標武器位置 - 粒子出生位置) / 抵達所需時間
            double speedX = (targetX - spawnX) / ticksToReachCenter;
            double speedY = (targetY - spawnY) / ticksToReachCenter;
            double speedZ = (targetZ - spawnZ) / ticksToReachCenter;

            // 3. 渲染粒子
            level.addParticle(ParticleTypes.END_ROD,
                    spawnX, spawnY, spawnZ,
                    speedX, speedY, speedZ
            );
        }
    }
    @Override
    public void releaseUsing(ItemStack itemstack, Level level, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player p) {
            // 計算玩家實際蓄力的時間 (1秒 = 20 ticks)
            int charge = this.getUseDuration(itemstack) - i;
            // =================【🎯 2b. 核心公式：根據蓄力時間動態計算長度】=================
            // 設定蓄力完全滿（80刻）時的最大光束長度（格數）
            double maxBeamLength = 50;

            // 計算蓄力進度：0.0 (剛開始) 到 1.0 (蓄滿 80 刻)
            float chargeProgress = Math.min((float) charge / 100.0F, 1.0F);

            double knockbackStrengthPlayer = 0.8 * chargeProgress;   // 蓄力越滿，後座力越強
            Vec3 lookDirection = p.getLookAngle();

            p.setDeltaMovement(
                    p.getDeltaMovement().x - lookDirection.x * knockbackStrengthPlayer,
                    p.getDeltaMovement().y + 0.1,   // 稍微往上一點點，避免整個人貼在地上滑
                    p.getDeltaMovement().z - lookDirection.z * knockbackStrengthPlayer
            );

            // 🌟 最終光束長度 = 最大長度 * 蓄力進度 (例如：蓄力一半就是 25 格)
            double beamLength = maxBeamLength * chargeProgress;
            if(!level.isClientSide()&&charge > 40){
                level.playSound(null,p.getX(), p.getY(), p.getZ(),
                        ModSounds.BIG_DOG_OWL.get(), SoundSource.PLAYERS, 1.0F, 1.0f);
                // 後座力：往玩家「面朝方向」的反方向推

            }else{
                level.playSound(null,p.getX(), p.getY(), p.getZ(),
                        ModSounds.BIG_DOG_noOWL.get(), SoundSource.PLAYERS, 1.0F, 1.0f);
            }

            // ================= 【1. 伺服器端邏輯】 =================
            if (!level.isClientSide() && charge >= 40) {
                // 取消蓄力狀態的 NBT 標記
                if (itemstack.hasTag()) {
                    itemstack.getTag().putBoolean("charge", false);
                }

                // 只有蓄力滿 80 Ticks (4秒) 的時候才觸發後續動作
                if (charge >= 40) {
                    // 觸發 GeckoLib 動畫
                    this.triggerAnim(p,
                            GeoItem.getOrAssignId(itemstack, (net.minecraft.server.level.ServerLevel) level),
                            "return", "animation.big_dog_return"
                    );
                    p.getCooldowns().addCooldown(this, 60);
                }

                // 射線掃描與 3x3 方塊破壞
                net.minecraft.world.phys.Vec3 lookVec = p.getLookAngle();
                double currentX = p.getX() + lookVec.x * 0.5;
                double currentY = p.getEyeY() - 0.2 + lookVec.y * 0.3;
                double currentZ = p.getZ() + lookVec.z * 0.5;

                double step = 0.5;
                boolean hitObstacle = false;
                // 🌟 建立一個名單，確保同一隻怪物在同一發雷射中「只會受到一次傷害」，避免高頻迴圈造成瞬間秒殺與判定混亂
                java.util.Set<java.util.UUID> hitEntities = new java.util.HashSet<>();

                for (double d = 0.0; d < beamLength; d += step) {
                    currentX = p.getX() + lookVec.x * d;
                    currentY = p.getEyeY() - 0.2 + lookVec.y * d;
                    currentZ = p.getZ() + lookVec.z * d;

                    BlockPos centerPos = new BlockPos((int) Math.floor(currentX), (int) Math.floor(currentY), (int) Math.floor(currentZ));
                    net.minecraft.world.level.block.state.BlockState centerState = level.getBlockState(centerPos);

                    if (!centerState.isAir() && centerState.blocksMotion()) {
                        hitObstacle = true;
                        break;
                    }

                    // --- 🎯 1a. 新增：沿途怪物的傷害與擊退判定 ---
                    // 定義掃描範圍（以當前光束點為中心往外擴散 1.2 格的立方體箱子）
                    net.minecraft.world.phys.AABB damageBox = new net.minecraft.world.phys.AABB(
                            currentX - 1.2, currentY - 1.2, currentZ - 1.2,
                            currentX + 1.2, currentY + 1.2, currentZ + 1.2
                    );

                    // 搜尋該範圍內所有的 LivingEntity（活體生物），排除發射者（玩家自己）
                    java.util.List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, damageBox, entity -> entity != p);

                    for (LivingEntity target : targets) {
                        // 如果這隻生物這發雷射還沒打過
                        if (!hitEntities.contains(target.getUUID())) {
                            hitEntities.add(target.getUUID()); // 標記為已擊中

                            // 💥 傷害計算：基礎 6 點傷害（3顆心），蓄滿 80 刻（4秒）會乘以 3.5 倍 ➔ 最大 21 點傷害（10.5顆心，一擊必殺小殭屍）
                            float calculatedDamage = 10.0F * (1.0F + chargeProgress * 3.5F);

                            // 取得玩家的基礎傷害源（這能確保擊殺訊息顯示為玩家的功勞）
                            net.minecraft.world.damagesource.DamageSource damageSource = level.damageSources().playerAttack(p);

                            // 施加傷害
                            if (target.hurt(damageSource, calculatedDamage)) {
                                // 🕸️ 附加特殊效果：給予怪物緩速效果（蓄力越久，減速時間越長、等級越強）
                                // 20 ticks = 1秒。基礎 3 秒，蓄滿 4 秒放開時最大可緩速 8 秒 (160 ticks)
                                int slowDurationTicks = (int) ((3.0F + chargeProgress * 5.0F) * 20.0F);

                                // 🌟 動態計算緩速等級 (Amplifier)：
                                // 0 代表緩速 I (減速 15%)，1 代表緩速 II (減速 30%)
                                // 如果玩家蓄力超過 75% 以上放開，直接給予超級緩速 III (等級填 2，減速 45%，怪物幾乎定在原地)
                                int slowAmplifier = 0;
                                if (chargeProgress >= 0.75F) {
                                    slowAmplifier = 2; // 緩速 III
                                } else if (chargeProgress >= 0.4F) {
                                    slowAmplifier = 1; // 緩速 II
                                }

                                // 施加緩速藥水效果
                                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                        net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                                        slowDurationTicks,//tick 數字
                                        slowAmplifier,//等級
                                        false, true, true
                                ));

                                // 🌀 強力物理擊退計算
                                // 計算擊退方向向量（玩家看向的方向），微幅往上飄（y + 0.15F）會讓擊退拋物線更帥氣
                                net.minecraft.world.phys.Vec3 knockbackDir = new net.minecraft.world.phys.Vec3(lookVec.x, lookVec.y + 0.5, lookVec.z).normalize();

                                // 擊退強度：基礎 0.5，蓄滿最大 1.8（可以把怪物轟飛高空並向後噴出十幾格遠）
                                double knockbackStrength = 0.5 + (chargeProgress * 1.3);

                                // 賦予怪物速度向量
                                target.setDeltaMovement(knockbackDir.x * knockbackStrength, knockbackDir.y * knockbackStrength, knockbackDir.z * knockbackStrength);
                                // 強制同步速度封包到所有客戶端（重要！不加這行，擊退在伺服器算完後怪物會卡住或穿模）
                                target.hurtMarked = true;
                            }
                        }
                    }
                }

                // 沿著剛才宣告好的 beamLength 推進
                for (double d = 0.0; d < beamLength; d += step) {
                    currentX = p.getX() + lookVec.x * d;
                    currentY = p.getEyeY() - 0.2 + lookVec.y * d;
                    currentZ = p.getZ() + lookVec.z * d;

                    BlockPos centerPos = new BlockPos((int) Math.floor(currentX), (int) Math.floor(currentY), (int) Math.floor(currentZ));
                    net.minecraft.world.level.block.state.BlockState centerState = level.getBlockState(centerPos);

                    // 🌟 【修正 2：移除 getMaterial()】
                    // 改用 1.20+ 新版方法：blocksMotion() 判斷方塊是否會阻擋運動（如石頭、泥土等實心方塊）
                    if (!centerState.isAir() && centerState.blocksMotion()) {
                        hitObstacle = true;
                        break;
                    }

                    // 消滅沿途 3x3 範圍內的方塊
                    // 🌟 =================【🎯 核心修改：不規則隨機圓柱破壞】=================
                    // 定義破壞的基礎半徑（1.5 代表涵蓋周圍大約 3 格寬度）
                    double baseRadius = 1.4;

                    for (int xOffset = -2; xOffset <= 2; xOffset++) {
                        for (int yOffset = -2; yOffset <= 2; yOffset++) {
                            for (int zOffset = -2; zOffset <= 2; zOffset++) {

                                // 1. 計算當前檢查的方塊相對於光束中心點的「真實幾何距離」
                                double distanceSq = (xOffset * xOffset) + (yOffset * yOffset) + (zOffset * zOffset);

                                // 2. 引入隨機模糊係數（創造坑窪不規則感）
                                // 每次檢查時，基礎半徑都會隨機增減 -0.4 ~ +0.4 格
                                double randomFuzz = (level.random.nextDouble() - 0.5) * 0.8;
                                double currentRadius = baseRadius + randomFuzz;

                                // 3. 如果方塊在隨機半徑的圓球/圓柱範圍內，則進行破壞
                                if (distanceSq <= (currentRadius * currentRadius)) {
                                    BlockPos targetPos = centerPos.offset(xOffset, yOffset, zOffset);
                                    net.minecraft.world.level.block.state.BlockState state = level.getBlockState(targetPos);

                                    if (!state.isAir() && state.getDestroySpeed(level, targetPos) >= 0) {
                                        level.destroyBlock(targetPos, true, p);
                                    }
                                }
                            }
                        }
                    }
                    // ===================================================================
                }

                // 在光束末端產生爆炸
                float explosionPower = 8.0F;
                level.explode(p, null, null,
                        currentX, currentY, currentZ,
                        explosionPower, false, Level.ExplosionInteraction.BLOCK);
            }


            // ================= 【2. 客戶端邏輯】 =================
            // 粒子必須在客戶端渲染，且我們同步限制：只有蓄力滿 80 刻才會射出光束
            if (level.isClientSide() && charge >= 40) {

                // 2a. 取得光束起點：玩家正手武器的世界座標
                net.minecraft.world.phys.Vec3 lookVec = p.getLookAngle();
                net.minecraft.world.phys.Vec3 rightVec =
                        lookVec.cross(new net.minecraft.world.phys.Vec3(0, 1, 0)).normalize();
                // 🌟 【修正補上】取得上方向量 (上方向)，用來與 rightVec 一起構建出垂直於視線的圓形平面
                net.minecraft.world.phys.Vec3 upVec = rightVec.cross(lookVec).normalize();

                boolean isLeftHanded = p.getMainArm() == net.minecraft.world.entity.HumanoidArm.LEFT;
                double handSideMultiplier = isLeftHanded ? -1.0 : 1.0;

                double startX = p.getX() + lookVec.x * 0.5 + rightVec.x * 0.35 * handSideMultiplier;
                double startY = p.getEyeY() - 0.2 + lookVec.y * 0.3;
                double startZ = p.getZ() + lookVec.z * 0.5 + rightVec.z * 0.35 * handSideMultiplier;



                // 粒子密度
                double step = 0.2;

                level.addParticle(ParticleTypes.SONIC_BOOM, startX, startY, startZ, 0.0, 0.0, 0.0);

                // 2c. 延著視角方向循環繪製直線粒子
                for (double d = 0.0; d < beamLength; d += step) {
                    double particleX = startX + lookVec.x * d;
                    double particleY = startY + lookVec.y * d;
                    double particleZ = startZ + lookVec.z * d;

                    // 🌟 每隔 1.0 格就生成一個 SONIC_BOOM 迴圈震波，直到與光束末端（beamLength）完全等長
                    // 使用 Math.abs(d - Math.floor(d)) < 0.01 確保在一格的整數點上觸發，避免生成過多導致嚴重卡頓
                        if (Math.abs(d - Math.round(d)) < 0.01) {
                            level.addParticle(ParticleTypes.SONIC_BOOM, particleX, particleY, particleZ, 0.0, 0.0, 0.0);
                        }





                    // 🌟 【圓圈生成邏輯】
                    // 為了不讓電腦卡死，我們設定「每隔 1.5 格」畫一個圓圈
                    if (Math.abs(d - Math.round(d / 1.5) * 1.5) < 0.01) {
                        // 設定圓圈的半徑（您可以根據需要調整，例如 0.6 格寬）
                        double circleRadius = 0.8;
                        // 一個圓圈用 8 顆粒子連成，既有圓形視覺又兼顧效能
                        int pointsInCircle = 32;

                        for (int k = 0; k < pointsInCircle; k++) {
                            // 計算圓周上的角度
                            double angle = (k * 2.0 * Math.PI) / pointsInCircle;

                            // 使用 3D 向量旋轉公式，讓圓圈能夠完美垂直於玩家的視線方向（不論朝天還是朝地）
                            double sin = Math.sin(angle) * circleRadius;
                            double cos = Math.cos(angle) * circleRadius;

                            double ringX = particleX + rightVec.x * cos + upVec.x * sin;
                            double ringY = particleY + rightVec.y * cos + upVec.y * sin;
                            double ringZ = particleZ + rightVec.z * cos + upVec.z * sin;

                            // 發射圓圈上的 END_ROD 粒子
                            level.addParticle(ParticleTypes.END_ROD, ringX, ringY, ringZ, 0.0, 0.0, 0.0);
                        }
                    }



                    // 讓光束核心周圍有一點點微幅擴散，增加粒子立體感與粗度
                    for (int j = 0; j < 2; j++) {
                        double offsetX = (level.random.nextDouble() - 0.5) * 0.05;
                        double offsetY = (level.random.nextDouble() - 0.5) * 0.05;
                        double offsetZ = (level.random.nextDouble() - 0.5) * 0.05;

                        // 發射末影燭粒子 (速度給 0，讓光束帥氣地定格在空中隨後消失)
                        level.addParticle(ParticleTypes.END_ROD,
                                particleX + offsetX, particleY + offsetY, particleZ + offsetZ,
                                0.0, 0.0, 0.0
                        );
                    }
                }
            }
        }
    }


    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(!attacker.level().isClientSide() && attacker instanceof Player p){
            this.triggerAnim(p, GeoItem.getOrAssignId(stack,
                    (net.minecraft.server.level.ServerLevel) attacker.level()), "return", "animation.big_dog.return");
        }
        return super.hurtEnemy(stack, target, attacker);
        }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private bigDogRender renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new bigDogRender();
                }
                return this.renderer;

            }
        });
    }
}

