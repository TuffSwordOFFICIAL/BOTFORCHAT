# Discord Bot Hosting Guide

This guide provides multiple options for hosting your Discord bot.

## Prerequisites

- Your Discord bot token (stored in `Discordtoken.env`)
- Node.js 16+ (for local/VPS hosting)
- Docker (optional, for containerized deployment)

---

## Option 1: Railway (Recommended - Easy & Free Tier)

Railway is one of the easiest platforms to deploy to.

1. **Install Railway CLI** (optional):
   ```bash
   npm i -g @railway/cli
   ```

2. **Deploy via GitHub**:
   - Go to [Railway.app](https://railway.app)
   - Sign up with GitHub
   - Click "New Project" → "Deploy from GitHub repo"
   - Select this repository
   - Set the root directory to `minecraftbotdisc`
   - Add environment variable: `TOKEN` (your Discord bot token)
   - Railway will auto-detect and deploy!

3. **Deploy via CLI**:
   ```bash
   cd minecraftbotdisc
   railway login
   railway init
   railway up
   railway variables set TOKEN=your_token_here
   ```

**Pros**: Free tier, auto-deploys from GitHub, easy setup
**Cons**: Free tier has usage limits

---

## Option 2: Render

1. Go to [Render.com](https://render.com)
2. Sign up and create a new "Web Service"
3. Connect your GitHub repository
4. Configure:
   - **Root Directory**: `minecraftbotdisc`
   - **Build Command**: `npm install`
   - **Start Command**: `node bot.js`
5. Add environment variable: `TOKEN` = your Discord bot token
6. Deploy!

**Pros**: Free tier available, simple interface
**Cons**: Free tier spins down after inactivity (not ideal for bots)

---

## Option 3: Docker (VPS/Self-Hosted)

If you have a VPS (DigitalOcean, AWS, Linode, etc.), use Docker:

### Using Docker Compose (Easiest)

```bash
cd minecraftbotdisc

# Make sure Discordtoken.env has your token:
# TOKEN=your_token_here

# Build and run
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### Using Docker directly

```bash
cd minecraftbotdisc

# Build image
docker build -t discord-bot .

# Run container
docker run -d \
  --name minecraft-discord-bot \
  -p 3000:3000 \
  -e TOKEN=your_token_here \
  --restart unless-stopped \
  discord-bot

# View logs
docker logs -f minecraft-discord-bot
```

**Pros**: Full control, runs anywhere
**Cons**: Requires server management

---

## Option 4: VPS with PM2 (Process Manager)

For a Linux VPS without Docker:

1. **Install Node.js** (if not installed):
   ```bash
   curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
   sudo apt-get install -y nodejs
   ```

2. **Install PM2**:
   ```bash
   sudo npm install -g pm2
   ```

3. **Deploy your bot**:
   ```bash
   # Clone/upload your code to the server
   cd minecraftbotdisc
   npm install
   
   # Make sure Discordtoken.env exists with your token
   
   # Start with PM2
   pm2 start bot.js --name minecraft-discord-bot
   
   # Setup auto-restart on server reboot
   pm2 startup
   pm2 save
   ```

4. **Useful PM2 commands**:
   ```bash
   pm2 status              # Check status
   pm2 logs                # View logs
   pm2 restart minecraft-discord-bot
   pm2 stop minecraft-discord-bot
   pm2 delete minecraft-discord-bot
   ```

**Pros**: Good control, stable, works on any VPS
**Cons**: Manual server setup required

---

## Option 5: Fly.io

1. Install flyctl: https://fly.io/docs/hands-on/install-flyctl/
2. Login: `flyctl auth login`
3. Initialize app:
   ```bash
   cd minecraftbotdisc
   flyctl launch
   ```
4. Set your token:
   ```bash
   flyctl secrets set TOKEN=your_token_here
   ```
5. Deploy:
   ```bash
   flyctl deploy
   ```

**Pros**: Free tier, easy deployment
**Cons**: Requires flyctl setup

---

## Testing Locally

Before deploying, test locally:

```bash
cd minecraftbotdisc
npm install
node bot.js
```

The bot should log in and display: `Logged in as YourBotName#1234`

Access the API at: http://localhost:3000/command

---

## Environment Variables

Make sure to set these environment variables in your hosting platform:

- `TOKEN` - Your Discord bot token (required)
- `PORT` - API port (optional, defaults to 3000)

---

## Security Notes

⚠️ **Important**: Never commit `Discordtoken.env` to GitHub!
- Add it to `.gitignore`
- Use environment variables in production
- Regenerate your token if it's been exposed

---

## Troubleshooting

### Bot not starting
- Check that your TOKEN is correct
- Verify your bot has the correct intents enabled in Discord Developer Portal

### API not accessible
- Check that the PORT is correctly exposed/configured
- Verify firewall rules on your hosting platform

### Bot goes offline
- Use a platform with "always on" (Railway, PM2, Docker)
- Avoid Render's free tier (spins down)

---

## Next Steps

1. Choose a hosting option above
2. Deploy your bot
3. Note the API URL for your Minecraft mod to connect to
4. Keep your token secure!

Need help? Check your platform's documentation or create an issue in this repo.
