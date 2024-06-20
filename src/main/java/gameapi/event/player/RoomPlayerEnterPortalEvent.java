package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/24} {21:13}
 */
public class RoomPlayerEnterPortalEvent extends RoomPlayerEvent {

    public RoomPlayerEnterPortalEvent(Room room, Player player) {
        super(room, player);
    }

}
