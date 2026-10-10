package com.hyrulecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Ocarina del Tiempo: click derecho abre el panel de notas (el cliente asigna el hook). */
public class OcarinaItem extends Item {
    public static Runnable OPEN_SCREEN = () -> {};

    public OcarinaItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) OPEN_SCREEN.run();
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.ocarina").formatted(Formatting.GRAY));
    }
}
