package com.hyrulecraft.registry;

import com.hyrulecraft.HyruleCraft;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModParticles {
    private ModParticles() {}
    public static final DefaultParticleType NAVI = FabricParticleTypes.simple();

    public static void init() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(HyruleCraft.MOD_ID, "navi"), NAVI);
    }
}
