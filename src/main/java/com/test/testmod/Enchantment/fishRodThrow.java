package com.test.testmod.Enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class fishRodThrow extends Enchantment{
    public fishRodThrow() {
        super(Rarity.RARE, EnchantmentCategory.FISHING_ROD,new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel() {
        return 1;
    }
}
