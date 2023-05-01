package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomEndEvent extends RoomEvent implements Cancellable {
    private final Room room;

    public RoomEndEvent(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
