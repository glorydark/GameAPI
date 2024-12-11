package gameapi.event.inventory;

import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.Cancellable;
import cn.nukkit.inventory.Inventory;
import gameapi.room.Room;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class RoomInventoryPickupArrowEvent extends RoomInventoryEvent implements Cancellable {

    private final EntityArrow arrow;

    public RoomInventoryPickupArrowEvent(Room room, Inventory inventory, EntityArrow arrow) {
        super(room, inventory);
        this.arrow = arrow;
    }

    public EntityArrow getArrow() {
        return arrow;
    }
}