package com.hyrulecraft.magic;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Guarda una instantánea por segundo (10 s) para la Canción del Tiempo. */
public final class TimeRewind {
    private TimeRewind() {}

    private record Snap(ServerWorld world, double x, double y, double z, float yaw, float pitch, float health) {}
    private static final Map<UUID, Deque<Snap>> HISTORY = new HashMap<>();

    public static void tick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0) return;
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (!p.isAlive()) continue;
            Deque<Snap> d = HISTORY.computeIfAbsent(p.getUuid(), k -> new ArrayDeque<>());
            d.addLast(new Snap(p.getServerWorld(), p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch(), p.getHealth()));
            while (d.size() > 10) d.removeFirst();
        }
    }

    public static boolean rewind(ServerPlayerEntity p) {
        Deque<Snap> d = HISTORY.get(p.getUuid());
        if (d == null || d.isEmpty()) return false;
        Snap s = d.peekFirst();
        p.teleport(s.world(), s.x(), s.y(), s.z(), s.yaw(), s.pitch());
        p.setHealth(Math.max(p.getHealth(), Math.min(s.health(), p.getMaxHealth())));
        p.fallDistance = 0;
        d.clear();
        return true;
    }

    public static void forget(UUID id) { HISTORY.remove(id); }
}
