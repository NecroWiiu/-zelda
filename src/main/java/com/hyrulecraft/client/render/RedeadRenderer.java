
package com.hyrulecraft.client.render;

import com.hyrulecraft.HyruleCraft;
import com.hyrulecraft.entity.RedeadEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class RedeadRenderer extends BipedEntityRenderer<RedeadEntity, BipedEntityModel<RedeadEntity>> {
    private static final Identifier TEX =
            new Identifier(HyruleCraft.MOD_ID, "textures/entity/redead.png");

    public RedeadRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public Identifier getTexture(RedeadEntity entity) {
        return TEX;
    }
}
