package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomReadyStartEvent extends RoomEvent implements Cancellable {

    public RoomReadyStartEvent(Room room) {
        this.room = room;
    }

}
