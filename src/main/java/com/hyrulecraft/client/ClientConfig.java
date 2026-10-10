package com.hyrulecraft.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Opciones del cliente guardadas en config/hyrulecraft.properties. */
public final class ClientConfig {
    private ClientConfig() {}
    public static boolean toggleTarget = false;
    public static boolean letterbox = true;
    public static String skin = "";

    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("hyrulecraft.properties"); }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (InputStream in = Files.newInputStream(f)) {
            Properties p = new Properties();
            p.load(in);
            toggleTarget = Boolean.parseBoolean(p.getProperty("toggleTarget", "false"));
            letterbox = Boolean.parseBoolean(p.getProperty("letterbox", "true"));
            skin = p.getProperty("skin", "");
        } catch (IOException ignored) {}
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("toggleTarget", String.valueOf(toggleTarget));
        p.setProperty("letterbox", String.valueOf(letterbox));
        p.setProperty("skin", skin);
        try (OutputStream out = Files.newOutputStream(file())) {
            p.store(out, "HyruleCraft");
        } catch (IOException ignored) {}
    }
}
