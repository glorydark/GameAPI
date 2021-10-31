package gameapi.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

public class RoomReadyStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomReadyStartEvent(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
