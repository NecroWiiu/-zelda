package com.hyrulecraft.item;

import com.hyrulecraft.entity.HyruleProjectileEntity;
import net.minecraft.client.item.TooltipContext;
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

/** Nuez Deku: estalla en un destello que aturde a los enemigos cercanos. */
public class DekuNutItem extends Item {
    public DekuNutItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            HyruleProjectileEntity nut = new HyruleProjectileEntity(world, user, HyruleProjectileEntity.Kind.FLASH, 0f);
            nut.setPosition(user.getX(), user.getEyeY() - 0.2, user.getZ());
            nut.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.2F, 1.0F);
            world.spawnEntity(nut);
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.6F, 1.6F);
            if (!user.getAbilities().creativeMode) stack.decrement(1);
        }
        user.getItemCooldownManager().set(this, 15);
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.nut").formatted(Formatting.GRAY));
    }
}
