package gameapi.event.room;


import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public class RoomNextRoundPreStartTickEvent extends RoomEvent implements Cancellable {

    public RoomNextRoundPreStartTickEvent(Room room) {
        super(room);
    }

    public Room getRoom() {
        return room;
    }
}
