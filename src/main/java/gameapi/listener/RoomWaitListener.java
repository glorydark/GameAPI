package gameapi.listener;


import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomWaitListener extends RoomEvent implements Cancellable {
    private final Room room;

    public RoomWaitListener(Room room){
        this.room = room;
    }

    public Room getRoom(){ return room;}
}
