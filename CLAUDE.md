# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

GameAPI is a **framework plugin** for the Nukkit (Minecraft: Bedrock) server platform — primarily the **MOT** Nukkit fork. It is *not* a standalone game. Other plugins (e.g. TNTRun, Spleef) declare `depend: GameAPI` and build mini-games on top of its `Room` abstraction, event bus, state machine, and tooling. When reasoning about a change, assume the consumers are *other plugins* calling this code, so public API shape and backward compatibility matter.

`GameAPI小游戏开发规范.md` (Chinese) is the authoritative, worked guide for building a downstream mini-game on this framework — read it when a task is about *how a game uses* GameAPI rather than GameAPI internals. It documents the standard `GameMain` → `RoomFactory` → `BaseGameListener` → command flow with full code templates.

## Build

Maven, Java 17. Dependencies resolve from `repo.lanink.cn` and jitpack — a clean build needs network access to those.

```bash
mvn package        # → target/GameAPI-1.8.9.jar (shaded, ready to drop in plugins/)
mvn deploy         # publish to repo.lanink.cn (distributionManagement)
```

There are **no unit tests** — this is a server plugin verified by running it on a Nukkit-MOT server. `maven-shade` relocates nothing but bundles `org.jnbt.*` (the only vendored dependency) into the jar; everything else is `provided`.

## Core architecture

Everything orbits the `Room` (`room/Room.java`, ~1300 lines) — one game session/instance. Downstream games rarely subclass it; they attach behavior via the event bus and attach state via Room's property stores.

### Two game loops (most important non-obvious detail)

A Room is driven by **two independent loops on different threads** — know which one your code runs on:

1. **Status loop — main server thread, 1 Hz.** `task/RoomTask.java` is a Nukkit `Task` scheduled every 20 ticks (see `GameAPI.onEnable`). It calls `CustomRoomStatus.onTick(room)` and advances `StageState`s. This is what fires the per-second lifecycle `*TickEvent`s (e.g. `RoomGameStartTickEvent`). Heavy/blocking work here stalls the whole server — keep it light.
2. **Precise loop — per-room thread pool, 20 Hz.** Each `Room` owns its own `ScheduledExecutorService` and runs `room/RoomUpdateTask.java` every `GAME_TASK_INTERVAL * 50` ms (= every tick, **off the main thread**). It handles movement detection, `RoomBlockTreadEvent`, portal detection, checkpoints, dynamic obstacles, music ticking, and held room-item `onUpdate`. Because this is async, code reached from here touches the world on a non-server thread — be deliberate about thread safety.

### Custom event bus (parallel to Nukkit's, not the same system)

`listener/base/GameListenerRegistry.java` is a **separate event bus keyed by game name** (a `String`). It is *not* Nukkit's event system.

- `listener/BaseEventListener.java` (+ `listener/mot/BaseEventListenerMOTPatch.java` on MOT) is the real Nukkit `Listener`. It catches Nukkit events at `MONITOR` priority, finds the player's room via `RoomManager.getRoom(player)`, wraps them as `Room*Event` (`event/**`), and re-dispatches through `GameListenerRegistry.callEvent(room, event)`. So a Nukkit `BlockBreakEvent` becomes a `RoomBlockBreakEvent` only for players currently in a room.
- Listeners are registered **per game name**, so a game's handlers only fire for its own rooms. `KEY_GLOBAL_LISTENER = "global"` registers across all games.
- Two equivalent registration styles: **Consumer** (`registerEvents(gameName, EventClass.class, this::handler)`) and **annotation** (`@GameEventHandler` methods + `registerEvents(gameName, listenerObj, plugin)`).
- Dispatch is **three-phase**: `newListenersBeforeOld` → annotation-based "old" listeners → `newListenersAfterOld`, each ordered by `EventPriority`. The before/after split lets new-style handlers run relative to legacy ones.

### State machine

`room/status/` + `room/status/factory/RoomDefaultStatusFactory.java`.

