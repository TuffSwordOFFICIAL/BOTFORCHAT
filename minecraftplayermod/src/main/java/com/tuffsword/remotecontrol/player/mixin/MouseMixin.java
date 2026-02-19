package com.tuffsword.remotecontrol.player.mixin;

import com.tuffsword.remotecontrol.player.InputInjector;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void injectMouseButtonEvent(long window, int button, int action, int modifiers, CallbackInfo ci) {
        // Check for pending injected events
        InputInjector.MouseButtonEvent event = InputInjector.pollMouseButtonEvent();
        if (event != null) {
            // Process injected event
        }
    }
    
    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void injectMouseScrollEvent(long window, double xOffset, double yOffset, CallbackInfo ci) {
        // Check for pending injected events
        InputInjector.MouseScrollEvent event = InputInjector.pollMouseScrollEvent();
        if (event != null) {
            // Process injected event
        }
    }
}
