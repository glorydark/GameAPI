package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/30} {19:59}
 */
public class RoomExplodePrimeEvent extends RoomEntityEvent implements Cancellable {

    private double force;

    private boolean blockBreaking;

    public RoomExplodePrimeEvent(Room room, Entity entity, double force, boolean blockBreaking) {
        super(room, entity);
        this.force = force;
        this.blockBreaking = blockBreaking;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public boolean isBlockBreaking() {
        return blockBreaking;
    }

    public void setBlockBreaking(boolean blockBreaking) {
        this.blockBreaking = blockBreaking;
    }
}
