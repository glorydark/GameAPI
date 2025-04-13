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

    private final long timeDiff;

    public RoomPlayerReachCheckpointEvent(Room room, Player player, CheckpointData checkPointData, long timeDiff) {
        super(room, player);
        this.checkPointData = checkPointData;
        this.timeDiff = timeDiff;
    }

    public CheckpointData getCheckPointData() {
        return checkPointData;
    }

    public long getTimeDiff() {
        return timeDiff;
    }
}
