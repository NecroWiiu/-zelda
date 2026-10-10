package com.hyrulecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/** Corazón de recuperación: al recogerlo te cura al instante (como en el juego). */
public class RecoveryHeartItem extends Item {
    public RecoveryHeartItem(Settings settings) { super(settings); }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity p) || stack.isEmpty()) return;
        p.heal(2.0F * stack.getCount());
        world.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.6F, 1.6F);
        stack.setCount(0);
    }
}
