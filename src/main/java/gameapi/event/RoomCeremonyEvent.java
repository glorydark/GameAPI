package gameapi.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomCeremonyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomCeremonyEvent(Room room){
        RoomCeremonyEvent.room = room;
    }

    public Room getRoom(){ return room;}
}
