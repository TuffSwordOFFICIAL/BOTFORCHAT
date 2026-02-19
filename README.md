# BOTFORCHAT

Discord to Minecraft Chat Bridge - Send Discord messages directly into Minecraft!

## 🎮 What This Does

- **Discord Bot**: Listens to Discord messages and exposes them via HTTP API
- **Minecraft Mod**: Polls the API and displays messages in-game chat

## 📁 Project Structure

```
BOTFORCHAT/
├── minecraftbotdisc/     # Discord bot (Node.js) - Deploy to Fly.io
└── minecraftmodbridge/   # Minecraft Fabric mod (Java) - Install locally
```

## 🚀 Quick Start

### 1. Deploy Discord Bot to Fly.io

See [minecraftbotdisc/DEPLOYMENT.md](minecraftbotdisc/DEPLOYMENT.md) for full instructions.

**Quick steps:**
1. Go to [Fly.io Dashboard](https://fly.io/dashboard)
2. Import from GitHub: `TuffSwordOFFICIAL/BOTFORCHAT`
3. Set root directory: `minecraftbotdisc`
4. Add secret: `TOKEN=your_discord_bot_token`
5. Deploy!

Your bot API will be at: `https://your-app-name.fly.dev/command`

### 2. Install Minecraft Mod

See [MINECRAFT_SETUP.md](MINECRAFT_SETUP.md) for detailed setup.

**Quick steps:**
1. Build mod: `cd minecraftmodbridge && ./gradlew build`
2. Copy `.jar` from `build/libs/` to Minecraft `mods/` folder
3. Launch Minecraft once, then close it
4. Edit `.minecraft/config/chatbridge.properties`:
   ```properties
   enabled=true
   apiUrl=https://your-app-name.fly.dev/command
   pollIntervalMs=16
   ```
5. Launch Minecraft - messages now flow from Discord to game!

## 📚 Documentation

- [Discord Bot Deployment Guide](minecraftbotdisc/DEPLOYMENT.md)
- [Minecraft Mod Setup](MINECRAFT_SETUP.md)
- [Discord Bot README](minecraftbotdisc/README.md)

## 🔧 Requirements

**For Discord Bot:**
- Discord bot token with Message Content intent enabled
- Fly.io account (free tier works)

**For Minecraft Mod:**
- Minecraft Java Edition
- Fabric Loader
- Java 17+

## 🛠️ Development

**Test bot locally:**
```bash
cd minecraftbotdisc
npm install
npm start
```

**Build Minecraft mod:**
```bash
cd minecraftmodbridge
./gradlew build
```

## 📝 License

ISC
