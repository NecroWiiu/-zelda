package com.hyrulecraft.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.function.Supplier;

/** Materiales de herramientas y armaduras del mod. */
public final class HyruleMaterials {
    private HyruleMaterials() {}

    public static class Tool implements ToolMaterial {
        private final int durability, level, enchant;
        private final float speed, damage;
        private final Supplier<Ingredient> repair;

        public Tool(int durability, float speed, float damage, int level, int enchant, Supplier<Ingredient> repair) {
            this.durability = durability; this.speed = speed; this.damage = damage;
            this.level = level; this.enchant = enchant; this.repair = repair;
        }
        @Override public int getDurability() { return durability; }
        @Override public float getMiningSpeedMultiplier() { return speed; }
        @Override public float getAttackDamage() { return damage; }
        @Override public int getMiningLevel() { return level; }
        @Override public int getEnchantability() { return enchant; }
        @Override public Ingredient getRepairIngredient() { return repair.get(); }
    }

    public static final ToolMaterial KOKIRI = new Tool(250, 6f, 1f, 2, 12, () -> Ingredient.ofItems(Items.COPPER_INGOT));
    public static final ToolMaterial MASTER = new Tool(2200, 8f, 3f, 4, 25, () -> Ingredient.ofItems(Items.DIAMOND));
    public static final ToolMaterial BIGGORON = new Tool(2800, 6f, 4f, 4, 20, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));
    public static final ToolMaterial DEKU = new Tool(40, 2f, 0f, 0, 5, () -> Ingredient.ofItems(Items.STICK));
    public static final ToolMaterial HAMMER = new Tool(900, 4f, 4f, 3, 8, () -> Ingredient.ofItems(Items.IRON_INGOT));

    /** prot = {casco, pechera, pantalones, botas}. La textura vive en assets/minecraft/textures/models/armor/NAME_layer_1.png */
    public static class Armor implements ArmorMaterial {
        private final String name;
        private final int durMul, enchant;
        private final int[] prot;
        private final float toughness, knockback;
        private final Supplier<Ingredient> repair;

        public Armor(String name, int durMul, int[] prot, int enchant, float toughness, float knockback, Supplier<Ingredient> repair) {
            this.name = name; this.durMul = durMul; this.prot = prot;
            this.enchant = enchant; this.toughness = toughness; this.knockback = knockback; this.repair = repair;
        }
        @Override public int getDurability(ArmorItem.Type type) {
            int base = switch (type) { case BOOTS -> 13; case LEGGINGS -> 15; case CHESTPLATE -> 16; case HELMET -> 11; };
            return base * durMul;
        }
        @Override public int getProtection(ArmorItem.Type type) {
            return switch (type) { case HELMET -> prot[0]; case CHESTPLATE -> prot[1]; case LEGGINGS -> prot[2]; case BOOTS -> prot[3]; };
        }
        @Override public int getEnchantability() { return enchant; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER; }
        @Override public Ingredient getRepairIngredient() { return repair.get(); }
        @Override public String getName() { return name; }
        @Override public float getToughness() { return toughness; }
        @Override public float getKnockbackResistance() { return knockback; }
    }

    public static final ArmorMaterial KOKIRI_SET = new Armor("hyrulecraft_kokiri", 18, new int[]{1, 4, 2, 1}, 15, 0f, 0f, () -> Ingredient.ofItems(Items.GREEN_WOOL));
    public static final ArmorMaterial GORON_SET = new Armor("hyrulecraft_goron", 24, new int[]{0, 7, 0, 0}, 15, 1f, 0.1f, () -> Ingredient.ofItems(Items.RED_WOOL));
    public static final ArmorMaterial ZORA_SET = new Armor("hyrulecraft_zora", 24, new int[]{0, 7, 0, 0}, 15, 1f, 0f, () -> Ingredient.ofItems(Items.BLUE_WOOL));
    public static final ArmorMaterial IRON_BOOTS = new Armor("hyrulecraft_iron_boots", 40, new int[]{0, 0, 0, 3}, 9, 2f, 0.5f, () -> Ingredient.ofItems(Items.IRON_BLOCK));
    public static final ArmorMaterial HOVER_BOOTS = new Armor("hyrulecraft_hover_boots", 30, new int[]{0, 0, 0, 2}, 18, 0f, 0f, () -> Ingredient.ofItems(Items.PHANTOM_MEMBRANE));
}
