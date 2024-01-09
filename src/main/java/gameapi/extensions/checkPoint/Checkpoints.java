package gameapi.extensions.checkPoint;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.event.room.RoomPlayerCheckRecordPointEvent;
import gameapi.event.room.RoomPlayerFinishAllLapsEvent;
import gameapi.event.room.RoomPlayerFinishLapEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Checkpoints {

    private List<CheckpointData> checkpointDataList;

    private HashMap<Player, PlayerCheckpointData> playerCheckpointData;

    private int minFinishCheckpoint = -1;

    private int maxLap;

    private CheckpointData endPoint;

    public Checkpoints() {
        this.checkpointDataList = new ArrayList<>();
        this.playerCheckpointData = new HashMap<>();
        this.maxLap = 1;
    }

    public void clearAllPlayerCheckPointData() {
        playerCheckpointData = new HashMap<>();
    }

    public PlayerCheckpointData getPlayerCheckpointData(Player player) {
        if (!playerCheckpointData.containsKey(player)) {
            playerCheckpointData.put(player, new PlayerCheckpointData());
        }
        return playerCheckpointData.get(player);
    }

    public void updatePlayerRecordPoint(Player player, CheckpointData data) {
        Room room = Room.getRoom(player);
        if (room == null) {
            return;
        }
        int currentLap = getPlayerCheckpointData(player).getLap();
        if (currentLap < maxLap) {
            if (getPlayerCheckpointData(player).addCheckPointData(data)) {
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.pass", data.getName()));
                GameListenerRegistry.callEvent(room, new RoomPlayerCheckRecordPointEvent(room, player, data));
            }
        }
    }

    public void updatePlayerEndPoint(Player player) {
        Room room = Room.getRoom(player);
        if (room == null) {
            return;
        }
        int currentLap = getPlayerCheckpointData(player).getLap();
        if (currentLap < maxLap) {
            int minCount = Math.max(minFinishCheckpoint, 0);
            if (getPlayerCheckpointData(player).getCheckpointDataList().size() < minCount) {
                return;
            }
            if (currentLap + 1 == maxLap) {
                RoomPlayerFinishAllLapsEvent roomPlayerFinishAllLapsEvent = new RoomPlayerFinishAllLapsEvent(room, player);
                GameListenerRegistry.callEvent(room, roomPlayerFinishAllLapsEvent);
                if (!roomPlayerFinishAllLapsEvent.isCancelled()) {
                    getPlayerCheckpointData(player).setCheckpointDataList(new HashSet<>());
                    getPlayerCheckpointData(player).setLap(currentLap + 1);
                    // player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.laps.finished"));
                }
            } else {
                RoomPlayerFinishLapEvent roomPlayerFinishLapEvent = new RoomPlayerFinishLapEvent(room, player, currentLap + 1);
                GameListenerRegistry.callEvent(room, roomPlayerFinishLapEvent);
                getPlayerCheckpointData(player).setCheckpointDataList(new HashSet<>());
                getPlayerCheckpointData(player).setLap(currentLap + 1);
                // player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.lap.finished", currentLap + 1));
            }
        }
    }

    public void onUpdate(Player player) {
        List<CheckpointData> data = checkpointDataList.stream()
                .filter(checkpointData -> checkpointData.isInRange(player))
                .collect(Collectors.toList());
        if (data.size() > 0) {
            CheckpointData newData = data.get(0);
            this.updatePlayerRecordPoint(player, newData);
        }
        if (endPoint != null && endPoint.isInRange(player)) {
            this.updatePlayerEndPoint(player);
        }
    }

}
