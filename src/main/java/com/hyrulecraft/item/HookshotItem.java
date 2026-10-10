package com.hyrulecraft.item;

import com.hyrulecraft.registry.ModSounds;
import com.hyrulecraft.util.HookshotManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Gancho: alcance 24 bloques. Si engancha un bloque, te arrastra; si engancha una criatura, la trae hacia ti. */
public class HookshotItem extends Item {
    public static final double RANGE = 24.0;

    public HookshotItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.fail(stack);
        if (world.isClient || !(user instanceof ServerPlayerEntity sp)) return TypedActionResult.success(stack, true);

        Vec3d start = user.getEyePos();
        Vec3d dir = user.getRotationVec(1.0F);
        Vec3d end = start.add(dir.multiply(RANGE));
        BlockHitResult bhr = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, user));
        Vec3d blockEnd = bhr.getType() == HitResult.Type.MISS ? end : bhr.getPos();
        Box box = user.getBoundingBox().stretch(dir.multiply(RANGE)).expand(1.0);
        EntityHitResult ehr = ProjectileUtil.raycast(user, start, blockEnd, box,
                e -> !e.isSpectator() && e.canHit() && e instanceof LivingEntity, RANGE * RANGE);

        Vec3d impact = blockEnd;
        if (ehr != null) {
            Entity target = ehr.getEntity();
            impact = ehr.getPos();
            Vec3d pull = user.getPos().subtract(target.getPos()).normalize().multiply(1.4).add(0, 0.25, 0);
            target.setVelocity(pull);
            target.velocityModified = true;
            target.damage(world.getDamageSources().playerAttack(user), 1.0F);
        } else if (bhr.getType() == HitResult.Type.BLOCK) {
            HookshotManager.start(sp, impact);
        }

        ServerWorld sw = (ServerWorld) world;
        double len = impact.distanceTo(start);
        for (double d = 0.8; d < len; d += 0.6) {
            Vec3d p = start.add(dir.multiply(d)).add(0, -0.25, 0);
            sw.spawnParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
        world.playSound(null, user.getBlockPos(), ModSounds.HOOKSHOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        user.getItemCooldownManager().set(this, 20);
        return TypedActionResult.success(stack, false);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hyrulecraft.hookshot").formatted(Formatting.GRAY));
    }
}
