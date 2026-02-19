# Minecraft Discord Chat Bridge Bot

A Discord bot that bridges Minecraft in-game chat with Discord, exposing an HTTP API for the Minecraft mod to poll for messages.

## Features

- Listens to Discord messages
- Exposes REST API endpoint for Minecraft mod
- Lightweight and easy to deploy
- Docker support

## Quick Start

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Configure your bot**:
   - Copy `.env.example` to `Discordtoken.env`
   - Add your Discord bot token

3. **Run the bot**:
   ```bash
   npm start
   ```

## API Endpoints

### GET `/command`
Returns the latest Discord message received.

**Response Headers**:
- `X-Command-Id`: Incrementing counter for each new message

**Response**: Plain text of the last message

## Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed hosting instructions including:
- Railway (recommended)
- Render
- Docker/Docker Compose
- VPS with PM2
- Fly.io

## Environment Variables

- `TOKEN` (required): Your Discord bot token
- `PORT` (optional): API server port (default: 3000)

## Discord Bot Setup

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application
3. Go to "Bot" section and create a bot
4. Enable these Privileged Gateway Intents:
   - Message Content Intent
   - Guild Messages
5. Copy your bot token
6. Invite bot to your server using OAuth2 URL with `bot` and `applications.commands` scopes

## Project Structure

- `bot.js` - Main bot application
- `Dockerfile` - Docker container configuration
- `docker-compose.yml` - Docker Compose setup
- `DEPLOYMENT.md` - Detailed deployment guide

## License

ISC
