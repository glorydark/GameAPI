package gameapi.event.room;


import cn.nukkit.event.HandlerList;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPreStartTickEvent extends RoomEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public RoomPreStartTickEvent(Room room) {
        super(room);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
