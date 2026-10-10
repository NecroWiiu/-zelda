package com.hyrulecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

/** Escudo Deku / Hylian. El Escudo Espejo (MirrorShield) además refleja proyectiles. */
public class HyruleShieldItem extends ShieldItem {
    private final Supplier<Ingredient> repair;
    private final boolean mirror;

    public HyruleShieldItem(Item.Settings settings, Supplier<Ingredient> repair, boolean mirror) {
        super(settings);
        this.repair = repair;
        this.mirror = mirror;
    }

    public boolean reflects() { return mirror; }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return repair.get().test(ingredient);
    }
}
