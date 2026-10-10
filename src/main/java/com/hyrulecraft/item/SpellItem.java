package com.hyrulecraft.item;

import com.hyrulecraft.magic.Spells;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Hechizos de las diosas: Fuego de Din, Amor de Nayru y Viento de Farore. */
public class SpellItem extends Item {
    public enum Spell { DIN, NAYRU, FARORE }

    private final Spell spell;
    private final int cooldown;

    public SpellItem(Settings settings, Spell spell, int cooldown) {
        super(settings);
        this.spell = spell;
        this.cooldown = cooldown;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.fail(stack);
        if (!world.isClient && user instanceof ServerPlayerEntity sp) {
            Spells.cast(sp, spell);
        }
        user.getItemCooldownManager().set(this, spell == Spell.FARORE && !world.isClient && Spells.hasMarker(user) ? 20 : cooldown);
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.spell." + spell.name().toLowerCase()).formatted(Formatting.AQUA));
    }
}
