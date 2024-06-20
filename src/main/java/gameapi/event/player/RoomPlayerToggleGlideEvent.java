package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerToggleGlideEvent extends RoomPlayerEvent implements Cancellable {

    protected final boolean isGliding;

    public RoomPlayerToggleGlideEvent(Room room, Player player, boolean isGliding) {
        super(room, player);
        this.isGliding = isGliding;
    }

    public boolean isGliding() {
        return isGliding;
    }

}
