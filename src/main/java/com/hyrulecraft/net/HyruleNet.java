package com.hyrulecraft.net;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.ocarina.OcarinaEffects;
import com.hyrulecraft.ocarina.OcarinaSong;
import com.hyrulecraft.registry.ModItems;
import com.hyrulecraft.registry.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Paquetes cliente↔servidor del mod. */
public final class HyruleNet {
    private HyruleNet() {}

    public static final Identifier DODGE = new Identifier(HyruleCraft.MOD_ID, "dodge");
    public static final Identifier NOTE = new Identifier(HyruleCraft.MOD_ID, "note");
    public static final Identifier SONG = new Identifier(HyruleCraft.MOD_ID, "song");
    public static final Identifier ANIM = new Identifier(HyruleCraft.MOD_ID, "anim");

    /** Tipos de animación: 0 voltereta, 1 salto atrás, 2 salto lateral izq., 3 salto lateral der., 4 giro de espada. */
    public static final int ANIM_SPIN = 4;

    private static final Map<UUID, Long> LAST_DODGE = new HashMap<>();

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(DODGE, (server, player, handler, buf, sender) -> {
            int type = buf.readByte();
            server.execute(() -> {
                if (type < 0 || type > 3 || !player.isAlive() || player.isSpectator()) return;
                long now = server.getTicks();
                Long last = LAST_DODGE.get(player.getUuid());
                if (last != null && now - last < 12) return;
                LAST_DODGE.put(player.getUuid(), now);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9, 4, false, false, false));
                player.fallDistance = 0;
                player.addExhaustion(0.15F);
                player.getServerWorld().playSound(null, player.getBlockPos(), ModSounds.ROLL, SoundCategory.PLAYERS, 0.8F, 1.0F);
                broadcastAnim(player, type, false);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(NOTE, (server, player, handler, buf, sender) -> {
            int note = buf.readByte();
            server.execute(() -> {
                if (note < 0 || note > 4 || !OcarinaEffects.hasOcarina(player)) return;
                player.getServerWorld().playSound(player, player.getBlockPos(), ModSounds.NOTES[note], SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.getServerWorld().spawnParticles(ParticleTypes.NOTE, player.getX(), player.getY() + 2.2, player.getZ(), 1, 0, 0, 0, note / 24.0);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SONG, (server, player, handler, buf, sender) -> {
            OcarinaSong song = OcarinaSong.byOrdinal(buf.readByte());
            server.execute(() -> {
                if (song == null || !OcarinaEffects.hasOcarina(player)) return;
                if (player.getItemCooldownManager().isCoolingDown(ModItems.OCARINA)) return;
                player.getItemCooldownManager().set(ModItems.OCARINA, 40);
                OcarinaEffects.perform(player, song);
            });
        });
    }

    /** Envía la animación a los jugadores cercanos (y al propio si includeSelf). */
    public static void broadcastAnim(ServerPlayerEntity player, int type, boolean includeSelf) {
        for (ServerPlayerEntity other : PlayerLookup.tracking(player)) sendAnim(other, player, type);
        if (includeSelf) sendAnim(player, player, type);
    }

    private static void sendAnim(ServerPlayerEntity to, ServerPlayerEntity who, int type) {
        PacketByteBuf out = PacketByteBufs.create();
        out.writeVarInt(who.getId());
        out.writeByte(type);
        ServerPlayNetworking.send(to, ANIM, out);
    }
}
