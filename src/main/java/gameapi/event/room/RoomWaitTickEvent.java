package gameapi.event.room;


import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomWaitTickEvent extends RoomEvent implements Cancellable {

    public RoomWaitTickEvent(Room room) {
        super(room);
    }
}
