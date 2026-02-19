FROM node:20-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install --production

# Copy application files
COPY bot.js ./

# Expose the API port
EXPOSE 3000

# Start the bot
CMD ["node", "bot.js"]
