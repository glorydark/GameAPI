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
public class CheckPoints {

    private List<CheckPointData> checkPointDataList;

    private HashMap<Player, PlayerCheckPointData> playerCheckPointData;

    private int maxLap;

    public CheckPoints() {
        this.checkPointDataList = new ArrayList<>();
        this.playerCheckPointData = new HashMap<>();
        this.maxLap = 1;
    }

    public void clearAllPlayerCheckPointData() {
        playerCheckPointData = new HashMap<>();
    }

    public PlayerCheckPointData getPlayerCheckPointData(Player player) {
        if (!playerCheckPointData.containsKey(player)) {
            playerCheckPointData.put(player, new PlayerCheckPointData());
        }
        return playerCheckPointData.get(player);
    }

    public void updatePlayerRecordPoint(Player player, CheckPointData data) {
        Room room = Room.getRoom(player);
        if (room == null) {
            return;
        }
        int currentLap = getPlayerCheckPointData(player).getLap();
        if (currentLap < maxLap) {
            if (getPlayerCheckPointData(player).addCheckPointData(data)) {
                GameListenerRegistry.callEvent(room, new RoomPlayerCheckRecordPointEvent(player, data));
                player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.pass", data.getName()));
                int newCheckpointNum = checkPointDataList.indexOf(data) + 1;
                if (newCheckpointNum >= checkPointDataList.size()) {
                    if (currentLap + 1 == maxLap) {
                        RoomPlayerFinishAllLapsEvent roomPlayerFinishAllLapsEvent = new RoomPlayerFinishAllLapsEvent(player);
                        GameListenerRegistry.callEvent(room, roomPlayerFinishAllLapsEvent);
                        if (!roomPlayerFinishAllLapsEvent.isCancelled()) {
                            getPlayerCheckPointData(player).setCheckPointDataList(new HashSet<>());
                            getPlayerCheckPointData(player).setLap(currentLap + 1);
                            // player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.laps.finished"));
                        }
                    } else {
                        RoomPlayerFinishLapEvent roomPlayerFinishLapEvent = new RoomPlayerFinishLapEvent(player, currentLap + 1);
                        GameListenerRegistry.callEvent(room, roomPlayerFinishLapEvent);
                        getPlayerCheckPointData(player).setCheckPointDataList(new HashSet<>());
                        getPlayerCheckPointData(player).setLap(currentLap + 1);
                        // player.sendMessage(GameAPI.getLanguage().getTranslation(player, "room.checkpoint.lap.finished", currentLap + 1));
                    }
                }
            }
        }
    }

    public void onUpdate(Player player) {
        if (checkPointDataList.size() == 0) {
            return;
        }
        List<CheckPointData> data = checkPointDataList.stream()
                .filter(checkPointData -> checkPointData.isInRange(player))
                .collect(Collectors.toList());
        if (data.size() > 0) {
            CheckPointData newData = data.get(0);
            this.updatePlayerRecordPoint(player, newData);
        }
    }

}
