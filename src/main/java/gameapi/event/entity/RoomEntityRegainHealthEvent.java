package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomEntityRegainHealthEvent extends RoomEntityEvent implements Cancellable {

    public static final int CAUSE_REGEN = 0;
    public static final int CAUSE_EATING = 1;
    public static final int CAUSE_MAGIC = 2;
    public static final int CAUSE_CUSTOM = 3;
    private static final HandlerList handlers = new HandlerList();
    private final int reason;
    private float amount;

    public RoomEntityRegainHealthEvent(Room room, Entity entity, float amount, int regainReason) {
        super(room, entity);
        this.amount = amount;
        this.reason = regainReason;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getRegainReason() {
        return this.reason;
    }
}
