package gameapi.event.entity;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomEntityDeathEvent extends RoomEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private Item[] drops;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomEntityDeathEvent(Room room, EntityLiving entity) {
        this(room, entity, new Item[0]);
    }

    public RoomEntityDeathEvent(Room room, EntityLiving entity, Item[] drops) {
        this.room = room;
        this.entity = entity;
        this.drops = drops;
    }

    public Item[] getDrops() {
        return this.drops;
    }

    public void setDrops(Item[] drops) {
        if (drops == null) {
            drops = new Item[0];
        }

        this.drops = drops;
    }

}
