package com.hyrulecraft.block;

import com.hyrulecraft.item.QuestItem;
import com.hyrulecraft.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Pedestal de la Espada Maestra: necesita las tres piedras espirituales y la Canción del Tiempo (o clic derecho). */
public class MasterSwordPedestalBlock extends Block {
    public static final BooleanProperty HAS_SWORD = BooleanProperty.of("has_sword");

    public MasterSwordPedestalBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(HAS_SWORD, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(HAS_SWORD); }

    private static boolean has(PlayerEntity p, Item item) {
        for (int i = 0; i < p.getInventory().size(); i++) if (p.getInventory().getStack(i).isOf(item)) return true;
        return false;
    }

    public static boolean hasStones(PlayerEntity p) {
        return has(p, ModItems.KOKIRI_EMERALD) && has(p, ModItems.GORON_RUBY) && has(p, ModItems.ZORA_SAPPHIRE);
    }

    /** Devuelve true si entregó la espada. Mensajes incluidos. */
    public static boolean tryGive(ServerPlayerEntity player, World world, BlockPos pos, BlockState state) {
        if (!state.get(HAS_SWORD)) {
            player.sendMessage(Text.translatable("message.hyrulecraft.pedestal_empty"), true);
            return false;
        }
        if (!hasStones(player)) {
            player.sendMessage(Text.translatable("message.hyrulecraft.pedestal_need_stones"), true);
            return false;
        }
        world.setBlockState(pos, state.with(HAS_SWORD, false));
        player.giveItemStack(new ItemStack(ModItems.MASTER_SWORD));
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 2.0F, 1.0F);
        LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
        if (bolt != null) {
            bolt.refreshPositionAfterTeleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            bolt.setCosmetic(true);
            world.spawnEntity(bolt);
        }
        player.sendMessage(Text.translatable("message.hyrulecraft.pedestal_given"), true);
        return true;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player instanceof ServerPlayerEntity sp) tryGive(sp, world, pos, state);
        return ActionResult.success(world.isClient);
    }
}
