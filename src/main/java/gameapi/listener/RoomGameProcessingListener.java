package gameapi.listener;

import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomGameProcessingListener extends RoomEvent implements Cancellable {
    private final Room room;

    public RoomGameProcessingListener(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }
}
