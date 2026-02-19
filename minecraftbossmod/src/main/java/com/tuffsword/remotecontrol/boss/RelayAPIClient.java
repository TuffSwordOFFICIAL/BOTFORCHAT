package com.tuffsword.remotecontrol.boss;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RelayAPIClient {
    private static final Logger LOGGER = LoggerFactory.getLogger("remotecontrol-boss-api");
    private final HttpClient httpClient;
    private final String relayUrl;
    private final Gson gson = new Gson();
    
    public RelayAPIClient(String relayUrl) {
        this.relayUrl = relayUrl.endsWith("/") ? relayUrl.substring(0, relayUrl.length() - 1) : relayUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }
    
    public CompletableFuture<List<String>> getPlayers() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(relayUrl + "/players"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                    JsonArray playersArray = json.getAsJsonArray("players");
                    List<String> players = new ArrayList<>();
                    playersArray.forEach(element -> players.add(element.getAsString()));
                    return players;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to get players", e);
            }
            return new ArrayList<>();
        });
    }
    
    public CompletableFuture<Boolean> sendCommand(String target, String command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("target", target);
                payload.addProperty("command", command);
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(relayUrl + "/command"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    LOGGER.info("Command sent to {}: {}", target, command);
                    return true;
                } else {
                    LOGGER.warn("Failed to send command: HTTP {}", response.statusCode());
                    return false;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to send command", e);
                return false;
            }
        });
    }
}
