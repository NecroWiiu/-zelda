package com.hyrulecraft.client;

import com.hyrulecraft.net.HyruleNet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

/** Movimientos: voltereta, salto atrás, saltos laterales y giro de espada (animación + control). */
public final class DodgeAnims {
    private DodgeAnims() {}

    private record Anim(int type, int start, int duration) {}
    private static final Map<Integer, Anim> ANIMS = new HashMap<>();

    private static int ticks, cooldown;
    private static Vec3d dir = Vec3d.ZERO;
    private static double speed;

    public static void start(Entity e, int type) {
        if (ANIMS.containsKey(e.getId())) return;
        int dur = type == 0 ? 9 : type == 1 ? 10 : type == 4 ? 8 : 7;
        ANIMS.put(e.getId(), new Anim(type, e.age, dur));
    }

    /** Se llama desde el mixin del renderizador para inclinar/girar el modelo. */
    public static void apply(LivingEntity e, MatrixStack m, float td) {
        Anim a = ANIMS.get(e.getId());
        if (a == null) return;
        float prog = (e.age + td - a.start()) / a.duration();
        if (prog >= 1.0F || prog < 0) { ANIMS.remove(e.getId()); return; }
        float h = e.getHeight() * 0.5F;
        m.translate(0, h, 0);
        switch (a.type()) {
            case 0 -> m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-360F * prog));
            case 1 -> m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(360F * prog));
            case 2 -> m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(25F * (float) Math.sin(Math.PI * prog)));
            case 3 -> m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-25F * (float) Math.sin(Math.PI * prog)));
            default -> m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360F * prog));
        }
        m.translate(0, -h, 0);
    }

    public static void clientTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        if (cooldown > 0) cooldown--;
        if (ticks > 0) {
            Vec3d v = p.getVelocity();
            p.setVelocity(dir.x * speed, v.y, dir.z * speed);
            speed *= 0.93;
            ticks--;
        }
        while (HyruleCraftClient.DODGE_KEY.wasPressed()) {
            if (mc.currentScreen == null) tryDodge(p);
        }
    }

    private static void tryDodge(ClientPlayerEntity p) {
        if (cooldown > 0 || !p.isOnGround() || p.hasVehicle() || p.isTouchingWater() || p.isSpectator() || p.isClimbing()) return;
        float f = p.input.movementForward, s = p.input.movementSideways;
        boolean locked = ZTargeting.isLocked();
        Vec3d fwd = Vec3d.fromPolar(0, p.getYaw()).multiply(1, 0, 1).normalize();
        Vec3d left = new Vec3d(fwd.z, 0, -fwd.x);
        int type;
        if (Math.abs(s) > 0.1F && (locked || Math.abs(s) > Math.abs(f))) type = s > 0 ? 2 : 3;
        else if (f < -0.1F || (locked && f <= 0.1F)) type = 1;
        else type = 0;
        switch (type) {
            case 0 -> { dir = fwd; speed = 0.62; ticks = 7; }
            case 1 -> { dir = fwd.negate(); speed = 0.5; ticks = 5; p.addVelocity(0, 0.42, 0); }
            case 2 -> { dir = left; speed = 0.55; ticks = 5; p.addVelocity(0, 0.25, 0); }
            default -> { dir = left.negate(); speed = 0.55; ticks = 5; p.addVelocity(0, 0.25, 0); }
        }
        cooldown = 14;
        start(p, type);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(type);
        ClientPlayNetworking.send(HyruleNet.DODGE, buf);
    }
}
