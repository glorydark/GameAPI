package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomPlayerRespawnEvent extends RoomPlayerEvent implements Cancellable {
    private final Room room;
    private final Player player;

    public RoomPlayerRespawnEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }

    public Room getRoom(){ return room;}

    public Player getPlayer() {
        return this.player;
    }
}
