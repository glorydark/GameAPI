package gameapi.event.room;

import cn.nukkit.Player;
import gameapi.event.player.RoomPlayerEvent;
import gameapi.extensions.checkPoint.CheckpointData;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/31} {18:02}
 */
public class RoomPlayerCheckRecordPointEvent extends RoomPlayerEvent {

    private final CheckpointData checkPointData;

    public RoomPlayerCheckRecordPointEvent(Room room, Player player, CheckpointData checkPointData) {
        this.room = room;
        this.player = player;
        this.checkPointData = checkPointData;
    }

    public CheckpointData getCheckPointData() {
        return checkPointData;
    }
}
