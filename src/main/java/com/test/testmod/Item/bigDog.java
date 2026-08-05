package com.test.testmod.Item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class bigDog extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public bigDog(Properties properties) {
        super(properties);
    }

    @Override

    public void registerControllers(AnimatableManager.ControllerRegistrar controll) {
        controll.add(new AnimationController<>
                (this, "atk_controller", 0, this::attackPredicate));
    }

    private PlayState attackPredicate
            (software.bernie.geckolib.core.animation.AnimationState<bigDog>state){
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(attacker.level().isClientSide){
            this.triggerAnim(attacker, GeoItem.getOrAssignId(stack,
                    (net.minecraft.server.level.ServerLevel) attacker.level()), "atk_controller", "attack");
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

