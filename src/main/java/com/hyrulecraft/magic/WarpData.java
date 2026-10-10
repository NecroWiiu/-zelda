package com.hyrulecraft.magic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Puntos de teletransporte por jugador (plataformas + marca de Viento de Farore). */
public class WarpData extends PersistentState {
    public record Loc(String dim, double x, double y, double z, float yaw) {}

    private final Map<UUID, Map<String, Loc>> data = new HashMap<>();

    public static WarpData get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(WarpData::fromNbt, WarpData::new, "hyrulecraft_warps");
    }

    public void set(UUID player, String key, Loc loc) {
        data.computeIfAbsent(player, k -> new HashMap<>()).put(key, loc);
        markDirty();
    }

    public Loc get(UUID player, String key) {
        Map<String, Loc> m = data.get(player);
        return m == null ? null : m.get(key);
    }

    public void remove(UUID player, String key) {
        Map<String, Loc> m = data.get(player);
        if (m != null && m.remove(key) != null) markDirty();
    }

    /** Teletransporta al jugador. Devuelve false si no hay punto guardado. */
    public static boolean teleport(ServerPlayerEntity player, String key, boolean consume) {
        WarpData wd = get(player.server);
        Loc l = wd.get(player.getUuid(), key);
        if (l == null) return false;
        RegistryKey<World> dim = RegistryKey.of(RegistryKeys.WORLD, new Identifier(l.dim()));
        ServerWorld target = player.server.getWorld(dim);
        if (target == null) return false;
        player.teleport(target, l.x(), l.y(), l.z(), l.yaw(), player.getPitch());
        player.fallDistance = 0;
        if (consume) wd.remove(player.getUuid(), key);
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound all = new NbtCompound();
        data.forEach((uuid, map) -> {
            NbtCompound p = new NbtCompound();
            map.forEach((k, l) -> {
                NbtCompound c = new NbtCompound();
                c.putString("dim", l.dim()); c.putDouble("x", l.x()); c.putDouble("y", l.y());
                c.putDouble("z", l.z()); c.putFloat("yaw", l.yaw());
                p.put(k, c);
            });
            all.put(uuid.toString(), p);
        });
        nbt.put("players", all);
        return nbt;
    }

    public static WarpData fromNbt(NbtCompound nbt) {
        WarpData wd = new WarpData();
        NbtCompound all = nbt.getCompound("players");
        for (String u : all.getKeys()) {
            NbtCompound p = all.getCompound(u);
            Map<String, Loc> map = new HashMap<>();
            for (String k : p.getKeys()) {
                NbtCompound c = p.getCompound(k);
                map.put(k, new Loc(c.getString("dim"), c.getDouble("x"), c.getDouble("y"), c.getDouble("z"), c.getFloat("yaw")));
            }
            wd.data.put(UUID.fromString(u), map);
        }
        return wd;
    }
}
