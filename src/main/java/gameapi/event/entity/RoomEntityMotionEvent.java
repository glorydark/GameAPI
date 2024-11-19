package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.math.Vector3;
import gameapi.room.Room;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class RoomEntityMotionEvent extends RoomEntityEvent implements Cancellable {

    private final Vector3 motion;

    public RoomEntityMotionEvent(Room room, Entity entity, Vector3 motion) {
        super(room, entity);
        this.motion = motion;
    }

    public Vector3 getVector() {
        return this.motion;
    }

    public Vector3 getMotion() {
        return this.motion;
    }
}
