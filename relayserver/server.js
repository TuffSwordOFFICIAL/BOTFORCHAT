const express = require('express');
const WebSocket = require('ws');
const app = express();

app.use(express.json());

// Store connected players: { username: websocket }
const connectedPlayers = new Map();

// HTTP endpoint for boss mod to get player list
app.get('/players', (req, res) => {
    const players = Array.from(connectedPlayers.keys());
    res.json({ players });
});

// HTTP endpoint for boss mod to send command
app.post('/command', (req, res) => {
    const { target, command } = req.body;
    
    if (!target || !command) {
        return res.status(400).json({ error: 'Missing target or command' });
    }
    
    const playerWs = connectedPlayers.get(target);
    if (!playerWs) {
        return res.status(404).json({ error: `Player ${target} not found` });
    }
    
    if (playerWs.readyState === WebSocket.OPEN) {
        playerWs.send(JSON.stringify({ type: 'command', command }));
        res.json({ success: true, message: `Command sent to ${target}` });
    } else {
        connectedPlayers.delete(target);
        res.status(410).json({ error: `Player ${target} disconnected` });
    }
});

// Health check
app.get('/health', (req, res) => {
    res.send('OK');
});

const PORT = process.env.PORT || 3000;
const server = app.listen(PORT, '0.0.0.0', () => {
    console.log(`Relay server running on port ${PORT}`);
});

// WebSocket server for player mods
const wss = new WebSocket.Server({ server });

wss.on('connection', (ws) => {
    let username = null;
    
    console.log('New connection');
    
    ws.on('message', (data) => {
        try {
            const message = JSON.parse(data);
            
            if (message.type === 'register') {
                username = message.username;
                
                // Remove old connection if exists
                if (connectedPlayers.has(username)) {
                    const oldWs = connectedPlayers.get(username);
                    if (oldWs !== ws) {
                        oldWs.close();
                    }
                }
                
                connectedPlayers.set(username, ws);
                console.log(`Player registered: ${username} (Total: ${connectedPlayers.size})`);
                
                ws.send(JSON.stringify({ type: 'registered', success: true }));
            } else if (message.type === 'ping') {
                ws.send(JSON.stringify({ type: 'pong' }));
            }
        } catch (err) {
            console.error('Error parsing message:', err);
        }
    });
    
    ws.on('close', () => {
        if (username) {
            connectedPlayers.delete(username);
            console.log(`Player disconnected: ${username} (Total: ${connectedPlayers.size})`);
        }
    });
    
    ws.on('error', (err) => {
        console.error('WebSocket error:', err);
    });
});

console.log('WebSocket server ready for player connections');
