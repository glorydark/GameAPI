package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityVehicle;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public abstract class RoomVehicleEvent extends RoomEvent implements Cancellable {

    protected EntityVehicle vehicle;

    public RoomVehicleEvent(Room room, EntityVehicle vehicle) {
        super(room);
        this.vehicle = vehicle;
    }

    public Entity getVehicle() {
        return vehicle;
    }
}
