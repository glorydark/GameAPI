package gameapi.event.room;

import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomNextRoundPreStartEvent extends RoomEvent {

    public RoomNextRoundPreStartEvent(Room room) {
        this.room = room;
    }

}
