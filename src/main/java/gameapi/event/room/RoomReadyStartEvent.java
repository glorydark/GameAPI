package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomReadyStartEvent extends RoomEvent implements Cancellable {
    private final Room room;

    public RoomReadyStartEvent(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
