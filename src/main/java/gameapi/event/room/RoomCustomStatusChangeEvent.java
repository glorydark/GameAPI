package gameapi.event.room;

import gameapi.event.RoomEvent;
import gameapi.room.Room;
import gameapi.room.status.base.CustomRoomStatus;

/**
 * @author Glorydark
 */
public class RoomCustomStatusChangeEvent extends RoomEvent {

    private final CustomRoomStatus previousRoomStatus;

    private final CustomRoomStatus currentRoomStatus;

    public RoomCustomStatusChangeEvent(Room room, CustomRoomStatus previousRoomStatus, CustomRoomStatus currentRoomStatus) {
        super(room);
        this.previousRoomStatus = previousRoomStatus;
        this.currentRoomStatus = currentRoomStatus;
    }

    public CustomRoomStatus getCurrentRoomStatus() {
        return currentRoomStatus;
    }

    public CustomRoomStatus getPreviousRoomStatus() {
        return previousRoomStatus;
    }
}
