package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import gameapi.GameAPI;
import gameapi.entity.TextEntity;
import gameapi.event.block.RoomBlockTreadEvent;
import gameapi.event.player.RoomPlayerEnterPortalEvent;
import gameapi.event.player.RoomPlayerMoveEvent;
import gameapi.extensions.checkpoint.CheckpointData;
import gameapi.extensions.obstacle.DynamicObstacle;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.items.RoomItemBase;
import gameapi.room.task.RoomPreciseUpdateTask;
import gameapi.tools.PlayerTools;
import gameapi.tools.VanillaCustomMusicTools;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author glorydark
 * @date {2023/12/27} {16:16}
 */
public class RoomUpdateTask implements Runnable {

    private final Room room;

    private final List<RoomPreciseUpdateTask> customTickListenerList = new ArrayList<>();

    private final Map<Player, Location> playerLocationHashMap = new HashMap<>();

    private boolean cancel = false;

    public RoomUpdateTask(Room room) {
        this.room = room;
    }

    public void addListener(Consumer<Room> roomConsumer) {
        this.addListener(new RoomPreciseUpdateTask() {
            @Override
            public void onUpdate(Room room) {
                roomConsumer.accept(room);
            }
        });
    }

    public void addListener(RoomPreciseUpdateTask roomPreciseUpdateTask) {
        roomPreciseUpdateTask.onStart(this.room);
        this.customTickListenerList.add(roomPreciseUpdateTask);
    }

    @Override
    public void run() {
        if (this.cancel) {
            return;
        }
        if (this.room.getPlayers().isEmpty()) {
            return;
        }
        for (Player player : new ArrayList<>(this.playerLocationHashMap.keySet())) {
            if (!this.room.hasPlayer(player)) {
                this.playerLocationHashMap.remove(player);
            }
        }
        if (!this.room.getRoomRule().isVanillaCustomMusic()) {
            VanillaCustomMusicTools.stopCustomMusic(0f, this.room.getPlayers().toArray(new Player[0]));
            VanillaCustomMusicTools.stopCustomMusic(0f, this.room.getSpectators().toArray(new Player[0]));
        }
        try {
            // Internal Process
            for (Player player : new ArrayList<>(this.room.getPlayers())) {
                if (player.getGamemode() != 3) {
                    this.onUpdateRoomBlockTreadEvent(player);
                    this.onUpdateRoomPlayerEnterPortalEvent(player);
                    this.onUpdateRoomPlayerMovementEvent(player);
                    // RecordPoint
                    this.room.getCheckpointManager().onUpdate(player);
                }
            }

            if (Server.getInstance().getTick() % 20 == 0) {
                if (!this.room.getCheckpointManager().getCheckpointDataList().isEmpty()) {
                    for (CheckpointData checkPointData : this.room.getCheckpointManager().getCheckpointDataList()) {
                        checkPointData.showParticleMarks(this.room.getPlayLevels().get(0));
                    }
                }
            }

            for (RoomPreciseUpdateTask task : new ArrayList<>(this.customTickListenerList)) {
                if (task.isCancelled()) {
                    task.onEnd(this.room);
                    this.customTickListenerList.remove(task);
                    continue;
                }
                task.setTick(task.getTick() + 1);
                task.onUpdate(this.room);
            }

            this.onTickDynamicObstacles();
            if (this.room.getNbsMusicManager() != null) {
                if (!this.room.getNbsMusicManager().isStopped()) {
                    this.room.getNbsMusicManager().onTick();
                }
            }
            if (this.room.getOggMusicManager() != null) {
                if (!this.room.getOggMusicManager().isStopped()) {
                    this.room.getOggMusicManager().onTick();
                }
            }
            this.onUpdateRoomItemHeld();

            if (this.room.getTextEntities().isEmpty()) {
                for (TextEntity textEntity : new ArrayList<>(this.room.getTextEntities())) {
                    if (!textEntity.isAlive() || textEntity.isClosed()) {
                        textEntity.respawn();
                        this.room.getTextEntities().remove(textEntity);
                    } else {
                        textEntity.onAsyncUpdate(Server.getInstance().getTick());
                    }
                }
            }
        } catch (Throwable e) {
            GameAPI.getGameDebugManager().printError(e);
        }
    }

