package com.hyrulecraft.item;

import com.hyrulecraft.registry.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Martillo Megatón: click derecho = golpe al suelo (daño en área, rompe bloques frágiles). */
public class MegatonHammerItem extends SwordItem {
    public MegatonHammerItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.pass(stack);
        if (!world.isClient) {
            ServerWorld sw = (ServerWorld) world;
            double r = 4.0;
            List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(r, 1.5, r),
                    e -> e != user && e.isAlive() && !user.isTeammate(e));
            for (LivingEntity e : list) {
                e.damage(user.getDamageSources().playerAttack(user), 8.0F);
                e.takeKnockback(1.2, user.getX() - e.getX(), user.getZ() - e.getZ());
            }
            BlockPos c = user.getBlockPos();
            for (BlockPos p : BlockPos.iterate(c.add(-3, -2, -3), c.add(3, 1, 3))) {
                BlockState s = world.getBlockState(p);
                if (s.isIn(ModTags.HAMMER_BREAKABLE)) world.breakBlock(p, true, user);
            }
            sw.spawnParticles(ParticleTypes.EXPLOSION, user.getX(), user.getY() + 0.2, user.getZ(), 3, 1.5, 0.1, 1.5, 0.0);
            world.playSound(null, c, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.8F, 1.4F);
            user.getItemCooldownManager().set(this, 40);
            stack.damage(3, user, p -> p.sendToolBreakStatus(hand));
        }
        user.swingHand(hand);
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.hammer").formatted(Formatting.GRAY));
    }
}
