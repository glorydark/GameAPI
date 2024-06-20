package gameapi.event.room;

import cn.nukkit.Player;
import gameapi.event.player.RoomPlayerEvent;
import gameapi.extensions.checkpoint.CheckpointData;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/31} {18:02}
 */
public class RoomPlayerReachCheckpointEvent extends RoomPlayerEvent {

    private final CheckpointData checkPointData;

    public RoomPlayerReachCheckpointEvent(Room room, Player player, CheckpointData checkPointData) {
        super(room, player);
        this.checkPointData = checkPointData;
    }

    public CheckpointData getCheckPointData() {
        return checkPointData;
    }
}
