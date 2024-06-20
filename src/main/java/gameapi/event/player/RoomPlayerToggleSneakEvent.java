package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerToggleSneakEvent extends RoomPlayerEvent implements Cancellable {

    protected final boolean isSneaking;

    public RoomPlayerToggleSneakEvent(Room room, Player player, boolean isSneaking) {
        super(room, player);
        this.isSneaking = isSneaking;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
