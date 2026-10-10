package com.hyrulecraft.magic;

import com.hyrulecraft.item.SpellItem;
import com.hyrulecraft.registry.ModBlocks;
import com.hyrulecraft.registry.ModTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public final class Spells {
    private Spells() {}

    public static boolean hasMarker(PlayerEntity p) {
        return p instanceof ServerPlayerEntity sp && WarpData.get(sp.server).get(sp.getUuid(), "farore") != null;
    }

    public static void cast(ServerPlayerEntity p, SpellItem.Spell spell) {
        ServerWorld w = p.getServerWorld();
        switch (spell) {
            case DIN -> {
                double r = 6.0;
                for (LivingEntity e : w.getEntitiesByClass(LivingEntity.class, p.getBoundingBox().expand(r),
                        t -> t != p && t.isAlive() && !(t instanceof ArmorStandEntity) && !p.isTeammate(t))) {
                    e.setOnFireFor(8);
                    e.damage(w.getDamageSources().playerAttack(p), 6.0F);
                    e.takeKnockback(0.8, p.getX() - e.getX(), p.getZ() - e.getZ());
                }
                BlockPos c = p.getBlockPos();
                for (BlockPos b : BlockPos.iterate(c.add(-5, -2, -5), c.add(5, 3, 5))) {
                    if (w.getBlockState(b).isOf(ModBlocks.DEKU_WEB)) w.breakBlock(b, false, p);
                }
                for (int i = 0; i < 48; i++) {
                    double a = i * Math.PI / 24.0;
                    w.spawnParticles(ParticleTypes.FLAME, p.getX() + Math.cos(a) * 5, p.getY() + 0.3, p.getZ() + Math.sin(a) * 5, 1, 0.1, 0.4, 0.1, 0.02);
                }
                w.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.5F, 0.6F);
            }
            case NAYRU -> {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 3));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 4));
                w.spawnParticles(ParticleTypes.ENCHANT, p.getX(), p.getBodyY(0.5), p.getZ(), 60, 0.6, 0.9, 0.6, 0.6);
                w.playSound(null, p.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.2F, 1.2F);
            }
            
            default -> {}
        }
        if (spell == SpellItem.Spell.FARORE) {
            WarpData wd = WarpData.get(p.server);
            WarpData.Loc mark = wd.get(p.getUuid(), "farore");
            if (mark != null && WarpData.teleport(p, "farore", true)) {
                w.playSound(null, p.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.3F);
            } else {
                wd.set(p.getUuid(), "farore", new WarpData.Loc(w.getRegistryKey().getValue().toString(), p.getX(), p.getY(), p.getZ(), p.getYaw()));
                for (int i = 0; i < 20; i++) w.spawnParticles(ParticleTypes.HAPPY_VILLAGER, p.getX(), p.getY() + i * 0.2, p.getZ(), 2, 0.1, 0, 0.1, 0);
                w.playSound(null, p.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1.0F, 1.6F);
                p.sendMessage(Text.translatable("message.hyrulecraft.farore_set"), true);
            }
        }
    }
}
