package gameapi.event.room;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomNextRoundPreStartEvent extends RoomEvent implements Cancellable {

    public RoomNextRoundPreStartEvent(Room room) {
        this.room = room;
    }

}
