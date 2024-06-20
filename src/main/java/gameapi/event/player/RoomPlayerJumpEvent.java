package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerJumpEvent extends RoomPlayerEvent {

    public RoomPlayerJumpEvent(Room room, Player player) {
        super(room, player);
    }

}
