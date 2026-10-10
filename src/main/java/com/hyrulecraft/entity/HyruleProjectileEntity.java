package com.hyrulecraft.entity;

import com.hyrulecraft.registry.ModEntities;
import com.hyrulecraft.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Proyectil genérico: ENERGY (bola de Ganondorf, se devuelve con espada o escudo),
 * FLASH (Nuez Deku del jugador: aturde al explotar).
 */
public class HyruleProjectileEntity extends ThrownItemEntity {
    public enum Kind { ENERGY, FLASH }

    private Kind kind = Kind.ENERGY;
    private float damage = 6f;
    private boolean reflected;

    public HyruleProjectileEntity(EntityType<? extends HyruleProjectileEntity> type, World world) { super(type, world); }

    public HyruleProjectileEntity(World world, LivingEntity owner, Kind kind, float damage) {
        super(ModEntities.PROJECTILE, owner, world);
        this.kind = kind;
        this.damage = damage;
        setItem(new ItemStack(kind == Kind.ENERGY ? ModItems.ENERGY_BALL : ModItems.DEKU_NUT));
    }

    @Override protected Item getDefaultItem() { return ModItems.ENERGY_BALL; }
    @Override protected float getGravity() { return kind == Kind.ENERGY ? 0.0F : 0.03F; }

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient) {
            if (kind == Kind.ENERGY) ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.WITCH, getX(), getY(), getZ(), 2, 0.05, 0.05, 0.05, 0);
            if (age > 200) discard();
        }
    }

    private boolean canReflect(PlayerEntity p) {
        if (kind != Kind.ENERGY) return false;
        return p.isBlocking() || (p.handSwinging && p.getMainHandStack().getItem() instanceof SwordItem);
    }

    @Override
    protected void onCollision(HitResult hit) {
        World w = getWorld();
        if (hit.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hit).getEntity();
            if (target == getOwner()) return;
            if (target instanceof PlayerEntity p && canReflect(p)) {
                Vec3d look = p.getRotationVec(1.0F);
                setVelocity(look.multiply(1.7));
                setPosition(getPos().add(look.multiply(1.5)));
                setOwner(p);
                reflected = true;
                velocityModified = true;
                w.playSound(null, getBlockPos(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.HOSTILE, 1.0F, 1.5F);
                return;
            }
            if (!w.isClient) {
                if (target instanceof GanondorfEntity g && reflected) g.onEnergyBallReflected();
                else if (kind == Kind.ENERGY) target.damage(getDamageSources().thrown(this, getOwner()), damage);
            }
        }
        if (!w.isClient) {
            if (kind == Kind.FLASH) flash((ServerWorld) w);
            discard();
        }
    }

    private void flash(ServerWorld w) {
        w.spawnParticles(ParticleTypes.FLASH, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        w.playSound(null, getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 1.0F, 1.5F);
        for (LivingEntity e : w.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(4.0), t -> t != getOwner())) {
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 5));
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 2));
            if (e instanceof net.minecraft.entity.mob.MobEntity m) m.setTarget(null);
        }
    }

    @Override public void readCustomDataFromNbt(NbtCompound nbt) { super.readCustomDataFromNbt(nbt); kind = Kind.values()[Math.floorMod(nbt.getInt("Kind"), 2)]; damage = nbt.getFloat("Damage"); }
    @Override public void writeCustomDataToNbt(NbtCompound nbt) { super.writeCustomDataToNbt(nbt); nbt.putInt("Kind", kind.ordinal()); nbt.putFloat("Damage", damage); }
}
