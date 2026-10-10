package com.hyrulecraft.client.render;

import com.hyrulecraft.entity.BombEntity;
import com.hyrulecraft.registry.ModItems;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class BombRenderer extends EntityRenderer<BombEntity> {
    private final ItemRenderer items;

    public BombRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.items = ctx.getItemRenderer();
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(BombEntity e, float yaw, float tickDelta, MatrixStack m, VertexConsumerProvider vc, int light) {
        m.push();
        m.translate(0, 0.3, 0);
        float s = 1.4F;
        if (e.getFuse() < 20 && (e.getFuse() / 3) % 2 == 0) s = 1.7F;
        m.scale(s, s, s);
        m.multiply(this.dispatcher.getRotation());
        items.renderItem(new ItemStack(ModItems.BOMB), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, m, vc, e.getWorld(), e.getId());
        m.pop();
        super.render(e, yaw, tickDelta, m, vc, light);
    }

    @Override public Identifier getTexture(BombEntity e) { return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE; }
}
