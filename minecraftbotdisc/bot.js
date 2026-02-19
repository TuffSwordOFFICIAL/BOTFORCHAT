require('dotenv').config();
const { Client, GatewayIntentBits } = require('discord.js');
const express = require('express');
const app = express();

app.use(express.json());

const client = new Client({
    intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages, GatewayIntentBits.MessageContent]
});

const token = process.env.TOKEN;
if (!token) {
    console.error("Error: Discord bot token not found! Set TOKEN in your environment variables.");
    process.exit(1);
}

let latestCommand = "";
let latestCommandId = 0;

client.on('messageCreate', message => {
    if (message.author.bot) return;
    latestCommand = message.content;
    latestCommandId += 1;
});

client.once('ready', () => {
    console.log(`Logged in as ${client.user.tag}`);
});

app.get('/command', (req, res) => {
    res.set('X-Command-Id', String(latestCommandId));
    res.type('text/plain');
    res.send(latestCommand);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`API running on port ${PORT}`));

client.login(token);
