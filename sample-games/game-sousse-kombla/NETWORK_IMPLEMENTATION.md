# Kombla Game - Network Implementation Summary

## Overview
This document summarizes the changes made to enable two-player network functionality in the Kombla game using TCP sockets.

## Server-Side Implementation

### 1. TCPMainServerDAO (`main/server/dal/TCPMainServerDAO.java`)
**Purpose:** Handles all server-side network communication

**Key Methods:**
- `start()` - Launches server socket in background thread
- `startServer()` - Accepts incoming client connections
- `processClient()` - Handles individual client communication:
  - Reads CONNECT command and player name
  - Sends StartGameInfo (playerId, maze layout)
  - Loops to read player commands (LEFT, RIGHT, UP, DOWN, FIRE)
- `writePlayer()` - Serializes player data (id, name, properties)
- `writeSprite()` - Serializes sprite data (id, kind, location, direction, movementStyle, properties)
- `sendModelChanged()` - Broadcasts game state to all connected clients every frame

**Protocol:** Binary protocol using DataInputStream/DataOutputStream

### 2. MainServerEngine (`main/server/engine/MainServerEngine.java`)
**Changes:**
- Instantiated `TCPMainServerDAO` (line 30)
- Added import for `TCPMainServerDAO`

## Client-Side Implementation

### 3. TCPMainClientDAO (`main/client/dal/TCPMainClientDAO.java`)
**Purpose:** Handles all client-side network communication

**Key Methods:**
- `start()` - Initializes listener and config
- `connect()` - Connects to server, sends player name, receives StartGameInfo
- `readStartGameInfo()` - Reads playerId and maze layout
- `onLoopReceiveModelChanged()` - Background thread continuously reading game updates
- `readModelChanged()` - Deserializes game state (frame, players, sprites)
- `readPlayer()` - Deserializes player data
- `readSprite()` - Deserializes sprite data (including movementStyle)
- `sendMoveLeft/Right/Up/Down/Fire()` - Sends player commands to server

### 4. MainClientEngine (`main/client/engine/MainClientEngine.java`)
**Changes:**
- Instantiated `TCPMainClientDAO` (line 21)
- Added import for `TCPMainClientDAO`

### 5. WelcomeController (`welcome/WelcomeController.java`)
**Changes:**
- Added playerName field reading (line 58)
- Set playerName in AppConfig for HOST mode (line 75)
- Set playerName in AppConfig for JOIN mode (line 81)

## Network Protocol

### Connection Flow:
1. Client → Server: `CONNECT` command + player name
2. Server → Client: `OK` status + playerId + maze dimensions + maze data
3. Client ↔ Server: Continuous command/update exchange

### Game Updates (every frame):
```
Server → Client:
- Frame number (long)
- Player count + Player data (id, name, properties)
- Sprite count + Sprite data (id, kind, name, x, y, direction, playerId, movementStyle, properties)
```

### Player Commands:
```
Client → Server: Command constants (LEFT=2, RIGHT=3, UP=4, DOWN=5, FIRE=6)
```

## Key Design Decisions

1. **TCP Protocol:** Reliable, ordered delivery for game state
2. **Binary Serialization:** Efficient using DataInputStream/DataOutputStream
3. **Centralized Authority:** Server maintains authoritative game state
4. **Broadcast Updates:** Server sends full game state to all clients every frame
5. **MovementStyle Transmission:** Required for correct sprite animations on client

## Default Configuration
- **Port:** 1234
- **Address:** localhost (for same-machine testing)
- **Player Name:** "boss" (default)

## Files Modified
- `TCPMainServerDAO.java` - Created (216 lines)
- `TCPMainClientDAO.java` - Created (177 lines)
- `MainServerEngine.java` - 2 lines changed
- `MainClientEngine.java` - 2 lines changed
- `WelcomeController.java` - 3 lines added
