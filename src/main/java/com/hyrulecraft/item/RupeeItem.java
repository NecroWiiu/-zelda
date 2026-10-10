package com.hyrulecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Rupia: moneda de Hyrule. El HUD suma el valor de todas las que llevas encima. */
public class RupeeItem extends Item {
    private final int value;

    public RupeeItem(Settings settings, int value) {
        super(settings);
        this.value = value;
    }

    public int getValue() { return value; }

    public static int countIn(PlayerEntity player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.getItem() instanceof RupeeItem r) total += r.value * s.getCount();
        }
        return total;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.rupee", value).formatted(Formatting.GREEN));
    }
}
