package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/24} {20:09}
 */
public class RoomEntityDamageEvent extends RoomEntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final EntityDamageEvent.DamageCause cause;
    private final float damage;

    public RoomEntityDamageEvent(Room room, Entity entity, EntityDamageEvent.DamageCause cause, final float damage) {
        this.room = room;
        this.entity = entity;
        this.cause = cause;
        this.damage = damage;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return this.cause;
    }


    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.setDamage(damage);
    }
}