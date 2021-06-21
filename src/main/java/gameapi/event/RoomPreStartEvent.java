package gameapi.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomPreStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomPreStartEvent(Room room){
        RoomPreStartEvent.room = room;
    }

    public Room getRoom(){ return room;}
}
