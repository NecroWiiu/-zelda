package com.hyrulecraft.client.render;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.entity.GanondorfEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GanondorfRenderer extends BipedEntityRenderer<GanondorfEntity, PlayerEntityModel<GanondorfEntity>> {
    private static final Identifier TEX = new Identifier(HyruleCraft.MOD_ID, "textures/entity/ganondorf.png");

    public GanondorfRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.7F);
    }

    @Override protected void scale(GanondorfEntity e, MatrixStack m, float tickDelta) { m.scale(1.3F, 1.3F, 1.3F); }
    @Override public Identifier getTexture(GanondorfEntity e) { return TEX; }
}
