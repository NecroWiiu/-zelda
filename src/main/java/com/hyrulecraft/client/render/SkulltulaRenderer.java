package com.hyrulecraft.client.render;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.entity.SkulltulaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SkulltulaRenderer extends MobEntityRenderer<SkulltulaEntity, SpiderEntityModel<SkulltulaEntity>> {
    private static final Identifier TEX = new Identifier(HyruleCraft.MOD_ID, "textures/entity/skulltula.png");
    private static final Identifier GOLD = new Identifier(HyruleCraft.MOD_ID, "textures/entity/skulltula_gold.png");

    public SkulltulaRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SpiderEntityModel<>(ctx.getPart(EntityModelLayers.SPIDER)), 0.8F);
    }

    @Override protected float getLyingAngle(SkulltulaEntity e) { return 180.0F; }
    @Override protected void scale(SkulltulaEntity e, MatrixStack m, float tickDelta) { m.scale(0.9F, 0.9F, 0.9F); }
    @Override public Identifier getTexture(SkulltulaEntity e) { return e.isGold() ? GOLD : TEX; }
}
