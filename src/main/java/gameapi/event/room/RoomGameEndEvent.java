package gameapi.event.room;

import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomGameEndEvent extends RoomEvent {

    public RoomGameEndEvent(Room room) {
        this.room = room;
    }

}
