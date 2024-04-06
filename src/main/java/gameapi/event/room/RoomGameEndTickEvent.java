package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public class RoomGameEndTickEvent extends RoomEvent implements Cancellable {
    private final Room room;

    public RoomGameEndTickEvent(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }
}