    protected Location getPlayerLastLocation(Player player) {
        return this.playerLocationHashMap.getOrDefault(player, player.add(0, 1, 0));
    }

    public void setPlayerLastLocation(Player player, Location location) {
        this.playerLocationHashMap.put(player, location);
    }

    protected void onUpdateRoomBlockTreadEvent(Player player) {
        if (player.isValid() && player.getLevel().getProvider() != null) {
            // Tread Block Event
            Block block = PlayerTools.getBlockUnderPlayer(player);
            if (block != null) {
                RoomBlockTreadEvent roomBlockTreadEvent = new RoomBlockTreadEvent(this.room, block, player);
                GameListenerRegistry.callEvent(this.room, roomBlockTreadEvent);
                if (!roomBlockTreadEvent.isCancelled()) {
                    for (DynamicObstacle dynamicObstacle : new ArrayList<>(this.room.getDynamicObstacles())) {
                        if (!dynamicObstacle.isEnabled()) {
                            continue;
                        }
                        for (Block dynamicObstacleBlock : dynamicObstacle.getBlocks()) {
                            if (dynamicObstacleBlock.distanceSquared(block) < 1d) {
                                dynamicObstacle.onTread(dynamicObstacleBlock);
                            }
                        }
                    }
                }
            }
        }
    }

    protected void onUpdateRoomPlayerEnterPortalEvent(Player player) {
        try {
            if (player.blocksAround == null) {
                player.blocksAround = new ArrayList<>();
            }
            if (player.collisionBlocks == null) {
                player.collisionBlocks = new ArrayList<>();
            }
            List<Block> collisionBlocks = new ArrayList<>(player.collisionBlocks);
            collisionBlocks.removeIf(Objects::isNull);
            for (Block collisionBlock : collisionBlocks) {
                if (collisionBlock.getId() == 90) {
                    RoomPlayerEnterPortalEvent roomPlayerEnterPortalEvent = new RoomPlayerEnterPortalEvent(this.room, player);
                    GameListenerRegistry.callEvent(this.room, roomPlayerEnterPortalEvent);
                    player.inPortalTicks = 0;
                    break;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    protected void onUpdateRoomPlayerMovementEvent(Player player) {
        Location lastPosition = getPlayerLastLocation(player);
        if (player.getLocation().equals(lastPosition)) {
            return;
        }
        if (!this.room.getRoomRule().isAllowWalk()) {
            if (lastPosition != null && lastPosition.getLevel() == player.getLevel()) {
                player.teleport(lastPosition, PlayerTeleportEvent.TeleportCause.PLUGIN);
                return;
            }
        }
        // MoveEvent
        RoomPlayerMoveEvent roomPlayerMoveEvent = new RoomPlayerMoveEvent(this.room, player, lastPosition, player.getLocation());
        GameListenerRegistry.callEvent(this.room, roomPlayerMoveEvent);
        if (roomPlayerMoveEvent.isCancelled()) {
            Location from = roomPlayerMoveEvent.getFrom();
            if (from != null) {
                player.teleport(from, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        } else {
            setPlayerLastLocation(player, roomPlayerMoveEvent.getTo());
        }
    }

    protected void onTickDynamicObstacles() {
        for (DynamicObstacle dynamicObstacle : new ArrayList<>(this.room.getDynamicObstacles())) {
            if (dynamicObstacle.isEnabled()) {
                dynamicObstacle.onTick();
            }
        }
    }

    protected void onUpdateRoomItemHeld() {
        for (Player player : this.room.getPlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            Item item = player.getInventory().getItemInHand();
            RoomItemBase roomItemBase = this.room.getRoomItem(RoomItemBase.getRoomItemIdentifier(item));
            if (roomItemBase != null) {
                long nextUseMillis = item.getNamedTag().getLong("next_use_millis");
                if (System.currentTimeMillis() >= nextUseMillis) {
                    roomItemBase.onUpdate(this.room, player, item);
                }
            }
        }
    }

    public void cancel() {
        this.cancel = true;
    }
}