- `CustomRoomStatus` is an abstract status; instances self-register into `RoomDefaultStatusFactory.REGISTRY` keyed by a string identifier (`"wait"`, `"game_start"`, …).
- A Room's flow is an ordered `List<CustomRoomStatus>` (`roomStatusList`, default: WAIT → PRESTART → READY_START → GAME_START → GAME_END → CEREMONY → ROOM_END). `getNextRoomStatus` walks this list.
- `room.setCurrentRoomStatus(status[, reason])` calls `status.onEnter(room)` and fires `RoomCustomStatusChangeEvent`. Each status's `onEnter`/`onTick` fires its own lifecycle event (e.g. `RoomStatusGameStart` fires `RoomGameStartEvent` on enter, `RoomGameStartTickEvent` per tick). **Use `setCurrentRoomStatus(...)` to end a game normally; use `room.resetAll(reason)` to tear it down.**
- Statuses also expose **per-phase behavior gates** — `isAllowBreakBlock(room)`, `isAllowPlaceBlock(room)`, `isAllowEntityDamagedByEntity(room)`, etc. These are distinct from, and layered on top of, the static `RoomRule` flags.

### Room state stores

Three key-value maps on `Room`, the primary way downstream games attach state without subclassing:

- `roomProperty` — room-scoped (`getRoomProperty/setRoomProperty/hasRoomProperty`).
- `playerProperty` — per-player, game-facing (`getPlayerProperty/setPlayerProperty`).
- `internalPlayerProperty` — per-player, reserved for framework internals (`getInternalPlayerProperty/...`).

`RoomRule` (`room/RoomRule.java`) is the static per-room config set at creation (allow break/place, PVP, fall damage, respawn, time limit, chat, …).

### Managers

- **Static registries:** `RoomManager` (all loaded rooms as `Map<gameName, List<Room>>`, player→room map, `loadRoom`/`unloadRoom` — `loadRoom` is what schedules the room's `RoomUpdateTask`), plus data managers `PlayerGameDataManager`, `RankingManager`, `GameActivityManager`, `GlobalSettingsManager`, `AchievementManager`.
- **Per-room managers** (each `Room` owns an instance): `CheckpointManager`, `AdvancedBlockManager`, `GhostyManager` (replay), `RoomVirtualHealthManager`.

### Commands

`commands/base/EasyCommand` + `EasySubCommand`: a top-level `EasyCommand` holds a map of named subcommands; `execute` routes `args[0]` to the matching subcommand or falls back to `onExecuteDefault`. Top-level commands registered in `GameAPI.onEnable`: `gameapi`, `worldedit`, `hub`, `vanilla`.

### Forms

`form/` — `AdvancedFormWindowSimple/Custom/Modal` and `AdvancedChestForm`/`AdvancedHopperForm` wrap Nukkit forms with a **callback-per-element** model: each `ResponsiveElement*` carries its own `onRespond` handler, so you never parse a raw response-index array.

### Extensions

`extensions/` are optional self-contained subsystems that hook the precise loop: `checkpoint` (race tracks), `obstacle` (dynamic moving blocks), `particleGun`, `projectileGun`, `supplyChest`.

## Conventions

- **Lombok** is used heavily (`@Getter`/`@Setter` on `Room` and others) — generated accessors won't appear in source; don't hand-write them.
- **Logging/errors** go through `GameAPI.getGameDebugManager()` (`.info(...)`, `.printError(throwable)`), which writes to `plugins/GameAPI/logs/` and optionally the console — not `System.out`/`printStackTrace`.
- **User-facing strings** are i18n keys resolved via `GameAPI.getLanguage().getTranslation(player, "key")`, defined in `src/main/resources/languages/{zh_CN,en_US}.properties` with `%1%`, `%2%` placeholders. Keys are grouped by originating class. Add new strings to *both* property files.
- **Nukkit-fork compatibility:** check `NukkitTypeUtils.getNukkitType()` / `isMOTPatchVersion()` before using MOT-only APIs; MOT-specific listeners live under `listener/mot/`.
- The `glorydark-feature` config flag (off by default) gates author-specific experimental content (projectile guns, custom entities) in `GameAPI.onLoad`/`onEnable` — leave it off unless that is explicitly the task.
- Code comments and the dev guide are predominantly Chinese; match the surrounding language when editing nearby.
