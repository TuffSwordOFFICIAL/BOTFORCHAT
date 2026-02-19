package com.tuffsword.remotecontrol.player.mixin;

import com.tuffsword.remotecontrol.player.InputInjector;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    
    @Inject(method = "onKey", at = @At("HEAD"))
    private void injectKeyEvent(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        // Check for pending injected events
        InputInjector.KeyEvent event = InputInjector.pollKeyEvent();
        if (event != null) {
            // Process injected event
        }
    }
    
    @Inject(method = "onChar", at = @At("HEAD"))
    private void injectCharEvent(long window, int codePoint, int modifiers, CallbackInfo ci) {
        // Check for pending injected events
        InputInjector.CharEvent event = InputInjector.pollCharEvent();
        if (event != null) {
            // Process injected event
        }
    }
}
