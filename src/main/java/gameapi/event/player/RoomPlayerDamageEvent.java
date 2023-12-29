package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityDamageEvent;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/24} {20:09}
 */
public class RoomPlayerDamageEvent extends RoomPlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final EntityDamageEvent.DamageCause cause;
    private final float damage;

    public RoomPlayerDamageEvent(Room room, Player player, EntityDamageEvent.DamageCause cause, final float damage) {
        this.room = room;
        this.player = player;
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