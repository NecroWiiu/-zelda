package com.hyrulecraft.registry;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {
    private ModEntities() {}

    public static final EntityType<StalfosEntity> STALFOS = mob("stalfos", StalfosEntity::new, 0.6f, 1.99f);
    public static final EntityType<RedeadEntity> REDEAD = mob("redead", RedeadEntity::new, 0.6f, 1.95f);
    public static final EntityType<SkulltulaEntity> SKULLTULA = mob("skulltula", SkulltulaEntity::new, 1.2f, 0.8f);
    public static final EntityType<GanondorfEntity> GANONDORF = mob("ganondorf", GanondorfEntity::new, 0.8f, 2.6f);

    public static final EntityType<HyruleProjectileEntity> PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HyruleCraft.MOD_ID, "projectile"),
            FabricEntityTypeBuilder.<HyruleProjectileEntity>create(SpawnGroup.MISC, HyruleProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f, 0.3f)).trackRangeBlocks(64).trackedUpdateRate(5).build());
    public static final EntityType<BoomerangEntity> BOOMERANG = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HyruleCraft.MOD_ID, "boomerang"),
            FabricEntityTypeBuilder.<BoomerangEntity>create(SpawnGroup.MISC, BoomerangEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.4f)).trackRangeBlocks(64).trackedUpdateRate(2).build());
    public static final EntityType<BombEntity> BOMB = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HyruleCraft.MOD_ID, "bomb"),
            FabricEntityTypeBuilder.<BombEntity>create(SpawnGroup.MISC, BombEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeBlocks(64).trackedUpdateRate(2).build());

    private static <T extends MobEntity> EntityType<T> mob(String id, EntityType.EntityFactory<T> factory, float w, float h) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(HyruleCraft.MOD_ID, id),
                FabricEntityTypeBuilder.<T>create(SpawnGroup.MONSTER, factory)
                        .dimensions(EntityDimensions.fixed(w, h)).trackRangeChunks(8).build());
    }

    public static void init() {
        FabricDefaultAttributeRegistry.register(STALFOS, StalfosEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(REDEAD, RedeadEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SKULLTULA, SkulltulaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GANONDORF, GanondorfEntity.createAttributes());
    }
}
