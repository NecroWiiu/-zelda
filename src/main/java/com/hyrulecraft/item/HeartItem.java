package com.hyrulecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/** Pieza de corazón (+0.5 PV, 4 piezas = 1 corazón) y Contenedor de corazón (+2 PV). Máximo +20 PV. */
public class HeartItem extends Item {
    public static final UUID MODIFIER_ID = UUID.fromString("5b0f5b6e-2c1d-4a77-9a54-6c1e1d0a7e01");
    public static final double MAX_BONUS = 20.0;
    private final double amount;

    public HeartItem(Settings settings, double amount) {
        super(settings);
        this.amount = amount;
    }

    public static double bonus(PlayerEntity p) {
        EntityAttributeInstance inst = p.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (inst == null) return 0;
        EntityAttributeModifier m = inst.getModifier(MODIFIER_ID);
        return m == null ? 0 : m.getValue();
    }

    public static void apply(PlayerEntity p, double total) {
        EntityAttributeInstance inst = p.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (inst == null) return;
        inst.removeModifier(MODIFIER_ID);
        if (total > 0) {
            inst.addPersistentModifier(new EntityAttributeModifier(MODIFIER_ID, "Hyrule hearts", total, EntityAttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        double cur = bonus(user);
        if (cur >= MAX_BONUS) {
            if (!world.isClient) user.sendMessage(Text.translatable("message.hyrulecraft.hearts_full"), true);
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient) {
            double total = Math.min(MAX_BONUS, cur + amount);
            apply(user, total);
            if (amount >= 2.0) {
                user.setHealth(user.getMaxHealth());
                user.sendMessage(Text.translatable("message.hyrulecraft.heart_container"), true);
            } else {
                int pieces = (int) Math.round((total % 2.0) / 0.5);
                if (pieces == 0) {
                    user.setHealth(user.getMaxHealth());
                    user.sendMessage(Text.translatable("message.hyrulecraft.heart_complete"), true);
                } else {
                    user.sendMessage(Text.translatable("message.hyrulecraft.heart_piece", pieces), true);
                }
            }
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8F, 1.4F);
            if (!user.getAbilities().creativeMode) stack.decrement(1);
        }
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.heart", amount).formatted(Formatting.RED));
    }
}
