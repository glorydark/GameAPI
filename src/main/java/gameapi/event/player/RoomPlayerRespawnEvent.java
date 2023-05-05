package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomPlayerRespawnEvent extends RoomPlayerEvent implements Cancellable {

    public RoomPlayerRespawnEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }

}
