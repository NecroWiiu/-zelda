package com.hyrulecraft.client;

import com.hyrulecraft.HyruleCraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

/** Cambia tu skin (solo en tu cliente) por una de Ocarina of Time. */
public final class SkinManager {
    private SkinManager() {}
    public static final String[] SKINS = {"link", "adult_link", "zelda", "sheik", "ganondorf", "dark_link"};

    public static Identifier overrideFor(AbstractClientPlayerEntity player) {
        if (ClientConfig.skin.isEmpty() || player != MinecraftClient.getInstance().player) return null;
        for (String s : SKINS) {
            if (s.equals(ClientConfig.skin)) return new Identifier(HyruleCraft.MOD_ID, "textures/skins/" + s + ".png");
        }
        return null;
    }
}
