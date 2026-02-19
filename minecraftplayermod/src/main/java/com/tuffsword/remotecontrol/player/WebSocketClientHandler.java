package com.tuffsword.remotecontrol.player;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketClientHandler extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger("remotecontrol-player-ws");
    private final String username;
    private final ConcurrentLinkedQueue<String> commandQueue;
    private final Gson gson = new Gson();
    
    public WebSocketClientHandler(URI serverUri, String username, ConcurrentLinkedQueue<String> commandQueue) {
        super(serverUri);
        this.username = username;
        this.commandQueue = commandQueue;
    }
    
    @Override
    public void onOpen(ServerHandshake handshake) {
        // Register with server
        JsonObject register = new JsonObject();
        register.addProperty("type", "register");
        register.addProperty("username", username);
        send(register.toString());
    }
    
    @Override
    public void onMessage(String message) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();
            
            if ("command".equals(type)) {
                String command = json.get("command").getAsString();
                commandQueue.offer(command);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Try to reconnect after 5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                if (!isOpen()) {
                    reconnect();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    @Override
    public void onError(Exception ex) {
        // Silent fail
    }
}
