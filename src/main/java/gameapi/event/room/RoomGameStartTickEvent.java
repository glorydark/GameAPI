package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomGameStartTickEvent extends RoomEvent implements Cancellable {

    public RoomGameStartTickEvent(Room room) {
        super(room);
    }
}
