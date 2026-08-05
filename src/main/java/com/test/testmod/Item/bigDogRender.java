package com.test.testmod.Item;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class bigDogRender extends GeoItemRenderer<bigDog> {

    public bigDogRender() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation("testmod","big_dog")));
    }

    @Override
    public void renderByItem(ItemStack stack , ItemDisplayContext display, PoseStack pose,
                             MultiBufferSource buffer, int light, int overlay){
        if(display == ItemDisplayContext.GUI||display ==ItemDisplayContext.FIXED){
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(new net.minecraft.client.resources.model.ModelResourceLocation(
                    new ResourceLocation("testmod","big_dog_icon"),"inventory"
            ));

            VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(
                    buffer, RenderType.cutout(), true, stack.hasFoil());
            Minecraft.getInstance().getItemRenderer().renderModelLists(
                    model, stack, light, overlay, pose,vertexConsumer);
            return;
        }
        super.renderByItem(stack, display, pose, buffer, light, overlay);

    }
}
