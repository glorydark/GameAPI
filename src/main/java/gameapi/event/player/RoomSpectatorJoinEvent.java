package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomSpectatorJoinEvent extends RoomPlayerEvent implements Cancellable {

    public Location teleportLocation;

    public RoomSpectatorJoinEvent(Room room, Player player, Location teleportLocation) {
        super(room, player);
        this.teleportLocation = teleportLocation;
    }

    public Location getTeleportLocation() {
        return teleportLocation;
    }

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }
}
