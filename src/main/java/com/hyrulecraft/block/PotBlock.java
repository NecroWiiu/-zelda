package com.hyrulecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/** Vasija: se rompe de un golpe y suelta rupias o corazones. */
public class PotBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 14, 13);

    public PotBlock(Settings settings) { super(settings); }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient) world.breakBlock(hit.getBlockPos(), true, projectile);
    }
}
