package gameapi.event.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityVehicle;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomEntityVehicleEnterEvent extends RoomVehicleEvent {

    private final Entity riding;

    public RoomEntityVehicleEnterEvent(Room room, Entity riding, EntityVehicle vehicleEntity) {
        super(room, vehicleEntity);
        this.riding = riding;
    }

    public Entity getEntity() {
        return riding;
    }

    public boolean isPlayer() {
        return riding instanceof Player;
    }
}
