package com.hyrulecraft.client;

import com.hyrulecraft.item.RupeeItem;
import com.hyrulecraft.registry.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Contador de rupias y bandas de cine al fijar objetivo. */
public final class HyruleHud {
    private HyruleHud() {}

    public static void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();

        float lb = ZTargeting.letterbox();
        if (lb > 0.02F) {
            int bar = (int) (h * 0.06F * lb);
            ctx.fill(0, 0, w, bar, 0xFF000000);
            ctx.fill(0, h - bar, w, h, 0xFF000000);
        }

        int rupees = RupeeItem.countIn(mc.player);
        if (rupees > 0) {
            ctx.drawItem(new ItemStack(ModItems.RUPEE_GREEN), 4, 4);
            ctx.drawTextWithShadow(mc.textRenderer, String.valueOf(rupees), 24, 8, 0x55FF55);
        }
    }
}
