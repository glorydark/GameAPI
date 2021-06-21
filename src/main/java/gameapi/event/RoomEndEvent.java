package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

import java.util.ArrayList;
import java.util.List;

public class RoomEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomEndEvent(Room room){
        RoomEndEvent.room = room;
    }

    public Room getRoom(){ return room;}
}
