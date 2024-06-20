package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomSpectatorJoinEvent extends RoomPlayerEvent implements Cancellable {

    public RoomSpectatorJoinEvent(Room room, Player player) {
        super(room, player);
    }

}
