package gameapi.event.room;

import gameapi.event.RoomEvent;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomGameEndEvent extends RoomEvent {

    public static final String DEFAULT_REASON = "default";

    private String reason;

    public RoomGameEndEvent(Room room) {
        this(room, DEFAULT_REASON);
    }

    public RoomGameEndEvent(Room room, String reason) {
        super(room);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
