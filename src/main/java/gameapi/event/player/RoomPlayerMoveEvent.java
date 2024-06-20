package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.level.Location;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerMoveEvent extends RoomPlayerEvent implements Cancellable {

    protected Location from;
    protected Location to;

    public RoomPlayerMoveEvent(Room room, Player player, Location from, Location to) {
        super(room, player);
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }
}
