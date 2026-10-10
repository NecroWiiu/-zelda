package com.hyrulecraft.item;

import com.hyrulecraft.net.HyruleNet;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
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
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Espada de Hyrule:
 *  - Ataque giratorio: mantén click derecho ~1 s y suelta (si no llevas escudo en la mano izquierda).
 *  - Tajo en salto: golpea mientras caes para hacer daño extra.
 */
public class HyruleSwordItem extends SwordItem {
    public static final int CHARGE_TICKS = 20;
    private final float spinDamage, spinRadius, jumpBonus;

    public HyruleSwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings,
                           float spinDamage, float spinRadius, float jumpBonus) {
        super(material, attackDamage, attackSpeed, settings);
        this.spinDamage = spinDamage; this.spinRadius = spinRadius; this.jumpBonus = jumpBonus;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity p && !p.getWorld().isClient && !p.isOnGround()
                && p.fallDistance > 0.3F && !p.isTouchingWater() && !p.hasVehicle() && jumpBonus > 0) {
            target.timeUntilRegen = 0;
            target.damage(p.getDamageSources().playerAttack(p), jumpBonus);
            ((ServerWorld) p.getWorld()).spawnParticles(ParticleTypes.CRIT, target.getX(), target.getBodyY(0.5), target.getZ(), 14, 0.3, 0.4, 0.3, 0.25);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.SPEAR; }
    @Override public int getMaxUseTime(ItemStack stack) { return 72000; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (spinDamage <= 0 || user.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.pass(stack);
        if (user.getOffHandStack().getItem() instanceof ShieldItem) return TypedActionResult.pass(stack);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remaining) {
        int used = getMaxUseTime(stack) - remaining;
        if (used == CHARGE_TICKS && !world.isClient) {
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0F, 1.7F);
            ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, user.getX(), user.getBodyY(0.6), user.getZ(), 14, 0.4, 0.5, 0.4, 0.05);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remaining) {
        if (!(user instanceof PlayerEntity player) || world.isClient) return;
        if (getMaxUseTime(stack) - remaining < CHARGE_TICKS) return;
        ServerWorld sw = (ServerWorld) world;
        Box box = user.getBoundingBox().expand(spinRadius, 0.6, spinRadius);
        List<LivingEntity> hits = world.getEntitiesByClass(LivingEntity.class, box,
                t -> t != user && t.isAlive() && !(t instanceof ArmorStandEntity) && !user.isTeammate(t));
        for (LivingEntity e : hits) {
            if (e.squaredDistanceTo(user) > spinRadius * spinRadius) continue;
            e.damage(player.getDamageSources().playerAttack(player), spinDamage);
            e.takeKnockback(0.9, user.getX() - e.getX(), user.getZ() - e.getZ());
        }
        for (int i = 0; i < 16; i++) {
            double a = i * Math.PI / 8.0;
            sw.spawnParticles(ParticleTypes.SWEEP_ATTACK, user.getX() + Math.cos(a) * spinRadius * 0.8,
                    user.getBodyY(0.5), user.getZ() + Math.sin(a) * spinRadius * 0.8, 1, 0, 0, 0, 0);
        }
        if (player instanceof ServerPlayerEntity sp) HyruleNet.broadcastAnim(sp, HyruleNet.ANIM_SPIN, true);
        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.3F, 0.75F);
        player.getItemCooldownManager().set(this, 30);
        Hand hand = user.getActiveHand();
        stack.damage(2, user, p -> p.sendToolBreakStatus(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (spinDamage > 0) tooltip.add(Text.translatable("tooltip.hyrulecraft.spin_attack").formatted(Formatting.GRAY));
        if (jumpBonus > 0) tooltip.add(Text.translatable("tooltip.hyrulecraft.jump_slash").formatted(Formatting.GRAY));
    }
}
