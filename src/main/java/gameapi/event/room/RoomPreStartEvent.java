package gameapi.event.room;

import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPreStartEvent extends RoomEvent {

    public RoomPreStartEvent(Room room) {
        super(room);
    }
}
