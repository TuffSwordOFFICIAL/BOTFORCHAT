# Remote Control System Setup Guide

Control other players' Minecraft clients remotely using Boss and Player mods with a relay server.

## 📋 Overview

**System Components:**
1. **Relay Server** (Node.js) - Central hub that connects all mods
2. **Boss Mod** - For you, has GUI to control players  
3. **Player Mod** - For others, receives and executes commands

## 🚀 Quick Setup

### Step 1: Start the Relay Server

```bash
# Install dependencies
cd relayserver
npm install

# Start server
npm start
```

Server runs on `http://localhost:3000` by default.

**For remote access (Cloudflare Tunnel):**
```bash
# See hosting section below
cloudflared tunnel --url http://localhost:3000
```

---

### Step 2: Build the Mods

**Boss Mod (for you):**
```bash
cd minecraftbossmod
./gradlew build
```
Output: `minecraftbossmod/build/libs/remotecontrol-boss-1.0.0-boss.jar`

**Player Mod (for others):**
```bash
cd minecraftplayermod
./gradlew build
```
Output: `minecraftplayermod/build/libs/remotecontrol-player-1.0.0-player.jar`

---

### Step 3: Install Mods

**Your PC (Boss):**
1. Copy `remotecontrol-boss-*.jar` to `.minecraft/mods/`
2. Launch Minecraft
3. Edit `.minecraft/config/remotecontrol-boss.properties`:
   ```properties
   enabled=true
   relayUrl=http://localhost:3000
   ```

**Other Players (Player Mod):**
1. Copy `remotecontrol-player-*.jar` to `.minecraft/mods/`
2. Launch Minecraft
3. Edit `.minecraft/config/remotecontrol-player.properties`:
   ```properties
   enabled=true
   relayUrl=ws://YOUR_RELAY_URL:3000
   ```
   Replace `YOUR_RELAY_URL` with your Cloudflare tunnel URL or server IP.

---

### Step 4: Use It!

**Boss (You):**
1. Launch Minecraft
2. Press **R** key to open Remote Control Panel
3. Click "Refresh Players" to see connected players
4. Click a player name to select them
5. Type command and click "Send"

**Player (Others):**
- Just play Minecraft normally
- Commands from boss will execute automatically

---

## 🌐 Hosting Relay Server (Remote Access)

### Option 1: Cloudflare Tunnel (Easiest, Free, Hides IP)

```bash
# Install cloudflared
# Visit: https://developers.cloudflare.com/cloudflare-one/connections/connect-apps/install-and-setup/installation/

# Start relay server
cd relayserver
npm start

# In another terminal, start tunnel
cloudflared tunnel --url http://localhost:3000
```

You'll get a URL like: `https://random-words.trycloudflare.com`

**Configure mods:**
- Boss mod: `relayUrl=https://random-words.trycloudflare.com`
- Player mod: `relayUrl=wss://random-words.trycloudflare.com`

### Option 2: Fly.io / Railway (Permanent URL)

Deploy `relayserver/` folder to Fly.io or Railway:

```bash
cd relayserver
fly launch
fly deploy
```

You'll get permanent URL like: `https://your-app.fly.dev`

### Option 3: VPS with PM2

```bash
# On your VPS
cd relayserver
npm install
pm2 start server.js --name relay-server
pm2 save
```

Then use Cloudflare Tunnel or nginx to expose it.

---

## 🎮 How to Use

### Boss Commands:

Open GUI with **R** key (rebindable in Controls settings).

**GUI Features:**
- **Player List**: Shows all connected players
- **Auto-refresh**: Updates every 5 seconds
- **Select Player**: Click to target
- **Command Input**: Type any command/chat
- **Send Button**: Execute command on target

**Example Commands:**
- `/tp @s 100 64 100` - Teleport player
- `/give @s diamond 64` - Give items
- `Hello!` - Send chat message
- `/gamemode creative` - Change gamemode

### Player Mod:

No interface needed - just plays normally. Commands from boss execute automatically.

---

## 🔧 Configuration

### Relay Server

Edit `relayserver/server.js` to change port:
```javascript
const PORT = process.env.PORT || 3000;
```

### Boss Mod Config

`.minecraft/config/remotecontrol-boss.properties`:
```properties
enabled=true
relayUrl=http://localhost:3000
```

### Player Mod Config

`.minecraft/config/remotecontrol-player.properties`:
```properties
enabled=true
relayUrl=ws://localhost:3000
```

**Important:** 
- Boss uses `http://` or `https://`
- Player uses `ws://` or `wss://`

---

## 🛡️ Security

**Important Notes:**
- Only trusted players should have boss mod
- Players can disable by setting `enabled=false` in config
- Relay server doesn't authenticate - add auth if public
- Commands are executed as the player (no special permissions)

**Adding Authentication (Optional):**
Edit `relayserver/server.js` to add API key checks.

---

## 🐛 Troubleshooting

### Boss mod can't see players
- Check relay server is running (`npm start`)
- Verify `relayUrl` in boss config is correct
- Check relay logs for connections

### Player mod not receiving commands
- Check `relayUrl` uses `ws://` not `http://`
- Verify player connected (check relay server logs)
- Try `/reload` in game or restart Minecraft

### "Connection refused" errors
- Make sure relay server is accessible
- Check firewall isn't blocking port 3000
- For remote: verify Cloudflare tunnel is running

### Commands not executing
- Check Minecraft logs for errors
- Verify command syntax is correct
- Player must be in-game (not in menu)

---

## 📊 Relay Server API

### GET `/health`
Health check endpoint.

### GET `/players`
Returns list of connected players.
```json
{"players": ["Player1", "Player2"]}
```

### POST `/command`
Send command to player.
```json
{
  "target": "Player1",
  "command": "/tp @s 100 64 100"
}
```

### WebSocket Connection
Players connect via WebSocket to receive commands.

---

## 🎯 Use Cases

- **Server Management**: Control player positions/inventory
- **Events**: Coordinate multiplayer events
- **Teaching**: Demonstrate techniques remotely
- **Pranks**: Harmless fun with friends
- **Debugging**: Test commands on multiple clients

---

## 📦 File Structure

```
BOTFORCHAT/
├── relayserver/          # Central relay server
│   ├── server.js
│   └── package.json
├── minecraftbossmod/     # Boss mod (your control panel)
│   ├── src/
│   └── build.gradle
├── minecraftplayermod/   # Player mod (for others)
│   ├── src/
│   └── build.gradle
└── REMOTE_CONTROL_SETUP.md (this file)
```

---

## 🔄 Updates

**To update mods:**
1. Rebuild with `./gradlew build`
2. Replace old `.jar` in `mods/` folder
3. Restart Minecraft

**To update relay server:**
1. Pull new code
2. `npm install`
3. Restart server

---

## ⚡ Performance

- **Relay Server**: <10MB RAM, negligible CPU
- **Boss Mod**: Minimal impact, GUI only when open
- **Player Mod**: ~1-5ms per command, WebSocket connection

---

## 📝 License

MIT - Use freely, modify as needed.

---

## 🤝 Support

Check logs:
- Relay server: Terminal output
- Mods: `.minecraft/logs/latest.log`

All three components must be running and configured correctly!
