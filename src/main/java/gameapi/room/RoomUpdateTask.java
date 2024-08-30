package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;
import gameapi.event.block.RoomBlockTreadEvent;
import gameapi.event.player.RoomPlayerEnterPortalEvent;
import gameapi.event.player.RoomPlayerMoveEvent;
import gameapi.extensions.checkpoint.CheckpointData;
import gameapi.extensions.obstacle.DynamicObstacle;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.task.RoomAdvancedUpdateTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author glorydark
 * @date {2023/12/27} {16:16}
 */
public class RoomUpdateTask implements Runnable {

    private final Room room;

    private final List<RoomAdvancedUpdateTask> customTickListenerList = new ArrayList<>();

    private final HashMap<Player, Location> playerLocationHashMap = new HashMap<>();

    public RoomUpdateTask(Room room) {
        this.room = room;
    }

    public void addListener(Consumer<Room> roomConsumer) {
        this.customTickListenerList.add(new RoomAdvancedUpdateTask() {
            @Override
            public void onUpdate(Room room) {
                roomConsumer.accept(room);
            }
        });
    }

    public void addListener(RoomAdvancedUpdateTask roomAdvancedUpdateTask) {
        this.customTickListenerList.add(roomAdvancedUpdateTask);
    }

    @Override
    public void run() {
        if (this.room.getPlayers().isEmpty()) {
            return;
        }
        for (Player player : new ArrayList<>(this.playerLocationHashMap.keySet())) {
            if (!this.room.hasPlayer(player)) {
                this.playerLocationHashMap.remove(player);
            }
        }
        try {
            List<FullChunk> updateLightChunks = new ArrayList<>();
            // Internal Process
            for (Player player : this.room.getPlayers()) {
                if (player.getGamemode() != 3) {
                    this.onUpdateRoomBlockTreadEvent(player);
                    this.onUpdateRoomPlayerEnterPortalEvent(player);
                    this.onUpdateRoomPlayerMovementEvent(player);
                    // RecordPoint
                    this.room.getCheckpointManager().onUpdate(player);
                }

                FullChunk chunk = player.getChunk();
                if (!updateLightChunks.contains(chunk)) {
                    chunk.populateSkyLight();
                    updateLightChunks.add(chunk);
                }
            }

            if (!this.room.getCheckpointManager().getCheckpointDataList().isEmpty()) {
                for (CheckpointData checkPointData : this.room.getCheckpointManager().getCheckpointDataList()) {
                    checkPointData.showParticleMarks(this.room.getPlayLevels().get(0));
                }
            }

            for (RoomAdvancedUpdateTask customRoomUpdateTask : new ArrayList<>(this.customTickListenerList)) {
                if (customRoomUpdateTask.isCancelled()) {
                    this.customTickListenerList.remove(customRoomUpdateTask);
                }
                customRoomUpdateTask.setTick(customRoomUpdateTask.getTick() + 1);
                customRoomUpdateTask.onUpdate(this.room);
            }

            this.onTickDynamicObstacles();
            if (!this.room.getNbsMusicManager().isStopped()) {
                this.room.getNbsMusicManager().onTick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            GameAPI.getGameDebugManager().error(e.getCause().getMessage() + "\n"
                    + e + ":\n"
                    + Arrays.toString(e.getStackTrace()).replace("[", "\n").replace("]", "\n").replace(", ", "\n")
            );
        }
    }

    protected Location getPlayerLastLocation(Player player) {
        return this.playerLocationHashMap.getOrDefault(player, player.add(0, 1, 0));
    }

    public void setPlayerLastLocation(Player player, Location location) {
        this.playerLocationHashMap.put(player, location);
    }

    protected void onUpdateRoomBlockTreadEvent(Player player) {
        // Tread Block Event
        Vector3 vector3 = player.getPosition().floor().subtract(0, 1, 0);
        SimpleAxisAlignedBB bb = (SimpleAxisAlignedBB) new SimpleAxisAlignedBB(vector3, vector3).expand(1, 1, 1);
        bb.forEach(((i, i1, i2) -> {
            Block block = player.getLevel().getBlock(i, i1, i2).getLevelBlock();
            if (!(block.getId() == 0 || block instanceof BlockLiquid)) {
                RoomBlockTreadEvent roomBlockTreadEvent = new RoomBlockTreadEvent(room, block, player);
                GameListenerRegistry.callEvent(room, roomBlockTreadEvent);
                if (!roomBlockTreadEvent.isCancelled()) {
                    for (DynamicObstacle dynamicObstacle : new ArrayList<>(room.getDynamicObstacles())) {
                        for (Block dynamicObstacleBlock : dynamicObstacle.getBlocks()) {
                            if (dynamicObstacleBlock.distanceSquared(block) < 1d) {
                                dynamicObstacle.onTread(dynamicObstacleBlock);
                            }
                        }
                    }
                }
            }
        }));
    }

    protected void onUpdateRoomPlayerEnterPortalEvent(Player player) {
        if (player.blocksAround == null) {
            player.blocksAround = new ArrayList<>();
        }
        if (player.collisionBlocks == null) {
            player.collisionBlocks = new ArrayList<>();
        }
        for (Block collisionBlock : new ArrayList<>(player.getCollisionBlocks())) {
            if (collisionBlock.getId() == 90) {
                RoomPlayerEnterPortalEvent roomPlayerEnterPortalEvent = new RoomPlayerEnterPortalEvent(room, player);
                GameListenerRegistry.callEvent(room, roomPlayerEnterPortalEvent);
                player.inPortalTicks = 0;
                break;
            }
        }
    }

    protected void onUpdateRoomPlayerMovementEvent(Player player) {
        if (player.getLocation().equals(getPlayerLastLocation(player))) {
            return;
        }
        if ((this.room.getRoomStatus() == RoomStatus.ROOM_STATUS_READY_START || this.room.getRoomStatus() == RoomStatus.ROOM_STATUS_NEXT_ROUND_PRESTART) && !this.room.getRoomRule().isAllowReadyStartWalk()) {
            return;
        }
        // MoveEvent
        RoomPlayerMoveEvent roomPlayerMoveEvent = new RoomPlayerMoveEvent(this.room, player, getPlayerLastLocation(player), player.getLocation());
        GameListenerRegistry.callEvent(this.room, roomPlayerMoveEvent);
        if (roomPlayerMoveEvent.isCancelled()) {
            Location from = roomPlayerMoveEvent.getFrom();
            if (from != null) {
                player.teleport(from, null);
            }
        } else {
            setPlayerLastLocation(player, roomPlayerMoveEvent.getTo());
        }
    }

    protected void onTickDynamicObstacles() {
        for (DynamicObstacle dynamicObstacle : new ArrayList<>(room.getDynamicObstacles())) {
            dynamicObstacle.onTick();
        }
    }
}
