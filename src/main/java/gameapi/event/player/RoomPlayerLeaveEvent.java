package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerLeaveEvent extends RoomPlayerEvent implements Cancellable {
    private final Room room;
    private final Player player;

    public RoomPlayerLeaveEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }

    public Room getRoom(){ return room;}

    public Player getPlayer() {
        return this.player;
    }
}
