package com.tuffsword.remotecontrol.boss;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class RemoteControlBossConfig {
    private static final String FILE_NAME = "remotecontrol-boss.properties";

    public final boolean enabled;
    public final String relayUrl;

    private RemoteControlBossConfig(boolean enabled, String relayUrl) {
        this.enabled = enabled;
        this.relayUrl = relayUrl;
    }

    public static RemoteControlBossConfig loadOrCreateDefault() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        Properties properties = new Properties();

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                properties.load(inputStream);
            } catch (IOException ignored) {
            }
        } else {
            properties.setProperty("enabled", "true");
            properties.setProperty("relayUrl", "https://minecraft-relay.onrender.com");
            try {
                Files.createDirectories(configPath.getParent());
                try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                    properties.store(outputStream, "RemoteControl Boss Config - Set relayUrl to your relay server HTTP URL");
                }
            } catch (IOException ignored) {
            }
        }

        boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));
        String relayUrl = properties.getProperty("relayUrl", "https://minecraft-relay.onrender.com").trim();

        return new RemoteControlBossConfig(enabled, relayUrl);
    }
}
