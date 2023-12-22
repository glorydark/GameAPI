package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerJoinEvent extends RoomPlayerEvent {

    public RoomPlayerJoinEvent(Room room, Player player) {
        this.room = room;
        this.player = player;
    }

}
