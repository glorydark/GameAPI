package gameapi.event.inventory;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.inventory.Inventory;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomInventoryPickupItemEvent extends RoomInventoryEvent {

    private final EntityItem item;

    public RoomInventoryPickupItemEvent(Room room, Inventory inventory, EntityItem entityItem) {
        super(room, inventory);
        this.item = entityItem;
    }

    public EntityItem getItem() {
        return this.item;
    }
}
