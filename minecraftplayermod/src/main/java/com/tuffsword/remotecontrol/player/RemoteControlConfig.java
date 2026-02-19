package com.tuffsword.remotecontrol.player;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class RemoteControlConfig {
    private static final String FILE_NAME = "remotecontrol-player.properties";

    public final boolean enabled;
    public final String relayUrl;

    private RemoteControlConfig(boolean enabled, String relayUrl) {
        this.enabled = enabled;
        this.relayUrl = relayUrl;
    }

    public static RemoteControlConfig loadOrCreateDefault() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        Properties properties = new Properties();

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            } catch (IOException ignored) {
            }
        } else {
            properties.setProperty("enabled", "true");
            properties.setProperty("relayUrl", "ws://localhost:3000");
            try {
                Files.createDirectories(configPath.getParent());
                try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                    properties.store(outputStream, "RemoteControl Player Config - Set relayUrl to your relay server WebSocket URL");
                }
            } catch (IOException ignored) {
            }
        }

        boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));
        String relayUrl = properties.getProperty("relayUrl", "ws://localhost:3000").trim();

        return new RemoteControlConfig(enabled, relayUrl);
    }
}
