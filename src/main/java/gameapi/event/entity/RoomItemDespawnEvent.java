package gameapi.event.entity;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.Cancellable;
import gameapi.room.Room;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class RoomItemDespawnEvent extends RoomEntityEvent implements Cancellable {

    public RoomItemDespawnEvent(Room room, EntityItem item) {
        super(room, item);
    }

    @Override
    public EntityItem getEntity() {
        return (EntityItem) this.entity;
    }
}
