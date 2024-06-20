package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.MovingObjectPosition;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomProjectileHitEvent extends RoomEntityEvent implements Cancellable {

    private MovingObjectPosition movingObjectPosition;

    public RoomProjectileHitEvent(Room room, Entity entity, MovingObjectPosition movingObjectPosition) {
        super(room, entity);
        this.movingObjectPosition = movingObjectPosition;
    }

    public MovingObjectPosition getMovingObjectPosition() {
        return this.movingObjectPosition;
    }

    public void setMovingObjectPosition(MovingObjectPosition movingObjectPosition) {
        this.movingObjectPosition = movingObjectPosition;
    }
}
