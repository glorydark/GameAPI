package gameapi.listener;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public class RoomGameEndListener extends RoomEvent implements Cancellable {
    private final Room room;

    public RoomGameEndListener(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
