package com.hyrulecraft.item;

import com.hyrulecraft.entity.BombEntity;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Bomba: se lanza, rebota y estalla a los 2,5 s. Rompe Muros Agrietados y vasijas, pero no el terreno. */
public class BombItem extends Item {
    public BombItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            BombEntity bomb = new BombEntity(world, user.getX(), user.getEyeY() - 0.3, user.getZ(), user);
            Vec3d look = user.getRotationVec(1.0F);
            double power = user.isSneaking() ? 0.25 : 0.65;
            bomb.setVelocity(look.x * power, look.y * power + 0.18, look.z * power);
            world.spawnEntity(bomb);
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!user.getAbilities().creativeMode) stack.decrement(1);
        }
        user.getItemCooldownManager().set(this, 12);
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.bomb").formatted(Formatting.GRAY));
    }
}
