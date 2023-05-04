package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomCeremonyEvent extends RoomEvent implements Cancellable {

    public RoomCeremonyEvent(Room room){
        this.room = room;
    }

}
