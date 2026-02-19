package com.tuffsword.remotecontrol.player;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.ConcurrentHashMap;

public class InputInjector {
    private static final ConcurrentHashMap<String, Object> pendingInputs = new ConcurrentHashMap<>();
    
    public static void injectKeyEvent(int key, int scancode, int action, int modifiers) {
        pendingInputs.put("key", new KeyEvent(key, scancode, action, modifiers));
    }
    
    public static void injectCharEvent(int chr, int modifiers) {
        pendingInputs.put("char", new CharEvent(chr, modifiers));
    }
    
    public static void injectMouseButton(int button, int action, int modifiers) {
        pendingInputs.put("mouse", new MouseButtonEvent(button, action, modifiers));
    }
    
    public static void injectMouseScroll(double xOffset, double yOffset) {
        pendingInputs.put("scroll", new MouseScrollEvent(xOffset, yOffset));
    }
    
    public static KeyEvent pollKeyEvent() {
        return (KeyEvent) pendingInputs.remove("key");
    }
    
    public static CharEvent pollCharEvent() {
        return (CharEvent) pendingInputs.remove("char");
    }
    
    public static MouseButtonEvent pollMouseButtonEvent() {
        return (MouseButtonEvent) pendingInputs.remove("mouse");
    }
    
    public static MouseScrollEvent pollMouseScrollEvent() {
        return (MouseScrollEvent) pendingInputs.remove("scroll");
    }
    
    public static class KeyEvent {
        public final int key, scancode, action, modifiers;
        public KeyEvent(int key, int scancode, int action, int modifiers) {
            this.key = key;
            this.scancode = scancode;
            this.action = action;
            this.modifiers = modifiers;
        }
    }
    
    public static class CharEvent {
        public final int chr, modifiers;
        public CharEvent(int chr, int modifiers) {
            this.chr = chr;
            this.modifiers = modifiers;
        }
    }
    
    public static class MouseButtonEvent {
        public final int button, action, modifiers;
        public MouseButtonEvent(int button, int action, int modifiers) {
            this.button = button;
            this.action = action;
            this.modifiers = modifiers;
        }
    }
    
    public static class MouseScrollEvent {
        public final double xOffset, yOffset;
        public MouseScrollEvent(double xOffset, double yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
