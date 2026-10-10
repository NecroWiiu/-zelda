package com.hyrulecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Lente de la Verdad: sostenida en la mano revela (resplandor) a las criaturas cercanas, incluso invisibles. */
public class LensItem extends Item {
    public LensItem(Settings settings) { super(settings); }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity p) || p.age % 10 != 0) return;
        if (p.getMainHandStack() != stack && p.getOffHandStack() != stack) return;
        for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, p.getBoundingBox().expand(18), t -> t != p && !(t instanceof ArmorStandEntity))) {
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 25, 0, true, false));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.lens").formatted(Formatting.GRAY));
    }
}
