package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomProjectileLaunchEvent extends RoomEntityEvent implements Cancellable {

    public RoomProjectileLaunchEvent(Room room, Entity entity) {
        super(room, entity);
    }
}
