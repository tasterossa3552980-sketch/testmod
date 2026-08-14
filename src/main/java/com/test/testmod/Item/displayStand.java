/*
package com.test.testmod.Item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import com.test.testmod.Block.displayStandRender; // 引入你的方塊渲染器路徑

import java.util.function.Consumer;

public class displayStand extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public displayStand(Block block, Properties properties) {
        super(block, properties);
    }

    // 關鍵：將物品的渲染綁定到 GeckoLib 的內建物品渲染器
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<displayStand> renderer;


            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    // 這裡重複使用你原本給方塊設定的模型路徑 "display_stand" 或 "display_block"
                    this.renderer = new GeoItemRenderer<>(
                            new software.bernie.geckolib.model.DefaultedItemGeoModel<>(
                                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("testmod", "display_stand")
                            )
                    );
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 如果物品欄內不需要播動畫，這裡維持空白即可
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

 */

