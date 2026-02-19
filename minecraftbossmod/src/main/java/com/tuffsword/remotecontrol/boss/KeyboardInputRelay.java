package com.tuffsword.remotecontrol.boss;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyboardInputRelay {
    private static boolean isRelayingInput = false;
    private static String targetPlayer = null;
    private static long lastInputTime = 0;
    
    public static void initialize() {
        // This will be called from key/mouse callbacks
    }
    
    public static void onKeyPress(int key, int scancode, int action, int modifiers) {
        if (!isRelayingInput || targetPlayer == null) return;
        
        // Send raw key event
        String eventType;
        if (action == GLFW.GLFW_PRESS) {
            eventType = "keypress";
        } else if (action == GLFW.GLFW_RELEASE) {
            eventType = "keyrelease";
        } else if (action == GLFW.GLFW_REPEAT) {
            eventType = "keyrepeat";
        } else {
            return;
        }
        
        String command = String.format("/rawinput %s %d %d %d", eventType, key, scancode, modifiers);
        RemoteControlBossClient.getApiClient().sendCommand(targetPlayer, command);
    }
    
    public static void onCharTyped(char chr, int modifiers) {
        if (!isRelayingInput || targetPlayer == null) return;
        
        // Send character for text input
        String command = String.format("/rawinput char %d %d", (int)chr, modifiers);
        RemoteControlBossClient.getApiClient().sendCommand(targetPlayer, command);
    }
    
    public static void onMouseButton(int button, int action, int modifiers) {
        if (!isRelayingInput || targetPlayer == null) return;
        
        String eventType = action == GLFW.GLFW_PRESS ? "mousepress" : "mouserelease";
        String command = String.format("/rawinput %s %d %d", eventType, button, modifiers);
        RemoteControlBossClient.getApiClient().sendCommand(targetPlayer, command);
    }
    
    public static void onMouseMove(double xpos, double ypos) {
        if (!isRelayingInput || targetPlayer == null) return;
        
        // Throttle mouse movement updates
        long now = System.currentTimeMillis();
        if (now - lastInputTime < 50) return; // Max 20 updates per second
        lastInputTime = now;
        
        String command = String.format("/rawinput mousemove %.2f %.2f", xpos, ypos);
        RemoteControlBossClient.getApiClient().sendCommand(targetPlayer, command);
    }
    
    public static void onScroll(double xOffset, double yOffset) {
        if (!isRelayingInput || targetPlayer == null) return;
        
        String command = String.format("/rawinput scroll %.2f %.2f", xOffset, yOffset);
        RemoteControlBossClient.getApiClient().sendCommand(targetPlayer, command);
    }
    
    public static void startRelaying(String player) {
        targetPlayer = player;
        isRelayingInput = true;
        lastInputTime = 0;
        RemoteControlBossClient.LOGGER.info("Started relaying raw input to: {}", player);
    }
    
    public static void stopRelaying() {
        isRelayingInput = false;
        targetPlayer = null;
        RemoteControlBossClient.LOGGER.info("Stopped relaying input");
    }
    
    public static boolean isRelaying() {
        return isRelayingInput;
    }
    
    public static String getTargetPlayer() {
        return targetPlayer;
    }
}
