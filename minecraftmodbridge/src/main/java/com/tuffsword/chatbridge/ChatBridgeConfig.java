package com.tuffsword.chatbridge;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ChatBridgeConfig {
    private static final String FILE_NAME = "chatbridge.properties";

    public final boolean enabled;
    public final String apiUrl;
    public final int pollIntervalMs;

    private ChatBridgeConfig(boolean enabled, String apiUrl, int pollIntervalMs) {
        this.enabled = enabled;
        this.apiUrl = apiUrl;
        this.pollIntervalMs = pollIntervalMs;
    }

    public static ChatBridgeConfig loadOrCreateDefault() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        Properties properties = new Properties();

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            } catch (IOException ignored) {
            }
        } else {
            properties.setProperty("enabled", "true");
            properties.setProperty("apiUrl", "http://localhost:3000/command");
            properties.setProperty("pollIntervalMs", "16");
            try {
                Files.createDirectories(configPath.getParent());
                try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                    properties.store(outputStream, "Chat Bridge config");
                }
            } catch (IOException ignored) {
            }
        }

        boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));
        String apiUrl = properties.getProperty("apiUrl", "http://localhost:3000/command").trim();
        int pollIntervalMs = parsePollInterval(properties.getProperty("pollIntervalMs", "16"));

        return new ChatBridgeConfig(enabled, apiUrl, pollIntervalMs);
    }

    private static int parsePollInterval(String rawValue) {
        try {
            int parsed = Integer.parseInt(rawValue);
            return Math.max(parsed, 16);
        } catch (NumberFormatException ignored) {
            return 16;
        }
    }
}
