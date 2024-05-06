package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import gameapi.event.block.RoomBlockTreadEvent;
import gameapi.event.player.RoomPlayerEnterPortalEvent;
import gameapi.event.player.RoomPlayerMoveEvent;
import gameapi.extensions.checkpoint.CheckpointData;
import gameapi.extensions.obstacle.DynamicObstacle;
import gameapi.listener.base.GameListenerRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author glorydark
 * @date {2023/12/27} {16:16}
 */
public class RoomUpdateTask extends Task {

    private final Room room;

    private final List<Consumer<Room>> customConsumerList = new ArrayList<>();

    private final HashMap<Player, Location> playerLocationHashMap = new HashMap<>();

    public RoomUpdateTask(Room room) {
        this.room = room;
    }

    public void addConsumer(Consumer<Room> roomConsumer) {
        customConsumerList.add(roomConsumer);
    }

    @Override
    public void onRun(int i) {
        for (Player player : new ArrayList<>(playerLocationHashMap.keySet())) {
            if (!room.hasPlayer(player)) {
                playerLocationHashMap.remove(player);
            }
        }
        if (room.getPlayers().size() == 0) {
            return;
        }
        // Internal Process
        for (Player player : room.getPlayers()) {
            if (player.getGamemode() != 3) {
                this.onUpdateRoomBlockTreadEvent(player);
                this.onUpdateRoomPlayerEnterPortalEvent(player);
                this.onUpdateRoomPlayerMovementEvent(player);
                this.onUpdatePlayerAroundChunk(player);
                // RecordPoint
                room.getCheckpointManager().onUpdate(player);
            }
        }
        if (room.getCheckpointManager().getCheckpointDataList().size() > 0) {
            for (CheckpointData checkPointData : room.getCheckpointManager().getCheckpointDataList()) {
                checkPointData.showParticleMarks(room.getPlayLevels().get(0));
            }
        }
        // Provide methods for other games to use
        for (Consumer<Room> roomConsumer : customConsumerList) {
            roomConsumer.accept(this.room);
        }
        this.onTickDynamicObstacles();
    }

    protected Location getPlayerLastLocation(Player player) {
        return playerLocationHashMap.getOrDefault(player, null);
    }

    public void setPlayerLastLocation(Player player, Location location) {
        playerLocationHashMap.put(player, location);
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
        if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_GameReadyStart && !room.getRoomRule().isAllowReadyStartWalk()) {
            Location from = getPlayerLastLocation(player).clone();
            Location to = player.getLocation();
            if (from.getLevel() != to.getLevel()) {
                setPlayerLastLocation(player, to);
                return;
            }
            if (from.getFloorX() != to.getFloorX() || from.getFloorZ() != to.getFloorZ()) {
                from.yaw = player.yaw;
                from.pitch = player.pitch;
                from.headYaw = player.headYaw;
                player.teleport(from, null);
            }
        }
        // MoveEvent
        RoomPlayerMoveEvent roomPlayerMoveEvent = new RoomPlayerMoveEvent(room, player, getPlayerLastLocation(player), player.getLocation());
        GameListenerRegistry.callEvent(room, roomPlayerMoveEvent);
        if (roomPlayerMoveEvent.isCancelled()) {
            Location from = roomPlayerMoveEvent.getFrom();
            if (from != null) {
                player.teleport(from, null);
            }
        } else {
            setPlayerLastLocation(player, roomPlayerMoveEvent.getTo());
        }
    }

    // todo: Solve issues possibly caused by corrupt chunks?
    protected void onUpdatePlayerAroundChunk(Player player) {
        Level level = player.getLevel();
        int chunkX = player.getChunkX();
        int chunkZ = player.getChunkZ();
        for (int i = chunkX - 1; i <= chunkX + 1; i++) {
            for (int i2 = chunkZ - 1; i2 <= chunkZ + 1; i2++) {
                level.loadChunk(i, i2);
                level.getChunk(i, i2).populateSkyLight();
            }
        }
    }

    protected void onTickDynamicObstacles() {
        for (DynamicObstacle dynamicObstacle : new ArrayList<>(room.getDynamicObstacles())) {
            dynamicObstacle.onTick();
        }
    }
}
