package com.hyrulecraft.ocarina;

import com.hyrulecraft.block.MasterSwordPedestalBlock;
import com.hyrulecraft.magic.TimeRewind;
import com.hyrulecraft.magic.WarpData;
import com.hyrulecraft.registry.ModItems;
import com.hyrulecraft.registry.ModSounds;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Lo que hace cada canción en Minecraft (se ejecuta siempre en el servidor). */
public final class OcarinaEffects {
    private OcarinaEffects() {}

    public static void perform(ServerPlayerEntity p, OcarinaSong song) {
        ServerWorld w = p.getServerWorld();
        w.playSound(null, p.getBlockPos(), ModSounds.SONG_OK, SoundCategory.PLAYERS, 1.0F, 1.0F);
        w.spawnParticles(ParticleTypes.NOTE, p.getX(), p.getY() + 2.2, p.getZ(), 6, 0.4, 0.2, 0.4, 1.0);
        p.sendMessage(Text.translatable("song.hyrulecraft." + song.id), true);

        switch (song) {
            case ZELDAS_LULLABY -> {
                for (PlayerEntity o : w.getEntitiesByClass(PlayerEntity.class, p.getBoundingBox().expand(8), x -> true)) {
                    o.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 160, 1));
                }
                for (HostileEntity m : w.getEntitiesByClass(HostileEntity.class, p.getBoundingBox().expand(12), x -> true)) {
                    m.setTarget(null);
                    m.getNavigation().stop();
                    m.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
                }
            }
            case EPONAS_SONG -> {
                for (HorseEntity old : w.getEntitiesByClass(HorseEntity.class, p.getBoundingBox().expand(128),
                        h -> h.hasCustomName() && "Epona".equals(h.getCustomName().getString()) && p.getUuid().equals(h.getOwnerUuid()))) {
                    old.discard();
                }
                HorseEntity h = EntityType.HORSE.create(w);
                if (h != null) {
                    h.refreshPositionAndAngles(p.getX() + 2, p.getY(), p.getZ(), p.getYaw(), 0);
                    h.setTame(true);
                    h.setOwnerUuid(p.getUuid());
                    h.setVariant(HorseColor.CHESTNUT, HorseMarking.WHITE_FIELD);
                    h.setCustomName(Text.literal("Epona"));
                    h.saddle(SoundCategory.NEUTRAL);
                    h.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3375);
                    h.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(0.9);
                    w.spawnEntity(h);
                }
            }
            case SARIAS_SONG -> {
                for (LivingEntity e : w.getEntitiesByClass(LivingEntity.class, p.getBoundingBox().expand(32),
                        t -> t != p && !(t instanceof ArmorStandEntity))) {
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 240, 0, true, false));
                }
            }
            case SUNS_SONG -> {
                long t = w.getTimeOfDay() % 24000L;
                w.setTimeOfDay(t < 12000L ? 18000L : 6000L);
                for (MobEntity m : w.getEntitiesByClass(MobEntity.class, p.getBoundingBox().expand(24), x -> x.getGroup() == EntityGroup.UNDEAD)) {
                    m.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 9));
                    m.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 3));
                }
            }
            case SONG_OF_TIME -> {
                BlockPos c = p.getBlockPos();
                BlockPos found = null;
                for (BlockPos b : BlockPos.iterate(c.add(-6, -3, -6), c.add(6, 3, 6))) {
                    if (w.getBlockState(b).getBlock() instanceof MasterSwordPedestalBlock) { found = b.toImmutable(); break; }
                }
                if (found != null) MasterSwordPedestalBlock.tryGive(p, w, found, w.getBlockState(found));
                else if (!TimeRewind.rewind(p)) p.sendMessage(Text.translatable("message.hyrulecraft.rewind_none"), true);
            }
            case SONG_OF_STORMS -> {
                if (w.isRaining() || w.isThundering()) w.setWeather(12000, 0, false, false);
                else w.setWeather(0, 6000, true, true);
            }
            default -> {
                if (song.warpKey != null && !WarpData.teleport(p, song.warpKey, false)) {
                    p.sendMessage(Text.translatable("message.hyrulecraft.warp_unknown"), true);
                }
            }
        }
    }

    public static boolean hasOcarina(PlayerEntity p) {
        for (int i = 0; i < p.getInventory().size(); i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s.isOf(ModItems.OCARINA)) return true;
        }
        return false;
    }
}
