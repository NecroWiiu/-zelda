package com.hyrulecraft.entity;

import com.hyrulecraft.registry.ModItems;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** Skulltula: araña trepadora. 12 % son doradas y sueltan una Ficha de Skulltula Dorada. */
public class SkulltulaEntity extends SpiderEntity {
    private static final TrackedData<Boolean> GOLD = DataTracker.registerData(SkulltulaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public SkulltulaEntity(EntityType<? extends SkulltulaEntity> type, World world) { super(type, world); }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return SpiderEntity.createSpiderAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(GOLD, false);
    }

    public boolean isGold() { return dataTracker.get(GOLD); }
    public void setGold(boolean gold) { dataTracker.set(GOLD, gold); }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason reason,
                                 @Nullable EntityData data, @Nullable NbtCompound nbt) {
        EntityData d = super.initialize(world, difficulty, reason, data, nbt);
        if (random.nextFloat() < 0.12F) setGold(true);
        return d;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!getWorld().isClient && isGold()) dropStack(new ItemStack(ModItems.GOLD_SKULLTULA_TOKEN));
    }

    @Override public void writeCustomDataToNbt(NbtCompound nbt) { super.writeCustomDataToNbt(nbt); nbt.putBoolean("Gold", isGold()); }
    @Override public void readCustomDataFromNbt(NbtCompound nbt) { super.readCustomDataFromNbt(nbt); setGold(nbt.getBoolean("Gold")); }
}
