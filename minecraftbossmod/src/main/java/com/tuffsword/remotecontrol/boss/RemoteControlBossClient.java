package com.tuffsword.remotecontrol.boss;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteControlBossClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("remotecontrol-boss");
    
    private static RemoteControlBossConfig config;
    private static RelayAPIClient apiClient;
    private static KeyBinding openGuiKey;
    private static KeyBinding toggleRelayKey;
    
    @Override
    public void onInitializeClient() {
        config = RemoteControlBossConfig.loadOrCreateDefault();
        
        if (!config.enabled) {
            LOGGER.info("RemoteControl Boss is disabled by config.");
            return;
        }
        
        apiClient = new RelayAPIClient(config.relayUrl);
        
        // Initialize keyboard input relay
        KeyboardInputRelay.initialize();
        
        // Register keybinding to open GUI (Default: R key)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.remotecontrol.opengui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.remotecontrol.boss"
        ));
        
        // Register keybinding to toggle input relay (Default: K key)
        toggleRelayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.remotecontrol.togglerelay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.remotecontrol.boss"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new RemoteControlScreen());
                }
            }
            
            while (toggleRelayKey.wasPressed()) {
                if (KeyboardInputRelay.isRelaying()) {
                    KeyboardInputRelay.stopRelaying();
                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("§cInput relay stopped"), false);
                    }
                } else {
                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("§eOpen GUI and select a player first!"), false);
                    }
                }
            }
        });
        
        LOGGER.info("RemoteControl Boss initialized - Press R to open control panel, K to stop input relay");
    }
    
    public static RelayAPIClient getApiClient() {
        return apiClient;
    }
    
    public static RemoteControlBossConfig getConfig() {
        return config;
    }
}
