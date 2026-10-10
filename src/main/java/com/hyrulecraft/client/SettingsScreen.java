package com.hyrulecraft.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Menú de HyruleCraft (tecla K): skins de Ocarina of Time y opciones del Z-Targeting. */
public class SettingsScreen extends Screen {
    public SettingsScreen() { super(Text.translatable("screen.hyrulecraft.settings")); }

    private Text onOff(String key, boolean v) {
        return Text.translatable(key).append(": ").append(Text.translatable(v ? "options.on" : "options.off"));
    }

    @Override
    protected void init() {
        int cx = width / 2, y = height / 2 - 90;
        addDrawableChild(ButtonWidget.builder(onOff("screen.hyrulecraft.toggle_mode", ClientConfig.toggleTarget), b -> {
            ClientConfig.toggleTarget = !ClientConfig.toggleTarget;
            b.setMessage(onOff("screen.hyrulecraft.toggle_mode", ClientConfig.toggleTarget));
            ClientConfig.save();
        }).dimensions(cx - 100, y, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(onOff("screen.hyrulecraft.letterbox", ClientConfig.letterbox), b -> {
            ClientConfig.letterbox = !ClientConfig.letterbox;
            b.setMessage(onOff("screen.hyrulecraft.letterbox", ClientConfig.letterbox));
            ClientConfig.save();
        }).dimensions(cx - 100, y + 24, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("skin.hyrulecraft.none"), b -> choose("")).dimensions(cx - 100, y + 60, 200, 20).build());
        int i = 0;
        for (String s : SkinManager.SKINS) {
            int col = i % 2, row = i / 2;
            addDrawableChild(ButtonWidget.builder(Text.translatable("skin.hyrulecraft." + s), b -> choose(s))
                    .dimensions(cx - 100 + col * 102, y + 84 + row * 24, 98, 20).build());
            i++;
        }
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), b -> close()).dimensions(cx - 100, y + 164, 200, 20).build());
    }

    private void choose(String skin) {
        ClientConfig.skin = skin;
        ClientConfig.save();
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 112, 0xFFE066);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.hyrulecraft.skins"), width / 2, height / 2 - 40, 0xAAAAAA);
        super.render(ctx, mx, my, delta);
    }
}
