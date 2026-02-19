# Connecting Minecraft Mod to Fly.io Bot

After deploying your Discord bot to Fly.io, follow these steps to connect your Minecraft mod to it.

## Step 1: Get Your Fly.io URL

After deploying on Fly.io, you'll receive a URL like:
```
https://your-app-name.fly.dev
```

Your API endpoint will be:
```
https://your-app-name.fly.dev/command
```

## Step 2: Configure the Minecraft Mod

1. **Build the Minecraft mod** (if you haven't already):
   ```bash
   cd minecraftmodbridge
   ./gradlew build
   ```
   The mod JAR will be in: `build/libs/`

2. **Install the mod**:
   - Copy the `.jar` file to your Minecraft `mods` folder
   - Make sure you have Fabric Loader installed

3. **Configure the API URL**:
   - Launch Minecraft once (this creates the config file)
   - Close Minecraft
   - Open: `.minecraft/config/chatbridge.properties`
   - Change the URL to your Fly.io endpoint:
   
   ```properties
   enabled=true
   apiUrl=https://your-app-name.fly.dev/command
   pollIntervalMs=16
   ```

4. **Launch Minecraft**:
   - Start Minecraft again
   - The mod will now poll your Fly.io hosted bot!

## Step 3: Test the Connection

1. **Send a message in Discord** (in the channel your bot is watching)
2. **In Minecraft**, the message should appear in chat within a few milliseconds

## Example Config File

Here's what your `chatbridge.properties` should look like:

```properties
# Chat Bridge config
enabled=true
apiUrl=https://minecraft-discord-bot.fly.dev/command
pollIntervalMs=16
```

**Replace** `minecraft-discord-bot` with your actual Fly.io app name!

## Troubleshooting

### Mod not receiving messages
- Check that the API URL is correct (including `https://`)
- Verify your bot is running on Fly.io (check logs)
- Make sure the bot is in your Discord server
- Test the API manually: open `https://your-app-name.fly.dev/command` in a browser

### "Connection timeout" errors
- Check that your Fly.io app is deployed and running
- Verify the URL is accessible from outside: `curl https://your-app-name.fly.dev/health`
- Make sure you're using `https://` not `http://`

### Messages not updating
- Try increasing `pollIntervalMs` to `50` or `100` if having issues
- Check Minecraft logs for errors

## Advanced: Custom Commands

Your Minecraft mod processes Discord messages as commands. You can extend the mod to:
- Filter specific users
- Parse command prefixes (e.g., `!command`)
- Execute specific actions based on message content

See `ChatBridgeClient.java` for modification details.

---

**Your full setup should be:**
1. ✅ Discord bot hosted on Fly.io
2. ✅ Minecraft mod installed with config pointing to Fly.io URL
3. ✅ Bot in Discord server with proper intents
4. ✅ Messages flow from Discord → Fly.io → Minecraft

Enjoy your chat bridge! 🎮💬
