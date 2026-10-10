package com.hyrulecraft.mixin;

import com.hyrulecraft.client.ZTargeting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Con el objetivo fijado el ratón no mueve la cámara. */
@Mixin(Entity.class)
public abstract class EntityLookMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void hyrulecraft$lock(double dx, double dy, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player && ZTargeting.isLocked()) ci.cancel();
    }
}
