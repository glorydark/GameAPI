package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerToggleSprintEvent extends RoomPlayerEvent implements Cancellable {

    protected final boolean isSprinting;

    public RoomPlayerToggleSprintEvent(Room room, Player player, boolean isSprinting) {
        super(room, player);
        this.isSprinting = isSprinting;
    }

    public boolean isSprinting() {
        return isSprinting;
    }
}
