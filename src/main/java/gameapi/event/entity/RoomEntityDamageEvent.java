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
    private float damage;
    protected EntityDamageEvent sourceEvent;

    public RoomEntityDamageEvent(Room room, Entity entity, EntityDamageEvent.DamageCause cause, final float damage, EntityDamageEvent sourceEvent) {
        super(room, entity);
        this.cause = cause;
        this.damage = damage;
        this.sourceEvent = sourceEvent;
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
        this.damage = damage;
    }

    public EntityDamageEvent getSourceEvent() {
        return sourceEvent;
    }
}