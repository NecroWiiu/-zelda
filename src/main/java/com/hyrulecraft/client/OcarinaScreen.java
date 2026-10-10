package com.hyrulecraft.client;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.net.HyruleNet;
import com.hyrulecraft.ocarina.OcarinaNote;
import com.hyrulecraft.ocarina.OcarinaSong;
import com.hyrulecraft.registry.ModSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/** Panel de la ocarina: Espacio = A, flechas/WASD = botones C. También se puede hacer clic. */
public class OcarinaScreen extends Screen {
    private static final Identifier[] ICONS = new Identifier[5];
    static {
        String[] n = {"a", "down", "right", "left", "up"};
        for (int i = 0; i < 5; i++) ICONS[i] = new Identifier(HyruleCraft.MOD_ID, "textures/gui/note_" + n[i] + ".png");
    }

    private final List<OcarinaNote> played = new ArrayList<>();
    private final int[] flash = new int[5];
    private Text songText;
    private int closeIn = -1;
    private boolean showSongs = true;

    public OcarinaScreen() { super(Text.translatable("screen.hyrulecraft.ocarina")); }

    @Override public boolean shouldPause() { return false; }

    private int[] pos(OcarinaNote n, int cx, int cy) {
        return switch (n) {
            case A -> new int[]{cx - 16, cy - 16};
            case UP -> new int[]{cx - 16, cy - 64};
            case DOWN -> new int[]{cx - 16, cy + 32};
            case LEFT -> new int[]{cx - 72, cy - 16};
            case RIGHT -> new int[]{cx + 40, cy - 16};
        };
    }

    private void press(OcarinaNote n) {
        if (closeIn >= 0) return;
        played.add(n);
        if (played.size() > 8) played.remove(0);
        flash[n.ordinal()] = 6;
        client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.NOTES[n.ordinal()], 1.0F, 1.0F));
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(n.ordinal());
        ClientPlayNetworking.send(HyruleNet.NOTE, buf);
        OcarinaSong s = OcarinaSong.match(played);
        if (s != null) {
            songText = Text.translatable("song.hyrulecraft." + s.id);
            PacketByteBuf sb = PacketByteBufs.create();
            sb.writeByte(s.ordinal());
            ClientPlayNetworking.send(HyruleNet.SONG, sb);
            played.clear();
            closeIn = 45;
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        switch (key) {
            case GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_ENTER -> press(OcarinaNote.A);
            case GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_W -> press(OcarinaNote.UP);
            case GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_S -> press(OcarinaNote.DOWN);
            case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_A -> press(OcarinaNote.LEFT);
            case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_D -> press(OcarinaNote.RIGHT);
            case GLFW.GLFW_KEY_TAB -> showSongs = !showSongs;
            default -> { return super.keyPressed(key, scan, mods); }
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int cx = width / 2, cy = height / 2 + 10;
        for (OcarinaNote n : OcarinaNote.values()) {
            int[] p = pos(n, cx, cy);
            if (mx >= p[0] && mx < p[0] + 32 && my >= p[1] && my < p[1] + 32) { press(n); return true; }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void tick() {
        for (int i = 0; i < 5; i++) if (flash[i] > 0) flash[i]--;
        if (closeIn >= 0 && --closeIn < 0) close();
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx);
        int cx = width / 2, cy = height / 2 + 10;
        for (OcarinaNote n : OcarinaNote.values()) {
            int[] p = pos(n, cx, cy);
            int off = flash[n.ordinal()] > 0 ? -3 : 0;
            ctx.drawTexture(ICONS[n.ordinal()], p[0] - off / 2, p[1] + off / 2, 32, 32, 0, 0, 16, 16, 16, 16);
        }
        // notas tocadas
        int sx = cx - played.size() * 9;
        for (int i = 0; i < played.size(); i++) {
            ctx.drawTexture(ICONS[played.get(i).ordinal()], sx + i * 18, cy - 92, 16, 16, 0, 0, 16, 16, 16, 16);
        }
        if (songText != null) ctx.drawCenteredTextWithShadow(textRenderer, songText, cx, cy - 110, 0xFFE066);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.hyrulecraft.ocarina.help"), cx, height - 14, 0xAAAAAA);

        if (showSongs) {
            int y = 14;
            for (OcarinaSong s : OcarinaSong.values()) {
                ctx.drawTextWithShadow(textRenderer, Text.translatable("song.hyrulecraft." + s.id), 8, y, 0xFFFFFF);
                for (int i = 0; i < s.notes.length; i++) {
                    ctx.drawTexture(ICONS[s.notes[i].ordinal()], 118 + i * 9, y - 1, 8, 8, 0, 0, 16, 16, 16, 16);
                }
                y += 12;
            }
        }
        super.render(ctx, mx, my, delta);
    }
}
