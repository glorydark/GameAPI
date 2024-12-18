package gameapi.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public abstract class RoomInventoryEvent extends RoomEvent implements Cancellable {

    protected final Inventory inventory;

    public RoomInventoryEvent(Room room, Inventory inventory) {
        super(room);
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Player[] getViewers() {
        return this.inventory.getViewers().toArray(new Player[0]);
    }
}
