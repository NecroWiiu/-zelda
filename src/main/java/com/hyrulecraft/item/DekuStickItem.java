package com.hyrulecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

/** Palo Deku: frágil, pero prende fuego al enemigo. */
public class DekuStickItem extends SwordItem {
    public DekuStickItem(Settings settings) {
        super(HyruleMaterials.DEKU, 1, -2.0F, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(3);
        return super.postHit(stack, target, attacker);
    }
}
