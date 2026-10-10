package com.hyrulecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Botas de hierro (te hunden, pesadas) y Botas de aire (agáchate en el aire para flotar). */
public class HyruleBootsItem extends ArmorItem {
    public enum Kind { IRON, HOVER }
    private final Kind kind;

    public HyruleBootsItem(ArmorMaterial material, Settings settings, Kind kind) {
        super(material, ArmorItem.Type.BOOTS, settings);
        this.kind = kind;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity p) || p.getEquippedStack(EquipmentSlot.FEET) != stack) return;
        if (kind == Kind.IRON) {
            if (!world.isClient) p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0, true, false, false));
            if (p.isTouchingWater()) {
                Vec3d v = p.getVelocity();
                p.setVelocity(v.x * 0.9, Math.min(v.y, 0.0) - 0.08, v.z * 0.9);
            }
        } else if (p.isSneaking() && !p.isOnGround() && !p.isTouchingWater() && !p.getAbilities().flying) {
            Vec3d v = p.getVelocity();
            if (v.y < -0.02) p.setVelocity(v.x, -0.02, v.z);
            p.fallDistance = 0;
            if (world.isClient) {
                world.addParticle(ParticleTypes.CLOUD, p.getX(), p.getY(), p.getZ(), 0, -0.02, 0);
            } else if (p.age % 20 == 0) {
                stack.damage(1, p, e -> e.sendEquipmentBreakStatus(EquipmentSlot.FEET));
            }
        }
    }
}
