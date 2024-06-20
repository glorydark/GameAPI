package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public class RoomGameEndTickEvent extends RoomEvent implements Cancellable {

    public RoomGameEndTickEvent(Room room) {
        super(room);
    }
}
