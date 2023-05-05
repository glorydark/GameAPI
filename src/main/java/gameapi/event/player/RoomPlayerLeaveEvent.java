package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerLeaveEvent extends RoomPlayerEvent implements Cancellable {

    public RoomPlayerLeaveEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }

}
