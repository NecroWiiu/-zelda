// GENERADO por tools/generate_all.py a partir de tools/content.py. Edita content.py, no este archivo.

package com.hyrulecraft.registry;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.block.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class ModBlocks {
    private ModBlocks() {}

    public static final List<Item> BLOCK_ITEMS = new ArrayList<>();

    public static final Block HYRULE_STONE = reg("hyrule_stone", new Block(FabricBlockSettings.copyOf(Blocks.STONE)), true);
    public static final Block HYRULE_STONE_BRICKS = reg("hyrule_stone_bricks", new Block(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)), true);
    public static final Block HYRULE_STONE_BRICK_STAIRS = reg("hyrule_stone_brick_stairs", new StairsBlock(HYRULE_STONE_BRICKS.getDefaultState(), FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)), true);
    public static final Block HYRULE_STONE_BRICK_SLAB = reg("hyrule_stone_brick_slab", new SlabBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)), true);
    public static final Block CARVED_HYRULE_STONE = reg("carved_hyrule_stone", new Block(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)), true);
    public static final Block TEMPLE_PILLAR = reg("temple_pillar", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)), true);
    public static final Block TRIFORCE_TILE = reg("triforce_tile", new Block(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).luminance(7)), true);
    public static final Block FOREST_BRICKS = reg("forest_bricks", new Block(FabricBlockSettings.copyOf(Blocks.MOSSY_STONE_BRICKS)), true);
    public static final Block FIRE_BRICKS = reg("fire_bricks", new Block(FabricBlockSettings.copyOf(Blocks.NETHER_BRICKS)), true);
    public static final Block WATER_TILES = reg("water_tiles", new Block(FabricBlockSettings.copyOf(Blocks.PRISMARINE_BRICKS)), true);
    public static final Block SPIRIT_BRICKS = reg("spirit_bricks", new Block(FabricBlockSettings.copyOf(Blocks.SANDSTONE)), true);
    public static final Block SHADOW_BRICKS = reg("shadow_bricks", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS)), true);
    public static final Block CRACKED_WALL = reg("cracked_wall", new CrackedWallBlock(FabricBlockSettings.copyOf(Blocks.STONE).strength(50.0f, 0.0f)), true);
    public static final Block POT = reg("pot", new PotBlock(FabricBlockSettings.create().strength(0.0f).nonOpaque().sounds(BlockSoundGroup.DECORATED_POT)), true);
    public static final Block DEKU_WEB = reg("deku_web", new CobwebBlock(FabricBlockSettings.copyOf(Blocks.COBWEB)), true);
    public static final Block SACRED_FLAME = reg("sacred_flame", new Block(FabricBlockSettings.copyOf(Blocks.GLOWSTONE).luminance(15)), true);
    public static final Block FAIRY_FOUNTAIN_PAD = reg("fairy_fountain_pad", new FairyFountainBlock(FabricBlockSettings.create().strength(1.5f).luminance(10).nonOpaque()), true);
    public static final Block MASTER_SWORD_PEDESTAL = reg("master_sword_pedestal", new MasterSwordPedestalBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).strength(-1.0f, 3600000.0f).luminance(s -> s.get(MasterSwordPedestalBlock.HAS_SWORD) ? 8 : 0).nonOpaque()), true);
    public static final Block WARP_PAD_FOREST = reg("warp_pad_forest", new WarpPadBlock(FabricBlockSettings.create().strength(-1.0f, 3600000.0f).luminance(9).nonOpaque(), WarpPadBlock.Warp.FOREST), true);
    public static final Block WARP_PAD_FIRE = reg("warp_pad_fire", new WarpPadBlock(FabricBlockSettings.create().strength(-1.0f, 3600000.0f).luminance(9).nonOpaque(), WarpPadBlock.Warp.FIRE), true);
    public static final Block WARP_PAD_WATER = reg("warp_pad_water", new WarpPadBlock(FabricBlockSettings.create().strength(-1.0f, 3600000.0f).luminance(9).nonOpaque(), WarpPadBlock.Warp.WATER), true);
    public static final Block WARP_PAD_SPIRIT = reg("warp_pad_spirit", new WarpPadBlock(FabricBlockSettings.create().strength(-1.0f, 3600000.0f).luminance(9).nonOpaque(), WarpPadBlock.Warp.SPIRIT), true);
    public static final Block WARP_PAD_SHADOW = reg("warp_pad_shadow", new WarpPadBlock(FabricBlockSettings.create().strength(-1.0f, 3600000.0f).luminance(9).nonOpaque(), WarpPadBlock.Warp.SHADOW), true);
    public static final Block WARP_PAD_LIGHT = reg("warp_pad_light", new WarpPadBlock(FabricBlockSettings.create().strength(-1.0f, 3600000.0f).luminance(9).nonOpaque(), WarpPadBlock.Warp.LIGHT), true);

    private static Block reg(String id, Block block, boolean withItem) {
        Identifier ident = new Identifier(HyruleCraft.MOD_ID, id);
        Registry.register(Registries.BLOCK, ident, block);
        if (withItem) BLOCK_ITEMS.add(Registry.register(Registries.ITEM, ident, new BlockItem(block, new FabricItemSettings())));
        return block;
    }

    public static void init() {
        FlammableBlockRegistry.getDefaultInstance().add(DEKU_WEB, 100, 100);
    }
}
