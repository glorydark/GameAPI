package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public abstract class RoomEntityEvent extends RoomEvent implements Cancellable {

    protected Entity entity;

    public RoomEntityEvent(Room room, Entity entity) {
        super(room);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
