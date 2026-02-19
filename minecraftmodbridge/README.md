# Chat Bridge Mod (Fabric, 1.21+)

Reads input text from your Discord bridge API and applies it in Minecraft.

The bundled Discord bridge API in this repo returns the command as plain text plus an `X-Command-Id` header, so repeated identical messages are still treated as new inputs.

## Input rules

Single token movement controls (only when alone):

- `w` or `forward` -> hold forward
- `s`, `back`, or `backward` -> hold back
- `a` or `left` -> hold left
- `d` or `right` -> hold right
- `jump` or `space` -> hold jump
- `sneak` or `shift` -> hold sneak
- `sprint` or `run` -> hold sprint
- `stop` -> release all movement keys

Everything else is sent as normal player chat input:

- `/pay resinsword 1` runs as command
- `hello` sends as chat message
- `w hello` sends as chat (does NOT trigger movement because it is not alone)

## Polling

- Polls API at `60` times per second (`~16ms`)
- Default endpoint: `http://localhost:3000/command`

## Config file

Generated at first run:

- `config/chatbridge.properties`

Options:

- `enabled=true`
- `apiUrl=http://localhost:3000/command`
- `pollIntervalMs=16`

## Build

1. Create wrapper if needed: `gradle wrapper`
2. Build: `./gradlew build`
3. Copy jar from `build/libs/` to your Minecraft `mods` folder.
