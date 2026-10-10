package com.hyrulecraft.mixin;

import com.hyrulecraft.client.DodgeAnims;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Voltereta / giro de espada: rota el modelo del jugador. */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "setupTransforms", at = @At("TAIL"))
    private void hyrulecraft$anim(LivingEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
        DodgeAnims.apply(entity, matrices, tickDelta);
    }
}
