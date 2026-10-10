package com.hyrulecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/** Plataforma de Gran Hada: cura a quien se pare encima. */
public class FairyFountainBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);

    public FairyFountainBlock(Settings settings) { super(settings); }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof LivingEntity le && entity.age % 10 == 0) {
            le.heal(1.0F);
            le.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 1, true, false));
            ((ServerWorld) world).spawnParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + 1.0, entity.getZ(), 1, 0.3, 0.3, 0.3, 0);
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
