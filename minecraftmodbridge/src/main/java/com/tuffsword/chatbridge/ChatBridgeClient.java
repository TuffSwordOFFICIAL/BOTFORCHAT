package com.tuffsword.chatbridge;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ChatBridgeClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("chatbridge");

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(200))
        .build();

    private volatile String lastAppliedInput = "";
    private volatile long lastAppliedCommandId = -1;

    @Override
    public void onInitializeClient() {
        ChatBridgeConfig config = ChatBridgeConfig.loadOrCreateDefault();
        if (!config.enabled) {
            LOGGER.info("ChatBridge is disabled by config.");
            return;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "chatbridge-poller");
            thread.setDaemon(true);
            return thread;
        });

        scheduler.scheduleWithFixedDelay(
            () -> pollApiAndDispatch(config.apiUrl),
            1000,
            config.pollIntervalMs,
            TimeUnit.MILLISECONDS
        );

        LOGGER.info("ChatBridge running. Polling {} every {}ms", config.apiUrl, config.pollIntervalMs);
    }

    private void pollApiAndDispatch(String apiUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofMillis(200))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return;
            }

            String input = response.body() == null ? "" : response.body().trim();
            if (input.isEmpty()) {
                return;
            }

            long commandId = parseCommandId(response.headers().firstValue("X-Command-Id").orElse(null));
            if (commandId >= 0) {
                if (commandId == lastAppliedCommandId) {
                    return;
                }
                lastAppliedCommandId = commandId;
            } else {
                if (input.equals(lastAppliedInput)) {
                    return;
                }
                lastAppliedInput = input;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() -> applyInput(client, input));
        } catch (IOException | InterruptedException ignored) {
        } catch (Exception ignored) {
        }
    }

    private long parseCommandId(String rawHeader) {
        if (rawHeader == null || rawHeader.isBlank()) {
            return -1;
        }

        try {
            return Long.parseLong(rawHeader.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void applyInput(MinecraftClient client, String input) {
        if (client.player == null || client.player.networkHandler == null || client.options == null) {
            return;
        }

        String normalized = input.trim().toLowerCase(Locale.ROOT);
        String movementToken = normalizeMovementToken(normalized.replaceAll("\\\\+$", ""));
        if (isMovementOnlyToken(movementToken)) {
            applyMovementToken(client, movementToken);
            return;
        }

        if (input.startsWith("/") && input.length() > 1) {
            client.player.networkHandler.sendChatCommand(input.substring(1));
        } else {
            client.player.networkHandler.sendChatMessage(input);
        }
    }

    private boolean isMovementOnlyToken(String normalized) {
        return normalized.equals("w")
            || normalized.equals("a")
            || normalized.equals("s")
            || normalized.equals("d")
            || normalized.equals("jump")
            || normalized.equals("sneak")
            || normalized.equals("sprint")
            || normalized.equals("stop");
    }

    private String normalizeMovementToken(String token) {
        return switch (token) {
            case "forward" -> "w";
            case "back", "backward" -> "s";
            case "left" -> "a";
            case "right" -> "d";
            case "space" -> "jump";
            case "shift" -> "sneak";
            case "run" -> "sprint";
            default -> token;
        };
    }

    private void applyMovementToken(MinecraftClient client, String normalized) {
        if (normalized.equals("stop")) {
            setMovementState(client, false, false, false, false, false, false, false);
            return;
        }

        boolean forward = normalized.equals("w");
        boolean left = normalized.equals("a");
        boolean back = normalized.equals("s");
        boolean right = normalized.equals("d");
        boolean jump = normalized.equals("jump");
        boolean sneak = normalized.equals("sneak");
        boolean sprint = normalized.equals("sprint");

        setMovementState(client, forward, left, back, right, jump, sneak, sprint);
    }

    private void setMovementState(
        MinecraftClient client,
        boolean forward,
        boolean left,
        boolean back,
        boolean right,
        boolean jump,
        boolean sneak,
        boolean sprint
    ) {
        client.options.forwardKey.setPressed(forward);
        client.options.leftKey.setPressed(left);
        client.options.backKey.setPressed(back);
        client.options.rightKey.setPressed(right);
        client.options.jumpKey.setPressed(jump);
        client.options.sneakKey.setPressed(sneak);
        client.options.sprintKey.setPressed(sprint);
    }
}
