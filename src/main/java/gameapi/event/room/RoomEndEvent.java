package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomEndEvent extends RoomEvent implements Cancellable {

    public RoomEndEvent(Room room) {
        this.room = room;
    }

}
