package com.hyrulecraft.entity;

import com.hyrulecraft.registry.ModEntities;
import com.hyrulecraft.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Bumerán: vuela 14 ticks, vuelve al dueño, aturde y recoge objetos del suelo. */
public class BoomerangEntity extends ThrownItemEntity {
    private int life;
    private boolean returning;

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, World world) { super(type, world); }

    public BoomerangEntity(World world, LivingEntity owner) {
        super(ModEntities.BOOMERANG, owner, world);
        setItem(new ItemStack(ModItems.BOOMERANG));
        setNoGravity(true);
    }

    @Override protected Item getDefaultItem() { return ModItems.BOOMERANG; }

    @Override
    public void tick() {
        baseTick();
        Entity owner = getOwner();
        Vec3d vel = getVelocity();
        life++;
        if (!getWorld().isClient) {
            if (life > 14) returning = true;
            if (returning) {
                if (owner == null || !owner.isAlive() || life > 120) { finish(); return; }
                Vec3d to = owner.getEyePos().subtract(getPos());
                if (to.length() < 1.4) { finish(); return; }
                vel = to.normalize().multiply(1.0);
                setVelocity(vel);
            }
            HitResult hit = ProjectileUtil.getCollision(this, e -> e != owner && e.canHit() && !e.isSpectator());
            if (hit.getType() == HitResult.Type.ENTITY) {
                Entity t = ((EntityHitResult) hit).getEntity();
                if (t instanceof LivingEntity le && !returning) {
                    le.damage(getDamageSources().thrown(this, owner), 3.0F);
                    le.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 4));
                    returning = true;
                }
            } else if (hit.getType() == HitResult.Type.BLOCK) {
                returning = true;
            }
            for (ItemEntity item : getWorld().getEntitiesByClass(ItemEntity.class, getBoundingBox().expand(0.8), i -> true)) {
                item.setVelocity(vel);
                item.velocityModified = true;
            }
        }
        setPosition(getPos().add(vel));
        setYaw(getYaw() + 40);
    }

    private void finish() {
        if (getOwner() instanceof PlayerEntity p) p.getItemCooldownManager().set(ModItems.BOOMERANG, 0);
        discard();
    }

    @Override protected void onCollision(HitResult hitResult) {}
    @Override public void readCustomDataFromNbt(NbtCompound nbt) { life = nbt.getInt("Life"); returning = nbt.getBoolean("Returning"); super.readCustomDataFromNbt(nbt); }
    @Override public void writeCustomDataToNbt(NbtCompound nbt) { nbt.putInt("Life", life); nbt.putBoolean("Returning", returning); super.writeCustomDataToNbt(nbt); }
}
