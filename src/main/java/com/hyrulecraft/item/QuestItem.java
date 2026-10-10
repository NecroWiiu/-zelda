package com.hyrulecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Piedras espirituales y medallones: objetos de misión con brillo. */
public class QuestItem extends Item {
    public QuestItem(Settings settings) { super(settings); }

    @Override public boolean hasGlint(ItemStack stack) { return true; }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.quest").formatted(Formatting.GOLD));
    }
}
