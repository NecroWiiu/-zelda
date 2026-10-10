package com.hyrulecraft.block;

import com.hyrulecraft.magic.WarpData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/** Plataforma de teletransporte: actívala (clic derecho) y luego vuelve con la canción de teletransporte. */
public class WarpPadBlock extends Block {
    public enum Warp {
        FOREST, FIRE, WATER, SPIRIT, SHADOW, LIGHT;
        public String id() { return name().toLowerCase(); }
    }

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    private final Warp warp;

    public WarpPadBlock(Settings settings, Warp warp) {
        super(settings);
        this.warp = warp;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player instanceof ServerPlayerEntity sp) {
            WarpData.get(sp.server).set(sp.getUuid(), warp.id(), new WarpData.Loc(world.getRegistryKey().getValue().toString(),
                    pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() + 0.5, sp.getYaw()));
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.5F);
            sp.sendMessage(Text.translatable("message.hyrulecraft.warp_registered", Text.translatable("warp.hyrulecraft." + warp.id())), true);
        }
        return ActionResult.success(world.isClient);
    }
}
