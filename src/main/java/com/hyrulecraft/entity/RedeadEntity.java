package com.hyrulecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/** ReDead: su grito paraliza y su mordisco te deja inmóvil unos segundos. */
public class RedeadEntity extends HostileEntity {
    public RedeadEntity(EntityType<? extends RedeadEntity> type, World world) { super(type, world); }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 26.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(2, new MeleeAttackGoal(this, 0.9, false));
        goalSelector.add(5, new WanderAroundFarGoal(this, 0.6));
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        goalSelector.add(7, new LookAroundGoal(this));
        targetSelector.add(1, new RevengeGoal(this));
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    private static void paralyze(LivingEntity e, int ticks) {
        e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, ticks, 6));
        e.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, ticks, 3));
        e.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, ticks, 128, false, false));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean ok = super.tryAttack(target);
        if (ok && target instanceof LivingEntity le) paralyze(le, 50);
        return ok;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        LivingEntity t = getTarget();
        if (!getWorld().isClient && age % 120 == 0 && t instanceof PlayerEntity && t.distanceTo(this) < 6 && canSee(t)) {
            paralyze(t, 40);
            getWorld().playSound(null, getBlockPos(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.HOSTILE, 1.2F, 0.5F);
        }
    }

    @Override public EntityGroup getGroup() { return EntityGroup.UNDEAD; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_ZOMBIE_AMBIENT; }
    @Override protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource s) { return SoundEvents.ENTITY_ZOMBIE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_ZOMBIE_DEATH; }
}
