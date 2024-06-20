package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomSpectatorLeaveEvent extends RoomPlayerEvent implements Cancellable {
    protected Location returnLocation;

    public RoomSpectatorLeaveEvent(Room room, Player player, Location returnLocation) {
        super(room, player);
        this.returnLocation = returnLocation;
    }

    public Location getReturnLocation() {
        return returnLocation;
    }
}
