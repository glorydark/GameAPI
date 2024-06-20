package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/30} {19:59}
 */
public class RoomEntityExplodeEvent extends RoomEntityEvent implements Cancellable {

    private double force;

    public RoomEntityExplodeEvent(Room room, Entity entity, double force) {
        super(room, entity);
        this.force = force;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }
}
