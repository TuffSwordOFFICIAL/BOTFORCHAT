package com.tuffsword.remotecontrol.boss.mixin;

import com.tuffsword.remotecontrol.boss.KeyboardInputRelay;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButtonEvent(long window, int button, int action, int modifiers, CallbackInfo ci) {
        KeyboardInputRelay.onMouseButton(button, action, modifiers);
    }
    
    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void onMouseScrollEvent(long window, double xOffset, double yOffset, CallbackInfo ci) {
        KeyboardInputRelay.onScroll(xOffset, yOffset);
    }
}
