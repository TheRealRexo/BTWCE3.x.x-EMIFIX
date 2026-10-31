# BTW EMI Fix

A small Fabric client-side addon for Better Than Wolves CE that fixes EMI favorites disappearing on game shutdown and makes them world-specific.

## What it does

- **Fixes disappearing favorites**: EMI favorites are now saved when the game exits
- **World-specific storage**: Each world keeps its own favorites list in `emi_<world-id>.json`
- **Fallback safety**: A JVM shutdown hook ensures data is saved even if the game crashes

## Installation

1. Make sure you have **Better Than Wolves CE 3.1.1+** installed
2. Place `btw-emi-fix-*.jar` in your `.minecraft/mods/` folder
3. Launch the game - the mod will automatically create per-world favorites files

## How it works

The mod hooks into the client lifecycle and EMI's persistence system:

1. On world load, it reads `emi_<world-id>.json` from the world folder
2. On world unload, it saves the current favorites to that file
3. On game shutdown, a fallback hook saves to the global `emi.json` as well

This means your favorite stacks are different for each world you play.

## Build

```bash
./gradlew build
```

The JAR will be at `build/libs/btw-emi-fix-1.0.0.jar`.

## License

GPL-3.0-only
