package gameapi.event.room;


import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomReadyStartTickEvent extends RoomEvent implements Cancellable {

    public RoomReadyStartTickEvent(Room room) {
        super(room);
    }
}
