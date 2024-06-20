package gameapi.event.room;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.event.player.RoomPlayerEvent;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/31} {18:02}
 */
public class RoomPlayerFinishAllLapsEvent extends RoomPlayerEvent implements Cancellable {

    public RoomPlayerFinishAllLapsEvent(Room room, Player player) {
        super(room, player);
    }
}
