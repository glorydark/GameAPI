package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;

public abstract class RoomEntityEvent extends RoomEvent implements Cancellable {

    protected Entity entity;

    public RoomEntityEvent() {
    }

    public Entity getEntity() {
        return entity;
    }
}
