package com.hyrulecraft.entity;

import com.hyrulecraft.registry.ModEntities;
import com.hyrulecraft.registry.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Bomba lanzada: rebota, parpadea y estalla. No destruye terreno, solo bloques con el tag bombable. */
public class BombEntity extends Entity {
    private int fuse = 50;
    private LivingEntity owner;

    public BombEntity(EntityType<? extends BombEntity> type, World world) { super(type, world); }

    public BombEntity(World world, double x, double y, double z, LivingEntity owner) {
        this(ModEntities.BOMB, world);
        setPosition(x, y, z);
        this.owner = owner;
    }

    public int getFuse() { return fuse; }

    @Override protected void initDataTracker() {}
    @Override public boolean canHit() { return !isRemoved(); }

    @Override
    public void tick() {
        if (!hasNoGravity()) setVelocity(getVelocity().add(0, -0.04, 0));
        move(MovementType.SELF, getVelocity());
        setVelocity(getVelocity().multiply(0.98));
        if (isOnGround()) setVelocity(getVelocity().multiply(0.7, -0.5, 0.7));
        if (--fuse <= 0) {
            discard();
            if (!getWorld().isClient) explode();
        } else if (getWorld().isClient) {
            getWorld().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.7, getZ(), 0, 0, 0);
            if (fuse < 15 && fuse % 3 == 0) getWorld().addParticle(ParticleTypes.FLAME, getX(), getY() + 0.7, getZ(), 0, 0.02, 0);
        }
    }

    private void explode() {
        World w = getWorld();
        w.createExplosion(this, getX(), getBodyY(0.0625), getZ(), 2.2F, World.ExplosionSourceType.NONE);
        BlockPos c = getBlockPos();
        for (BlockPos p : BlockPos.iterate(c.add(-3, -3, -3), c.add(3, 3, 3))) {
            if (w.getBlockState(p).isIn(ModTags.BOMBABLE) && p.getSquaredDistance(c) <= 12) w.breakBlock(p, true, owner);
        }
    }

    @Override protected void readCustomDataFromNbt(NbtCompound nbt) { fuse = nbt.getShort("Fuse"); }
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) { nbt.putShort("Fuse", (short) fuse); }
}
