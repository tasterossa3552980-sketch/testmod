package com.test.testmod.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;


public class firstSword extends SwordItem {


    public firstSword(Tier t, int atk, float atkSpeed, Properties prop) {
        super(t, atk, atkSpeed, prop);

    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attaker) {
        attaker.heal(3.0f);
        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
        return super.hurtEnemy(stack, target, attaker);
    }


}
