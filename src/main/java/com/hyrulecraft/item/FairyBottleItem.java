package com.hyrulecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Hada en botella: cura 10 PV, limpia efectos negativos y devuelve la botella. */
public class FairyBottleItem extends Item {
    public FairyBottleItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getHealth() >= user.getMaxHealth() && user.getActiveStatusEffects().isEmpty()) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient) {
            user.heal(10.0F);
            List<StatusEffect> bad = new ArrayList<>();
            for (StatusEffectInstance e : user.getStatusEffects()) {
                if (e.getEffectType().getCategory() == StatusEffectCategory.HARMFUL) bad.add(e.getEffectType());
            }
            for (StatusEffect e : bad) user.removeStatusEffect(e);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
            ((ServerWorld) world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, user.getX(), user.getBodyY(0.6), user.getZ(), 16, 0.5, 0.6, 0.5, 0.1);
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0F, 1.5F);
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
                user.giveItemStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.fairy").formatted(Formatting.LIGHT_PURPLE));
    }
}
