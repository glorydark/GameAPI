package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public class RoomGameStartEvent extends RoomEvent implements Cancellable {

    public RoomGameStartEvent(Room room) {
        this.room = room;
    }

}
