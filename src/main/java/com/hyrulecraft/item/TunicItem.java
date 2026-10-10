package com.hyrulecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Túnica (pechera) que otorga efectos pasivos mientras la llevas puesta. */
public class TunicItem extends ArmorItem {
    private final StatusEffect[] effects;

    public TunicItem(ArmorMaterial material, Settings settings, StatusEffect... effects) {
        super(material, ArmorItem.Type.CHESTPLATE, settings);
        this.effects = effects;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof LivingEntity le)) return;
        if (le.getEquippedStack(EquipmentSlot.CHEST) != stack) return;
        for (StatusEffect effect : effects) {
            le.addStatusEffect(new StatusEffectInstance(effect, 60, 0, true, false, true));
        }
    }
}
