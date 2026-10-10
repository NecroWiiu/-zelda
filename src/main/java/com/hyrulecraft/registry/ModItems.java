// GENERADO por tools/generate_all.py a partir de tools/content.py. Edita content.py, no este archivo.

package com.hyrulecraft.registry;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.entity.*;
import com.hyrulecraft.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;

public final class ModItems {
    private ModItems() {}

    public static final List<Item> ALL = new ArrayList<>();

    public static final Item KOKIRI_SWORD = reg("kokiri_sword", new HyruleSwordItem(HyruleMaterials.KOKIRI, 3, -2.4f, new FabricItemSettings().maxCount(1), 6f, 2.6f, 2f));
    public static final Item MASTER_SWORD = reg("master_sword", new HyruleSwordItem(HyruleMaterials.MASTER, 4, -2.2f, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC), 10f, 3.2f, 4f));
    public static final Item BIGGORON_SWORD = reg("biggoron_sword", new HyruleSwordItem(HyruleMaterials.BIGGORON, 5, -3.0f, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 14f, 3.8f, 5f));
    public static final Item MEGATON_HAMMER = reg("megaton_hammer", new MegatonHammerItem(HyruleMaterials.HAMMER, 5, -3.1f, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item DEKU_STICK = reg("deku_stick", new DekuStickItem(new FabricItemSettings()));
    public static final Item DEKU_SHIELD = reg("deku_shield", new HyruleShieldItem(new FabricItemSettings().maxDamage(168), () -> Ingredient.fromTag(ItemTags.PLANKS), false));
    public static final Item HYLIAN_SHIELD = reg("hylian_shield", new HyruleShieldItem(new FabricItemSettings().maxDamage(672).fireproof(), () -> Ingredient.ofItems(Items.IRON_INGOT), false));
    public static final Item MIRROR_SHIELD = reg("mirror_shield", new HyruleShieldItem(new FabricItemSettings().maxDamage(1008).fireproof().rarity(Rarity.RARE), () -> Ingredient.ofItems(Items.DIAMOND), true));
    public static final Item KOKIRI_CAP = reg("kokiri_cap", new ArmorItem(HyruleMaterials.KOKIRI_SET, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item KOKIRI_TUNIC = reg("kokiri_tunic", new TunicItem(HyruleMaterials.KOKIRI_SET, new FabricItemSettings()));
    public static final Item KOKIRI_TROUSERS = reg("kokiri_trousers", new ArmorItem(HyruleMaterials.KOKIRI_SET, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item KOKIRI_BOOTS = reg("kokiri_boots", new ArmorItem(HyruleMaterials.KOKIRI_SET, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final Item GORON_TUNIC = reg("goron_tunic", new TunicItem(HyruleMaterials.GORON_SET, new FabricItemSettings().fireproof(), StatusEffects.FIRE_RESISTANCE));
    public static final Item ZORA_TUNIC = reg("zora_tunic", new TunicItem(HyruleMaterials.ZORA_SET, new FabricItemSettings(), StatusEffects.WATER_BREATHING, StatusEffects.DOLPHINS_GRACE));
    public static final Item IRON_BOOTS = reg("iron_boots", new HyruleBootsItem(HyruleMaterials.IRON_BOOTS, new FabricItemSettings(), HyruleBootsItem.Kind.IRON));
    public static final Item HOVER_BOOTS = reg("hover_boots", new HyruleBootsItem(HyruleMaterials.HOVER_BOOTS, new FabricItemSettings().rarity(Rarity.RARE), HyruleBootsItem.Kind.HOVER));
    public static final Item OCARINA = reg("ocarina", new OcarinaItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item HOOKSHOT = reg("hookshot", new HookshotItem(new FabricItemSettings().maxCount(1)));
    public static final Item BOOMERANG = reg("boomerang", new BoomerangItem(new FabricItemSettings().maxCount(1)));
    public static final Item BOMB = reg("bomb", new BombItem(new FabricItemSettings().maxCount(16)));
    public static final Item DEKU_NUT = reg("deku_nut", new DekuNutItem(new FabricItemSettings().maxCount(32)));
    public static final Item FAIRY_BOTTLE = reg("fairy_bottle", new FairyBottleItem(new FabricItemSettings().maxCount(1)));
    public static final Item LENS_OF_TRUTH = reg("lens_of_truth", new LensItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item DINS_FIRE = reg("dins_fire", new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), SpellItem.Spell.DIN, 300));
    public static final Item NAYRUS_LOVE = reg("nayrus_love", new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), SpellItem.Spell.NAYRU, 900));
    public static final Item FARORES_WIND = reg("farores_wind", new SpellItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), SpellItem.Spell.FARORE, 200));
    public static final Item RUPEE_GREEN = reg("rupee_green", new RupeeItem(new FabricItemSettings(), 1));
    public static final Item RUPEE_BLUE = reg("rupee_blue", new RupeeItem(new FabricItemSettings(), 5));
    public static final Item RUPEE_RED = reg("rupee_red", new RupeeItem(new FabricItemSettings(), 20));
    public static final Item RUPEE_PURPLE = reg("rupee_purple", new RupeeItem(new FabricItemSettings(), 50));
    public static final Item RUPEE_GOLD = reg("rupee_gold", new RupeeItem(new FabricItemSettings(), 200));
    public static final Item HEART_PIECE = reg("heart_piece", new HeartItem(new FabricItemSettings().rarity(Rarity.UNCOMMON), 0.5));
    public static final Item HEART_CONTAINER = reg("heart_container", new HeartItem(new FabricItemSettings().rarity(Rarity.RARE), 2.0));
    public static final Item RECOVERY_HEART = reg("recovery_heart", new RecoveryHeartItem(new FabricItemSettings()));
    public static final Item ENERGY_BALL = reg("energy_ball", new Item(new FabricItemSettings()));
    public static final Item GOLD_SKULLTULA_TOKEN = reg("gold_skulltula_token", new Item(new FabricItemSettings()));
    public static final Item KOKIRI_EMERALD = reg("kokiri_emerald", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item GORON_RUBY = reg("goron_ruby", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item ZORA_SAPPHIRE = reg("zora_sapphire", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item FOREST_MEDALLION = reg("forest_medallion", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item FIRE_MEDALLION = reg("fire_medallion", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item WATER_MEDALLION = reg("water_medallion", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item SPIRIT_MEDALLION = reg("spirit_medallion", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item SHADOW_MEDALLION = reg("shadow_medallion", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item LIGHT_MEDALLION = reg("light_medallion", new QuestItem(new FabricItemSettings().maxCount(1)));
    public static final Item STALFOS_SPAWN_EGG = reg("stalfos_spawn_egg", new SpawnEggItem(ModEntities.STALFOS, 0xd8d4c4, 0x4a4a5a, new FabricItemSettings()));
    public static final Item REDEAD_SPAWN_EGG = reg("redead_spawn_egg", new SpawnEggItem(ModEntities.REDEAD, 0x7b6f63, 0x8a1c1c, new FabricItemSettings()));
    public static final Item SKULLTULA_SPAWN_EGG = reg("skulltula_spawn_egg", new SpawnEggItem(ModEntities.SKULLTULA, 0x3b2a1a, 0xe0b020, new FabricItemSettings()));
    public static final Item GANONDORF_SPAWN_EGG = reg("ganondorf_spawn_egg", new SpawnEggItem(ModEntities.GANONDORF, 0x2a1a1a, 0xc02020, new FabricItemSettings()));

    private static Item reg(String id, Item item) {
        ALL.add(item);
        return Registry.register(Registries.ITEM, new Identifier(HyruleCraft.MOD_ID, id), item);
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(HyruleCraft.MOD_ID, "main"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(OCARINA))
                        .displayName(Text.translatable("itemGroup.hyrulecraft.main"))
                        .entries((ctx, entries) -> {
                            for (Item i : ModBlocks.BLOCK_ITEMS) entries.add(i);
                            for (Item i : ALL) entries.add(i);
                        }).build());
    }
}
