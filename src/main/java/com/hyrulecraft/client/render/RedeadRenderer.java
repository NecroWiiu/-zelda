package com.hyrulecraft.client.render;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.entity.RedeadEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;

public class RedeadRenderer extends BipedEntityRenderer<RedeadEntity, ZombieEntityModel<RedeadEntity>> {
    private static final Identifier TEX = new Identifier(HyruleCraft.MOD_ID, "textures/entity/redead.png");

    public RedeadRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ZombieEntityModel<>(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5F);
    }

    @Override public Identifier getTexture(RedeadEntity e) { return TEX; }
}
