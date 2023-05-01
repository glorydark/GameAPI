package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;

public abstract class RoomPlayerEvent extends RoomEvent implements Cancellable {
    protected Player player;

    public RoomPlayerEvent() {
    }

    public Player getPlayer() {
        return player;
    }
}
