package gameapi.extensions.recordPoint;

import cn.nukkit.Player;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecordPoints {

    private List<RecordPointData> recordPointDataList;

    private HashMap<Player, Integer> playerRecordPointNumbers;

    public RecordPoints() {
        this.recordPointDataList = new ArrayList<>();
        this.playerRecordPointNumbers = new HashMap<>();
    }

    public Integer getPlayerRecordPoint(Player player) {
        return playerRecordPointNumbers.getOrDefault(player, 0);
    }

    public void updatePlayerRecordPoint(Player player, RecordPointData recordPointData) {
        int num = recordPointDataList.indexOf(recordPointData) + 1;
        if (num >= getPlayerRecordPoint(player)) {
            playerRecordPointNumbers.put(player, num);
        }
    }

    // You must implement this method in RoomPlayerMoveEvent to check the record point
    public void onUpdate(Player player) {
        List<RecordPointData> data = recordPointDataList.stream()
                .filter(recordPointData -> recordPointData.isInRange(player))
                .collect(Collectors.toList());
        if (data.size() > 0) {
            RecordPointData newData = data.get(0);
            this.updatePlayerRecordPoint(player, newData);
        }
    }

}
