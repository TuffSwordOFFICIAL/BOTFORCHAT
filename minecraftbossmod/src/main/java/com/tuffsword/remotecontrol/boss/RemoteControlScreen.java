package com.tuffsword.remotecontrol.boss;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class RemoteControlScreen extends Screen {
    private List<String> players = new ArrayList<>();
    private String selectedPlayer = null;
    private TextFieldWidget commandInput;
    private ButtonWidget refreshButton;
    private ButtonWidget sendButton;
    private ButtonWidget startRelayButton;
    private ButtonWidget stopRelayButton;
    private List<ButtonWidget> playerButtons = new ArrayList<>();
    private int lastRefreshTick = 0;
    
    public RemoteControlScreen() {
        super(Text.literal("Remote Control Panel"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Command input field
        commandInput = new TextFieldWidget(
            this.textRenderer,
            this.width / 2 - 150,
            this.height - 50,
            200,
            20,
            Text.literal("Command")
        );
        commandInput.setMaxLength(256);
        commandInput.setPlaceholder(Text.literal("Enter command..."));
        addSelectableChild(commandInput);
        
        // Send button
        sendButton = ButtonWidget.builder(Text.literal("Send"), button -> {
            if (selectedPlayer != null && !commandInput.getText().isEmpty()) {
                sendCommand(selectedPlayer, commandInput.getText());
                commandInput.setText("");
            }
        })
        .dimensions(this.width / 2 + 60, this.height - 50, 60, 20)
        .build();
        addDrawableChild(sendButton);
        
        // Start Relay button
        startRelayButton = ButtonWidget.builder(Text.literal("Start Input Relay"), button -> {
            if (selectedPlayer != null) {
                KeyboardInputRelay.startRelaying(selectedPlayer);
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(
                        Text.literal("§aStarted relaying input to " + selectedPlayer + " - Press K to stop"),
                        false
                    );
                }
            }
        })
        .dimensions(this.width / 2 - 150, this.height - 80, 140, 20)
        .build();
        addDrawableChild(startRelayButton);
        
        // Stop Relay button
        stopRelayButton = ButtonWidget.builder(Text.literal("Stop Input Relay"), button -> {
            KeyboardInputRelay.stopRelaying();
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage(
                    Text.literal("§cStopped input relay"),
                    false
                );
            }
        })
        .dimensions(this.width / 2 + 10, this.height - 80, 140, 20)
        .build();
        addDrawableChild(stopRelayButton);
        
        // Refresh button
        refreshButton = ButtonWidget.builder(Text.literal("Refresh Players"), button -> {
            refreshPlayers();
        })
        .dimensions(this.width / 2 - 80, 40, 160, 20)
        .build();
        addDrawableChild(refreshButton);
        
        // Close button
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            if (this.client != null) {
                this.client.setScreen(null);
            }
        })
        .dimensions(this.width / 2 - 50, this.height - 30, 100, 20)
        .build());
        
        // Initial refresh
        refreshPlayers();
    }
    
    private void refreshPlayers() {
        RemoteControlBossClient.getApiClient().getPlayers().thenAccept(playerList -> {
            if (this.client != null) {
                this.client.execute(() -> {
                    this.players = playerList;
                    updatePlayerButtons();
                    RemoteControlBossClient.LOGGER.info("Found {} players", playerList.size());
                });
            }
        });
    }
    
    private void updatePlayerButtons() {
        // Remove old player buttons
        playerButtons.forEach(this::remove);
        playerButtons.clear();
        
        // Add new player buttons
        int y = 70;
        for (String player : players) {
            boolean isSelected = player.equals(selectedPlayer);
            ButtonWidget button = ButtonWidget.builder(
                Text.literal((isSelected ? "> " : "") + player),
                btn -> selectPlayer(player)
            )
            .dimensions(this.width / 2 - 100, y, 200, 20)
            .build();
            
            playerButtons.add(button);
            addDrawableChild(button);
            y += 25;
        }
    }
    
    private void selectPlayer(String player) {
        selectedPlayer = player;
        updatePlayerButtons();
        RemoteControlBossClient.LOGGER.info("Selected player: {}", player);
    }
    
    private void sendCommand(String target, String command) {
        RemoteControlBossClient.getApiClient().sendCommand(target, command).thenAccept(success -> {
            if (this.client != null) {
                this.client.execute(() -> {
                    if (success && this.client.player != null) {
                        this.client.player.sendMessage(
                            Text.literal("§aCommand sent to " + target + ": " + command),
                            false
                        );
                    } else if (this.client.player != null) {
                        this.client.player.sendMessage(
                            Text.literal("§cFailed to send command to " + target),
                            false
                        );
                    }
                });
            }
        });
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            20,
            0xFFFFFF
        );
        
        // Selected player indicator
        if (selectedPlayer != null) {
            context.drawTextWithShadow(
                this.textRenderer,
                "Selected: §a" + selectedPlayer,
                this.width / 2 - 150,
                this.height - 70,
                0xFFFFFF
            );
        } else {
            context.drawTextWithShadow(
                this.textRenderer,
                "§7No player selected",
                this.width / 2 - 150,
                this.height - 70,
                0xFFFFFF
            );
        }
        
        // Player count
        context.drawTextWithShadow(
            this.textRenderer,
            "§7Players online: " + players.size(),
            10,
            10,
            0xFFFFFF
        );
        
        // Input relay status
        if (KeyboardInputRelay.isRelaying()) {
            context.drawTextWithShadow(
                this.textRenderer,
                "§a⚡ Relaying to: " + KeyboardInputRelay.getTargetPlayer(),
                10,
                25,
                0xFFFFFF
            );
        }
        
        // Command input
        commandInput.render(context, mouseX, mouseY, delta);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Auto-refresh every 5 seconds (100 ticks)
        lastRefreshTick++;
        if (lastRefreshTick >= 100) {
            lastRefreshTick = 0;
            refreshPlayers();
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause game
    }
}
