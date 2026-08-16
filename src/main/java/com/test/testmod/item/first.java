package com.test.testmod.item;


import com.test.testmod.registry.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class first extends Item {

    public first(Properties props) {
        super(props);
    }

    @Override

    public InteractionResultHolder<ItemStack> use(Level level, Player p , InteractionHand hand){
        ItemStack stack = p.getItemInHand(hand);
        CompoundTag persistentData = p.getPersistentData();



        if(!level.isClientSide()){
            p.getFoodData().eat(1,0.6f);
            p.heal(4.0F);


            if(p.onGround()) {
                persistentData.putInt("air", 0);
                p.setDeltaMovement(p.getDeltaMovement().x, 0.8, p.getDeltaMovement().z);
                p.hurtMarked = true;


            } else {
                int air= persistentData.getInt("air");
                if(air<3) {
                    persistentData.putInt("air", air+1);
                    double db =Math.max(0.8 + (air * (-0.2)), 0);
                    p.setDeltaMovement(p.getDeltaMovement().x, db, p.getDeltaMovement().z);
                    p.hurtMarked = true;
                }
            }



            stack.hurtAndBreak(1,p,(entity) -> entity.broadcastBreakEvent(hand));
        }
        level.playSound(null,p.getX(),p.getY(),p.getZ(),
                ModSounds.first.get(), SoundSource.PLAYERS,1.0F,1.0F);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    public boolean hurtEnemy(ItemStack stack, LivingEntity target,LivingEntity attacker) {
        attacker.setDeltaMovement(attacker.getDeltaMovement().x, 1.0, attacker.getDeltaMovement().z);
        Player p = (Player) attacker;
        CompoundTag persistentData = p.getPersistentData();
        persistentData.putInt("air",0);
        attacker.hurtMarked=true;

        return super.hurtEnemy(stack,target,attacker);
    }
}
