package com.hyrulecraft.entity;

import com.hyrulecraft.registry.ModItems;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

/** Stalfos: guerrero esquelético que salta sobre ti con su espada. */
public class StalfosEntity extends HostileEntity {
    public StalfosEntity(EntityType<? extends StalfosEntity> type, World world) { super(type, world); }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.27)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 28.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(2, new LeapAtTargetGoal(this, 0.45F));
        goalSelector.add(3, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
        goalSelector.add(7, new LookAroundGoal(this));
        targetSelector.add(1, new RevengeGoal(this));
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty difficulty) {
        equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.KOKIRI_SWORD));
        if (random.nextFloat() < 0.4F) equipStack(EquipmentSlot.OFFHAND, new ItemStack(ModItems.DEKU_SHIELD));
    }

    @Override public EntityGroup getGroup() { return EntityGroup.UNDEAD; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_SKELETON_AMBIENT; }
    @Override protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) { return SoundEvents.ENTITY_SKELETON_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_SKELETON_DEATH; }
}
