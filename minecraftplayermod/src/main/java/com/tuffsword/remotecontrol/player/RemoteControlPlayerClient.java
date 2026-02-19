package com.tuffsword.remotecontrol.player;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RemoteControlPlayerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("remotecontrol-player");
    
    private WebSocketClientHandler wsClient;
    private final ConcurrentLinkedQueue<String> commandQueue = new ConcurrentLinkedQueue<>();
    private RemoteControlConfig config;
    
    @Override
    public void onInitializeClient() {
        config = RemoteControlConfig.loadOrCreateDefault();
        
        if (!config.enabled) {
            return;
        }
        
        connectToRelay();
        
        // Process commands on client tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                String command;
                while ((command = commandQueue.poll()) != null) {
                    executeCommand(client, command);
                }
            }
        });
    }
    
    private void connectToRelay() {
        try {
            String username = MinecraftClient.getInstance().getSession().getUsername();
            URI serverUri = new URI(config.relayUrl.replace("http://", "ws://").replace("https://", "wss://"));
            
            wsClient = new WebSocketClientHandler(serverUri, username, commandQueue);
            wsClient.connect();
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void executeCommand(MinecraftClient client, String command) {
        if (client.player == null) return;
        
        // Handle raw input commands (for mirror mode)
        if (command.startsWith("/rawinput ")) {
            handleRawInput(client, command);
            return;
        }
        
        // Execute as chat command or regular input
        if (command.startsWith("/")) {
            client.player.networkHandler.sendChatCommand(command.substring(1));
        } else {
            client.player.networkHandler.sendChatMessage(command);
        }
    }
    
    private void handleRawInput(MinecraftClient client, String command) {
        String[] parts = command.split(" ");
        if (parts.length < 2) return;
        
        String eventType = parts[1];
        
        try {
            switch (eventType) {
                case "keypress":
                case "keyrelease":
                case "keyrepeat": {
                    if (parts.length < 3) return;
                    int key = Integer.parseInt(parts[2]);
                    int scancode = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
                    int modifiers = parts.length > 4 ? Integer.parseInt(parts[4]) : 0;
                    int action = eventType.equals("keypress") ? 1 : (eventType.equals("keyrelease") ? 0 : 2);
                    
                    InputInjector.injectKeyEvent(key, scancode, action, modifiers);
                    break;
                }
                case "char": {
                    if (parts.length < 3) return;
                    int chr = Integer.parseInt(parts[2]);
                    int modifiers = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
                    
                    InputInjector.injectCharEvent(chr, modifiers);
                    break;
                }
                case "mousepress":
                case "mouserelease": {
                    if (parts.length < 3) return;
                    int button = Integer.parseInt(parts[2]);
                    int modifiers = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
                    int action = eventType.equals("mousepress") ? 1 : 0;
                    
                    InputInjector.injectMouseButton(button, action, modifiers);
                    break;
                }
                case "scroll": {
                    if (parts.length < 4) return;
                    double xOffset = Double.parseDouble(parts[2]);
                    double yOffset = Double.parseDouble(parts[3]);
                    
                    InputInjector.injectMouseScroll(xOffset, yOffset);
                    break;
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}
