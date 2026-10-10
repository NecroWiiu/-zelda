package com.hyrulecraft.entity;

import com.hyrulecraft.registry.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

/**
 * Jefe final. Lanza esferas de energía: devuélvelas con la espada (golpeando) o el escudo.
 * Si le aciertan queda aturdido y recibe daño extra.
 */
public class GanondorfEntity extends HostileEntity {
    private final ServerBossBar bossBar = new ServerBossBar(getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
    private int stun;

    public GanondorfEntity(EntityType<? extends GanondorfEntity> type, World world) {
        super(type, world);
        setPersistent();
        experiencePoints = 100;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 220.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true));
        goalSelector.add(5, new WanderAroundFarGoal(this, 0.6));
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 24.0F));
        targetSelector.add(1, new RevengeGoal(this));
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty difficulty) {
        equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BIGGORON_SWORD));
    }

    public void onEnergyBallReflected() {
        stun = 100;
        damage(getDamageSources().magic(), 25.0F);
        if (getWorld() instanceof ServerWorld sw) sw.spawnParticles(ParticleTypes.CRIT, getX(), getBodyY(0.6), getZ(), 40, 0.5, 0.8, 0.5, 0.3);
    }

    @Override protected boolean isImmobile() { return super.isImmobile() || stun > 0; }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (stun > 0) amount *= 1.5F;
        return super.damage(source, amount);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        bossBar.setPercent(getHealth() / getMaxHealth());
        if (stun > 0) { stun--; return; }
        LivingEntity t = getTarget();
        if (t == null) return;
        boolean enraged = getHealth() < getMaxHealth() * 0.5F;
        if (age % (enraged ? 60 : 90) == 0 && distanceTo(t) > 4 && canSee(t)) shoot(t, enraged ? 3 : 1);
        if (enraged && age % 240 == 0 && distanceTo(t) > 6) {
            Vec3d behind = t.getPos().subtract(t.getRotationVec(1.0F).multiply(2.0, 0, 2.0));
            teleport(behind.x, t.getY(), behind.z, true);
        }
    }

    private void shoot(LivingEntity t, int count) {
        for (int i = 0; i < count; i++) {
            HyruleProjectileEntity ball = new HyruleProjectileEntity(getWorld(), this, HyruleProjectileEntity.Kind.ENERGY, 7.0F);
            ball.setPosition(getX(), getEyeY() - 0.2, getZ());
            double dx = t.getX() - getX(), dy = t.getBodyY(0.5) - ball.getY(), dz = t.getZ() - getZ();
            ball.setVelocity(dx, dy, dz, 1.1F, count > 1 ? 8.0F : 0.5F);
            getWorld().spawnEntity(ball);
        }
    }

    @Override public void onStartedTrackingBy(ServerPlayerEntity player) { super.onStartedTrackingBy(player); bossBar.addPlayer(player); }
    @Override public void onStoppedTrackingBy(ServerPlayerEntity player) { super.onStoppedTrackingBy(player); bossBar.removePlayer(player); }
    @Override public void setCustomName(@org.jetbrains.annotations.Nullable Text name) { super.setCustomName(name); bossBar.setName(getDisplayName()); }
    @Override public boolean canImmediatelyDespawn(double d) { return false; }
    @Override public void writeCustomDataToNbt(NbtCompound nbt) { super.writeCustomDataToNbt(nbt); nbt.putInt("Stun", stun); }
    @Override public void readCustomDataFromNbt(NbtCompound nbt) { super.readCustomDataFromNbt(nbt); stun = nbt.getInt("Stun"); if (hasCustomName()) bossBar.setName(getDisplayName()); }
}
