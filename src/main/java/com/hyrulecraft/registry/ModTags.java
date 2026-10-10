package com.hyrulecraft.registry;

import com.hyrulecraft.HyruleCraft;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModTags {
    private ModTags() {}
    /** Bloques que rompe el Martillo Megatón. */
    public static final TagKey<Block> HAMMER_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, new Identifier(HyruleCraft.MOD_ID, "hammer_breakable"));
    /** Bloques que revientan con bombas, Fuego de Din o explosiones. */
    public static final TagKey<Block> BOMBABLE = TagKey.of(RegistryKeys.BLOCK, new Identifier(HyruleCraft.MOD_ID, "bombable"));
}
