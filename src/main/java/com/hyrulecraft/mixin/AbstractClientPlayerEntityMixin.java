package com.hyrulecraft.mixin;

import com.hyrulecraft.client.SkinManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Skins de Ocarina of Time (solo en tu cliente). */
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
    private void hyrulecraft$skin(CallbackInfoReturnable<Identifier> cir) {
        Identifier id = SkinManager.overrideFor((AbstractClientPlayerEntity) (Object) this);
        if (id != null) cir.setReturnValue(id);
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void hyrulecraft$model(CallbackInfoReturnable<String> cir) {
        if (SkinManager.overrideFor((AbstractClientPlayerEntity) (Object) this) != null) cir.setReturnValue("default");
    }
}
