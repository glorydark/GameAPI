package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.event.player.RoomPlayerEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerJumpEvent extends RoomPlayerEvent {

    public RoomPlayerJumpEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }

}
