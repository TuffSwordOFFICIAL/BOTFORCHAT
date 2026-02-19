package com.tuffsword.remotecontrol.boss.mixin;

import com.tuffsword.remotecontrol.boss.KeyboardInputRelay;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKeyEvent(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        KeyboardInputRelay.onKeyPress(key, scancode, action, modifiers);
    }
    
    @Inject(method = "onChar", at = @At("HEAD"))
    private void onCharEvent(long window, int codePoint, int modifiers, CallbackInfo ci) {
        KeyboardInputRelay.onCharTyped((char)codePoint, modifiers);
    }
}
