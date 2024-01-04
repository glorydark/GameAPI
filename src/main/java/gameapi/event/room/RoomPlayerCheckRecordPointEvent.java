package gameapi.event.room;

import cn.nukkit.Player;
import gameapi.event.player.RoomPlayerEvent;
import gameapi.extensions.checkPoint.CheckPointData;

/**
 * @author glorydark
 * @date {2023/12/31} {18:02}
 */
public class RoomPlayerCheckRecordPointEvent extends RoomPlayerEvent {

    private final CheckPointData checkPointData;

    public RoomPlayerCheckRecordPointEvent(Player player, CheckPointData checkPointData) {
        this.player = player;
        this.checkPointData = checkPointData;
    }

    public CheckPointData getCheckPointData() {
        return checkPointData;
    }
}
