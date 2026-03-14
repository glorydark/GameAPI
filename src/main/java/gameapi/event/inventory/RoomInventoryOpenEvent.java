package gameapi.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomInventoryOpenEvent extends RoomInventoryEvent {

    private final Player who;

    public RoomInventoryOpenEvent(Room room, Player player, Inventory inventory) {
        super(room, inventory);
        this.who = player;
    }

    public Player getWho() {
        return who;
    }
}
