package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerJoinEvent extends RoomPlayerEvent implements Cancellable {

    public RoomPlayerJoinEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }

}
