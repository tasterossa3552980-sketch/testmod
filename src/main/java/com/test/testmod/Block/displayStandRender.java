package com.test.testmod.Block;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class displayStandRender extends GeoBlockRenderer<DisplayStandEntity> {
    public displayStandRender(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(new ResourceLocation("testmod", "display_stand")));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, DisplayStandEntity animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        // 先讓官方原本的邏輯，正常畫出底座造型
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);

        ItemStack displayedItem = animatable.getDisplayedItem();
        if (displayedItem.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0, 1.3, 0);

        float rotation = (animatable.getLevel().getGameTime() + partialTick) * 4.0F;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));

        poseStack.scale(0.75F, 0.75F, 0.75F);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                displayedItem, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                poseStack, bufferSource, animatable.getLevel(), 0);

        poseStack.popPose();
    }
}