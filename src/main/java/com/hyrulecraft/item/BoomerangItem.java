package com.hyrulecraft.item;

import com.hyrulecraft.entity.BoomerangEntity;
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

/** Bumerán: aturde, daña y recoge objetos. Vuelve a ti. */
public class BoomerangItem extends Item {
    public BoomerangItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.fail(stack);
        if (!world.isClient) {
            BoomerangEntity b = new BoomerangEntity(world, user);
            b.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.1F, 0.0F);
            world.spawnEntity(b);
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 1.0F, 1.6F);
        }
        user.getItemCooldownManager().set(this, 400);
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.boomerang").formatted(Formatting.GRAY));
    }
}
