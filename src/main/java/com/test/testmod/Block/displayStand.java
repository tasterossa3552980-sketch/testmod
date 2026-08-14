package com.test.testmod.Block;


import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.BlockModelProvider;

import javax.annotation.Nullable;

public class displayStand extends Block implements EntityBlock {
    public displayStand(Properties properties) {   // ← 應該是這個，吃 Properties
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);   // ← 這裡才是呼叫「方塊實體」的建構子
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof DisplayBlockEntity blockEntity) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack displayed = blockEntity.getDisplayedItem();

            if (displayed.isEmpty() && !heldItem.isEmpty()) {
                // 展示台是空的，手上有東西 → 放上去
                ItemStack toDisplay = heldItem.copy();
                toDisplay.setCount(1);
                blockEntity.setDisplayedItem(toDisplay);
                heldItem.shrink(1);

            } else if (!displayed.isEmpty()) {
                // 展示台上已經有東西 → 拿回來
                player.getInventory().add(displayed.copy());
                blockEntity.setDisplayedItem(ItemStack.EMPTY);
            }
        }

        return InteractionResult.CONSUME;
    }
}


