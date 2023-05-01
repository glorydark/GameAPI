package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public class RoomGameStartEvent extends RoomEvent implements Cancellable {
    private final Room room;

    public RoomGameStartEvent(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
