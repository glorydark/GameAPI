package gameapi.manager.room;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.event.room.RoomPlayerFinishAllLapsEvent;
import gameapi.event.room.RoomPlayerFinishLapEvent;
import gameapi.event.room.RoomPlayerReachCheckpointEvent;
import gameapi.extensions.checkpoint.CheckpointData;
import gameapi.extensions.checkpoint.PlayerCheckpointData;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class CheckpointManager {

    private List<CheckpointData> checkpointDataList = new ArrayList<>();

    private Map<Player, PlayerCheckpointData> playerCheckpointData = new LinkedHashMap<>();

    private int minFinishCheckpoint = -1;

    private int maxLap = 1;

    private CheckpointData endPoint = null;

    private final Room room;

    public CheckpointManager(Room room) {
        this.room = room;
    }

    public void clearAllPlayerCheckPointData() {
        this.playerCheckpointData = new LinkedHashMap<>();
    }

    public PlayerCheckpointData getPlayerCheckpointData(Player player) {
        if (!this.playerCheckpointData.containsKey(player)) {
            this.playerCheckpointData.put(player, new PlayerCheckpointData());
        }
        return this.playerCheckpointData.get(player);
    }

    public void updatePlayerRecordPoint(Player player, CheckpointData data) {
        int currentLap = this.getPlayerCheckpointData(player).getLap();
        if (currentLap < this.maxLap) {
            if (this.getPlayerCheckpointData(player).addCheckPointData(data)) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.pass", data.getName()));
                GameListenerRegistry.callEvent(room, new RoomPlayerReachCheckpointEvent(room, player, data));
            }
        }
    }

    public void updatePlayerEndPoint(Player player) {
        if (this.getPlayerCheckpointData(player).addCheckPointData(this.endPoint)) {
            player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.pass", this.endPoint.getName()));
        }
        int currentLap = this.getPlayerCheckpointData(player).getLap();
        if (currentLap < this.maxLap) {
            int minCount = Math.max(this.minFinishCheckpoint, 0);
            if (this.getPlayerCheckpointData(player).getCheckpointDataList().size() < minCount) {
                return;
            }
            if (currentLap + 1 == this.maxLap) {
                RoomPlayerFinishAllLapsEvent roomPlayerFinishAllLapsEvent = new RoomPlayerFinishAllLapsEvent(this.room, player);
                GameListenerRegistry.callEvent(this.room, roomPlayerFinishAllLapsEvent);
                if (!roomPlayerFinishAllLapsEvent.isCancelled()) {
                    if (this.maxLap > 1) {
                        this.getPlayerCheckpointData(player).setCheckpointDataList(new ArrayList<>());
                    }
                    this.getPlayerCheckpointData(player).setLap(currentLap + 1);
                    // player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.laps.finished"));
                }
            } else {
                RoomPlayerFinishLapEvent roomPlayerFinishLapEvent = new RoomPlayerFinishLapEvent(this.room, player, currentLap + 1);
                GameListenerRegistry.callEvent(this.room, roomPlayerFinishLapEvent);
                this.getPlayerCheckpointData(player).setCheckpointDataList(new ArrayList<>());
                this.getPlayerCheckpointData(player).setLap(currentLap + 1);
                // player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.lap.finished", currentLap + 1));
            }
        }
    }

    public void onUpdate(Player player) {
        if (this.getCheckpointDataList().isEmpty() && this.getEndPoint() == null) {
            return;
        }
        if (this.room != null && this.room.getRoomStatus() == RoomStatus.ROOM_STATUS_START) {
            if (!this.room.getPlayLevels().contains(player.getLevel())) {
                return;
            }
            List<CheckpointData> data = this.checkpointDataList.stream()
                    .filter(checkpointData -> checkpointData.isInRange(player))
                    .collect(Collectors.toList());
            if (!data.isEmpty()) {
                CheckpointData newData = data.get(0);
                this.updatePlayerRecordPoint(player, newData);
            }
            if (this.room.getPlayLevels().contains(player.getLevel()) && this.endPoint != null && this.endPoint.isInRange(player)) {
                this.updatePlayerEndPoint(player);
            }
        }
    }

    public Room getRoom() {
        return this.room;
    }
}
