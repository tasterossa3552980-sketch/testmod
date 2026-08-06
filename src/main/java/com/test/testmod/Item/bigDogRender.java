package com.test.testmod.Item;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class bigDogRender extends GeoItemRenderer<bigDog> {

    public bigDogRender() {
        super(new DefaultedItemGeoModel<>
                (new ResourceLocation("testmod","big_dog")));
    }

/*
    @Override
    protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        MultiBufferSource.BufferSource defaultBufferSource = bufferSource instanceof MultiBufferSource.BufferSource bufferSource2 ?
                bufferSource2 : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        BakedModel iconModel = Minecraft.getInstance().getModelManager().getModel(
                new ModelResourceLocation(
                        new ResourceLocation("testmod", "big_dog_icon"),
                        "inventory"
                )
        );
        RenderType renderType =
                getRenderType(this.animatable, getTextureLocation(this.animatable), defaultBufferSource, Minecraft.getInstance().getFrameTime());
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());



        Minecraft.getInstance().getItemRenderer().renderModelLists(
                iconModel, this.currentItemStack, packedLight, packedOverlay, poseStack, vertexConsumer
        );
    }*/
}
