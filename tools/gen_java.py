# -*- coding: utf-8 -*-
import os
from content import ITEMS, BLOCKS

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVA = os.path.join(ROOT, "src/main/java/com/hyrulecraft/registry")

HEADER = "// GENERADO por tools/generate_all.py a partir de tools/content.py. Edita content.py, no este archivo.\n"

def gen_items():
    lines = [HEADER, "package com.hyrulecraft.registry;\n",
        "import com.hyrulecraft.HyruleCraft;",
        "import com.hyrulecraft.entity.*;",
        "import com.hyrulecraft.item.*;",
        "import net.fabricmc.fabric.api.item.v1.FabricItemSettings;",
        "import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;",
        "import net.minecraft.entity.effect.StatusEffects;",
        "import net.minecraft.item.*;",
        "import net.minecraft.recipe.Ingredient;",
        "import net.minecraft.registry.Registries;",
        "import net.minecraft.registry.Registry;",
        "import net.minecraft.registry.tag.ItemTags;",
        "import net.minecraft.text.Text;",
        "import net.minecraft.util.Identifier;",
        "import net.minecraft.util.Rarity;\n",
        "import java.util.ArrayList;",
        "import java.util.List;\n",
        "public final class ModItems {",
        "    private ModItems() {}\n",
        "    public static final List<Item> ALL = new ArrayList<>();\n"]
    for it in ITEMS:
        lines.append(f'    public static final Item {it["id"].upper()} = reg("{it["id"]}", {it["expr"]});')
    lines += ["",
        "    private static Item reg(String id, Item item) {",
        "        ALL.add(item);",
        "        return Registry.register(Registries.ITEM, new Identifier(HyruleCraft.MOD_ID, id), item);",
        "    }\n",
        "    public static void init() {",
        "        Registry.register(Registries.ITEM_GROUP, new Identifier(HyruleCraft.MOD_ID, \"main\"),",
        "                FabricItemGroup.builder()",
        "                        .icon(() -> new ItemStack(OCARINA))",
        "                        .displayName(Text.translatable(\"itemGroup.hyrulecraft.main\"))",
        "                        .entries((ctx, entries) -> {",
        "                            for (Item i : ModBlocks.BLOCK_ITEMS) entries.add(i);",
        "                            for (Item i : ALL) entries.add(i);",
        "                        }).build());",
        "    }",
        "}", ""]
    open(os.path.join(JAVA, "ModItems.java"), "w", encoding="utf-8").write("\n".join(lines))

def gen_blocks():
    lines = [HEADER, "package com.hyrulecraft.registry;\n",
        "import com.hyrulecraft.HyruleCraft;",
        "import com.hyrulecraft.block.*;",
        "import net.fabricmc.fabric.api.item.v1.FabricItemSettings;",
        "import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;",
        "import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;",
        "import net.minecraft.block.*;",
        "import net.minecraft.item.BlockItem;",
        "import net.minecraft.item.Item;",
        "import net.minecraft.registry.Registries;",
        "import net.minecraft.registry.Registry;",
        "import net.minecraft.sound.BlockSoundGroup;",
        "import net.minecraft.util.Identifier;\n",
        "import java.util.ArrayList;",
        "import java.util.List;\n",
        "public final class ModBlocks {",
        "    private ModBlocks() {}\n",
        "    public static final List<Item> BLOCK_ITEMS = new ArrayList<>();\n"]
    for b in BLOCKS:
        lines.append(f'    public static final Block {b["id"].upper()} = reg("{b["id"]}", {b["expr"]}, {"true" if b["item"] else "false"});')
    lines += ["",
        "    private static Block reg(String id, Block block, boolean withItem) {",
        "        Identifier ident = new Identifier(HyruleCraft.MOD_ID, id);",
        "        Registry.register(Registries.BLOCK, ident, block);",
        "        if (withItem) BLOCK_ITEMS.add(Registry.register(Registries.ITEM, ident, new BlockItem(block, new FabricItemSettings())));",
        "        return block;",
        "    }\n",
        "    public static void init() {",
        "        FlammableBlockRegistry.getDefaultInstance().add(DEKU_WEB, 100, 100);",
        "    }",
        "}", ""]
    open(os.path.join(JAVA, "ModBlocks.java"), "w", encoding="utf-8").write("\n".join(lines))

def run():
    os.makedirs(JAVA, exist_ok=True)
    gen_items(); gen_blocks()
    print("  Java: ModItems.java, ModBlocks.java")

if __name__ == "__main__":
    run()
