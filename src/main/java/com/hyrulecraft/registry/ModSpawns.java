package com.hyrulecraft.registry;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.tag.BiomeTags;

/** Aparición natural de enemigos de Hyrule en el Overworld. */
public final class ModSpawns {
    private ModSpawns() {}

    public static void init() {
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, ModEntities.STALFOS, 6, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, ModEntities.REDEAD, 4, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_FOREST), SpawnGroup.MONSTER, ModEntities.SKULLTULA, 8, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), SpawnGroup.MONSTER, ModEntities.SKULLTULA, 8, 1, 2);
    }
}
