package gameapi.event.room;


import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomCeremonyTickEvent extends RoomEvent implements Cancellable {

    public RoomCeremonyTickEvent(Room room) {
        super(room);
    }
}
