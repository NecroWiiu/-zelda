package com.hyrulecraft.client;

import com.hyrulecraft.client.render.*;
import com.hyrulecraft.item.OcarinaItem;
import com.hyrulecraft.net.HyruleNet;
import com.hyrulecraft.registry.ModBlocks;
import com.hyrulecraft.registry.ModEntities;
import com.hyrulecraft.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class HyruleCraftClient implements ClientModInitializer {
    public static KeyBinding Z_KEY, NEXT_KEY, DODGE_KEY, MENU_KEY;
    private static final String CAT = "category.hyrulecraft";

    private static KeyBinding key(String id, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.hyrulecraft." + id, InputUtil.Type.KEYSYM, code, CAT));
    }

    @Override
    public void onInitializeClient() {
        ClientConfig.load();
        Z_KEY = key("z_target", GLFW.GLFW_KEY_Z);
        NEXT_KEY = key("next_target", GLFW.GLFW_KEY_R);
        DODGE_KEY = key("dodge", GLFW.GLFW_KEY_V);
        MENU_KEY = key("menu", GLFW.GLFW_KEY_K);

        EntityRendererRegistry.register(ModEntities.STALFOS, StalfosRenderer::new);
        EntityRendererRegistry.register(ModEntities.REDEAD, RedeadRenderer::new);
        EntityRendererRegistry.register(ModEntities.SKULLTULA, SkulltulaRenderer::new);
        EntityRendererRegistry.register(ModEntities.GANONDORF, GanondorfRenderer::new);
        EntityRendererRegistry.register(ModEntities.PROJECTILE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BOOMERANG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BOMB, BombRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.NAVI, EndRodParticle.Factory::new);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.POT, ModBlocks.DEKU_WEB);

        OcarinaItem.OPEN_SCREEN = () -> MinecraftClient.getInstance().setScreen(new OcarinaScreen());

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            ZTargeting.tick(mc);
            DodgeAnims.clientTick(mc);
            while (MENU_KEY.wasPressed()) mc.setScreen(new SettingsScreen());
        });
        WorldRenderEvents.START.register(ctx -> ZTargeting.updateCamera());
        WorldRenderEvents.LAST.register(ZTargeting::renderMarker);
        HudRenderCallback.EVENT.register(HyruleHud::render);

        ClientPlayNetworking.registerGlobalReceiver(HyruleNet.ANIM, (client, handler, buf, sender) -> {
            int id = buf.readVarInt();
            int type = buf.readByte();
            client.execute(() -> {
                if (client.world == null) return;
                Entity e = client.world.getEntityById(id);
                if (e != null) DodgeAnims.start(e, type);
            });
        });
    }
}
