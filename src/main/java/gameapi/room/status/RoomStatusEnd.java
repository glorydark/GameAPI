package gameapi.room.status;

import gameapi.room.Room;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusEnd extends InternalRoomStatus {

    public RoomStatusEnd() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_ROOM_END_ID, false);
    }

    @Override
    public void onEnter(Room room) {
        room.resetAll(ResetAllReason.ROOM_GAME_FINISH);
    }

    @Override
    public int getTime(Room room) {
        return 0;
    }
}