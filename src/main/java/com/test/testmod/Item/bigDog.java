package com.test.testmod.Item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
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

import java.util.function.Consumer;

public class bigDog extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
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
                //controller.triggerableAnim("return", RawAnimation.begin().thenPlay("animation.big_dog.return"));
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
    public void releaseUsing(ItemStack itemstack, Level level, LivingEntity livingEntity, int i) {
        if(!level.isClientSide()&& livingEntity instanceof Player p) {
            int charge = this.getUseDuration(itemstack)- i;

            if(itemstack.hasTag()){
                itemstack.getTag().putBoolean("charge",false);
            }

            /*if (charge >= 80) {
                this.triggerAnim(p,
                        GeoItem.getOrAssignId(itemstack,(net.minecraft.server.level.ServerLevel)level),
                        "atk_controller","charge");
            }*/
            //這個是之後要放攻擊效果的地方
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

