package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomPlayerRespawnEvent extends RoomPlayerEvent implements Cancellable {

    // default set to null, denoting that players will be teleported automatically by the core.
    Location respawnLocation;

    public RoomPlayerRespawnEvent(Room room, Player player, Location respawnLocation) {
        super(room, player);
        this.respawnLocation = respawnLocation;
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }
}
