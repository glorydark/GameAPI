package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public abstract class RoomPlayerEvent extends RoomEvent implements Cancellable {

    protected Player player;

    public RoomPlayerEvent(Room room, Player player) {
        super(room);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
