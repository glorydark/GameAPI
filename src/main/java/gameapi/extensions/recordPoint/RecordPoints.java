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

    private HashMap<Player, RecordPointData> playerRecordPointData;

    public RecordPoints(){
        this.recordPointDataList = new ArrayList<>();
        this.playerRecordPointData = new HashMap<>();
    }

    public RecordPointData getPlayerRecordPoint(Player player){
        return playerRecordPointData.getOrDefault(player, null);
    }

    public void addPlayerRecordPoint(Player player, RecordPointData recordPointData){
        playerRecordPointData.put(player, recordPointData);
    }

    // You must implement this method in RoomPlayerMoveEvent to check the record point
    public void onTick(Player player){
        List<RecordPointData> data = recordPointDataList.stream()
                .filter(recordPointData ->
                        !recordPointData.equals(playerRecordPointData.get(player)) && recordPointData.isInRange(player)
                )
                .collect(Collectors.toList());
        if(data.size() > 0){
            this.addPlayerRecordPoint(player, data.get(0));
        }
    }

}
