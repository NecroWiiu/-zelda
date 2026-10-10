package com.hyrulecraft.client;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.registry.ModParticles;
import com.hyrulecraft.registry.ModSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Z-Targeting: fija la cámara en un enemigo, caminas en círculos a su alrededor y aparece la flecha amarilla. */
public final class ZTargeting {
    private ZTargeting() {}

    private static final Identifier MARKER = new Identifier(HyruleCraft.MOD_ID, "textures/gui/z_marker.png");
    private static final double MAX_DIST = 24.0;
    private static LivingEntity target;
    private static boolean wasDown;
    private static float letterbox;

    public static boolean isLocked() { return target != null; }
    public static LivingEntity target() { return target; }
    public static float letterbox() { return letterbox; }

    public static void tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.world == null || mc.currentScreen != null || p.isSpectator()) {
            target = null;
            wasDown = false;
            letterbox *= 0.7F;
            return;
        }
        boolean down = HyruleCraftClient.Z_KEY.isPressed();
        if (ClientConfig.toggleTarget) {
            while (HyruleCraftClient.Z_KEY.wasPressed()) {
                if (target != null) target = null; else acquire(mc, null);
            }
        } else {
            if (down && !wasDown) acquire(mc, null);
            if (!down && wasDown) target = null;
            if (down && target == null && mc.world.getTime() % 10 == 0) acquire(mc, null);
        }
        wasDown = down;
        while (HyruleCraftClient.NEXT_KEY.wasPressed()) {
            if (target != null) acquire(mc, target);
        }
        if (target != null && (!target.isAlive() || target.isRemoved() || target.squaredDistanceTo(p) > MAX_DIST * MAX_DIST)) {
            target = null;
        }
        if (target != null && p.age % 6 == 0) {
            mc.world.addParticle(ModParticles.NAVI, target.getX(), target.getY() + target.getHeight() + 0.9, target.getZ(), 0, 0.01, 0);
        }
        letterbox += ((target != null && ClientConfig.letterbox ? 1.0F : 0.0F) - letterbox) * 0.25F;
    }

    private static void acquire(MinecraftClient mc, LivingEntity exclude) {
        ClientPlayerEntity p = mc.player;
        Vec3d look = p.getRotationVec(1.0F);
        List<LivingEntity> list = new ArrayList<>(mc.world.getEntitiesByClass(LivingEntity.class,
                p.getBoundingBox().expand(MAX_DIST), e -> e != p && e != exclude && e.isAlive() && !(e instanceof ArmorStandEntity)
                        && !(e instanceof PlayerEntity) && !e.isInvisible() && p.squaredDistanceTo(e) <= MAX_DIST * MAX_DIST && p.canSee(e)));
        if (list.isEmpty()) return;
        list.sort(Comparator.comparingDouble(e -> {
            Vec3d to = e.getPos().add(0, e.getHeight() * 0.5, 0).subtract(p.getEyePos()).normalize();
            double angle = Math.acos(MathHelper.clamp(look.dotProduct(to), -1.0, 1.0));
            return angle + p.distanceTo(e) * 0.02 + (e instanceof Monster ? 0 : 1.5);
        }));
        LivingEntity old = target;
        target = list.get(0);
        if (old != target) mc.getSoundManager().play(PositionedSoundInstance.master(ModSounds.Z_TARGET, 1.0F, 0.6F));
    }

    /** Se llama cada fotograma: gira suavemente la vista hacia el objetivo. */
    public static void updateCamera() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (target == null || p == null) return;
        Vec3d from = p.getEyePos();
        Vec3d to = target.getPos().add(0, target.getHeight() * 0.6, 0);
        double dx = to.x - from.x, dy = to.y - from.y, dz = to.z - from.z;
        double h = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (MathHelper.atan2(dz, dx) * 57.29577951308232) - 90.0F;
        float pitch = (float) -(MathHelper.atan2(dy, h) * 57.29577951308232);
        float ny = MathHelper.lerpAngleDegrees(0.35F, p.getYaw(), yaw);
        float np = MathHelper.clamp(MathHelper.lerp(0.35F, p.getPitch(), pitch), -80.0F, 80.0F);
        p.setYaw(ny);
        p.setPitch(np);
        p.prevYaw = ny;
        p.prevPitch = np;
    }

    public static void renderMarker(WorldRenderContext ctx) {
        if (target == null) return;
        Camera cam = ctx.camera();
        Vec3d cp = cam.getPos();
        float td = ctx.tickDelta();
        double x = MathHelper.lerp(td, target.prevX, target.getX()) - cp.x;
        double y = MathHelper.lerp(td, target.prevY, target.getY()) - cp.y + target.getHeight() + 0.55 + Math.sin((target.age + td) * 0.2) * 0.08;
        double z = MathHelper.lerp(td, target.prevZ, target.getZ()) - cp.z;
        MatrixStack m = ctx.matrixStack();
        m.push();
        m.translate(x, y, z);
        m.multiply(cam.getRotation());
        Matrix4f mat = m.peek().getPositionMatrix();
        float s = 0.3F;
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, MARKER);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        Tessellator t = Tessellator.getInstance();
        BufferBuilder bb = t.getBuffer();
        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bb.vertex(mat, -s, -s, 0).texture(0, 1).color(1f, 1f, 1f, 1f).next();
        bb.vertex(mat, s, -s, 0).texture(1, 1).color(1f, 1f, 1f, 1f).next();
        bb.vertex(mat, s, s, 0).texture(1, 0).color(1f, 1f, 1f, 1f).next();
        bb.vertex(mat, -s, s, 0).texture(0, 0).color(1f, 1f, 1f, 1f).next();
        t.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        m.pop();
    }
}
