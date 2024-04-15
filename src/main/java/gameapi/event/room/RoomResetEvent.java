package gameapi.event.room;

import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomResetEvent extends RoomEvent {

    public RoomResetEvent(Room room) {
        this.room = room;
    }

}
