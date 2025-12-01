package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomPlayerMissedSwingEvent extends RoomPlayerEvent {

    public RoomPlayerMissedSwingEvent(Room room, Player player) {
        super(room, player);
    }
}
