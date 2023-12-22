package gameapi.listener;


import cn.nukkit.event.HandlerList;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPreStartListener extends RoomEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public RoomPreStartListener(Room room) {
        this.room = room;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Room getRoom() {
        return room;
    }
}
