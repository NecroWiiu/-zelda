package com.hyrulecraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/** Tira del jugador hacia el punto donde se enganchó el Gancho. */
public final class HookshotManager {
    private HookshotManager() {}

    private static final class Pull {
        final Vec3d target;
        int ticks;
        Pull(Vec3d t) { target = t; }
    }

    private static final Map<UUID, Pull> PULLS = new HashMap<>();

    public static void start(ServerPlayerEntity player, Vec3d target) {
        PULLS.put(player.getUuid(), new Pull(target));
    }

    public static void clear(UUID id) { PULLS.remove(id); }

    public static void tick(MinecraftServer server) {
        if (PULLS.isEmpty()) return;
        Iterator<Map.Entry<UUID, Pull>> it = PULLS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Pull> e = it.next();
            PlayerEntity p = server.getPlayerManager().getPlayer(e.getKey());
            Pull pull = e.getValue();
            if (p == null || !p.isAlive() || ++pull.ticks > 45) { it.remove(); continue; }
            Vec3d to = pull.target.subtract(p.getEyePos());
            double d = to.length();
            if (d < 1.9 || p.isSneaking()) { it.remove(); continue; }
            Vec3d v = to.normalize().multiply(1.15);
            p.setVelocity(v);
            p.velocityModified = true;
            p.fallDistance = 0;
        }
    }
}
