package gameapi.event.room;

import gameapi.event.RoomEvent;
import gameapi.room.Room;

public class RoomGameStartEvent extends RoomEvent {

    public RoomGameStartEvent(Room room) {
        super(room);
    }
}
