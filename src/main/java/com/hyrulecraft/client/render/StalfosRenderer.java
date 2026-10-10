package com.hyrulecraft.client.render;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.entity.StalfosEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class StalfosRenderer extends BipedEntityRenderer<StalfosEntity, BipedEntityModel<StalfosEntity>> {
    private static final Identifier TEX = new Identifier(HyruleCraft.MOD_ID, "textures/entity/stalfos.png");

    public StalfosRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.SKELETON)), 0.5F);
    }

    @Override public Identifier getTexture(StalfosEntity e) { return TEX; }
}
